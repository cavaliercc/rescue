package com.rescue.service;

import com.rescue.dto.task.TaskStartRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── State ────────────────────────────────────────────────────────────────

    public enum TaskState { idle, running, paused, stopped, completed }

    @Data
    public static class RoutePoint {
        private final String lon;
        private final String lat;
    }

    @Data
    public static class TaskStatus {
        private String taskId;
        private TaskState state;
        private int reportedCount;
        private int totalPoints;
        private int progress;
        private int remainingTime;
        private RoutePoint currentPosition;
        private String lastReportTime;
        private boolean lastReportSuccess;
    }

    @Data
    public static class ReportLog {
        private int seq;
        private String timestamp;
        private String lon;
        private String lat;
        private boolean success;
        private int responseCode;
        private String responseMsg;
    }

    @Data
    public static class StartResult {
        private String taskId;
        private int totalPoints;
        private int estimatedDuration;
        private int routeDistance;
    }

    private final ExternalRescueApiService externalApi;

    // ── Internal task context ─────────────────────────────────────────────────

    private volatile TaskState state = TaskState.idle;
    private volatile String taskId;
    private volatile String caseId;
    private volatile String workId;
    private volatile String reportTaskId;
    private volatile String handleUserId;
    private volatile String handleOrgId;
    private volatile String handleUserName;
    private volatile Integer caseFromOrgId;
    private volatile String deviceToken;
    private volatile List<RoutePoint> points = Collections.emptyList();
    private volatile int intervalSeconds;
    private final AtomicInteger cursor = new AtomicInteger(0);
    private final List<ReportLog> logs = new CopyOnWriteArrayList<>();
    private volatile ReportLog lastLog;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile ScheduledFuture<?> reportFuture;

    // ── Public API ────────────────────────────────────────────────────────────

    public synchronized StartResult start(TaskStartRequest req, List<RoutePoint> routePoints) {
        if (state == TaskState.running || state == TaskState.paused) {
            throw new IllegalStateException("任务正在进行中，请先停止");
        }

        taskId = "TASK_" + System.currentTimeMillis();
        caseId = req.getCaseId();
        workId = req.getWorkId();
        reportTaskId = req.getReportTaskId();
        handleUserId = req.getHandleUserId();
        handleOrgId = req.getHandleOrgId();
        handleUserName = req.getHandleUserName();
        caseFromOrgId = req.getCaseFromOrgId();
        deviceToken = req.getDeviceToken();
        points = new ArrayList<>(routePoints);
        intervalSeconds = req.getInterval();
        cursor.set(0);
        logs.clear();
        lastLog = null;
        state = TaskState.running;

        scheduleNext();

        int total = points.size();
        int estimatedDuration = total * intervalSeconds;

        StartResult result = new StartResult();
        result.setTaskId(taskId);
        result.setTotalPoints(total);
        result.setEstimatedDuration(estimatedDuration);
        result.setRouteDistance(0);
        return result;
    }

    public synchronized void pause() {
        if (state != TaskState.running) throw new IllegalStateException("任务未在运行");
        state = TaskState.paused;
        cancelFuture();
    }

    public synchronized void resume() {
        if (state != TaskState.paused) throw new IllegalStateException("任务未暂停");
        state = TaskState.running;
        scheduleNext();
    }

    public synchronized void stop() {
        cancelFuture();
        state = TaskState.stopped;
    }

    public TaskStatus getStatus() {
        TaskStatus s = new TaskStatus();
        s.setTaskId(taskId);
        s.setState(state);

        int reported = logs.size();
        int total = points.size();
        s.setReportedCount(reported);
        s.setTotalPoints(total);
        s.setProgress(total > 0 ? (int) ((reported * 100.0) / total) : 0);

        int remaining = total > reported ? (total - reported) * intervalSeconds : 0;
        s.setRemainingTime(remaining);

        int idx = Math.max(0, cursor.get() - 1);
        if (!points.isEmpty() && idx < points.size()) {
            s.setCurrentPosition(points.get(idx));
        }

        if (lastLog != null) {
            s.setLastReportTime(lastLog.getTimestamp());
            s.setLastReportSuccess(lastLog.isSuccess());
        }
        return s;
    }

    public Map<String, Object> getLogs(int page, int limit) {
        List<ReportLog> all = new ArrayList<>(logs);
        Collections.reverse(all);
        int total = all.size();
        int from = Math.min((page - 1) * limit, total);
        int to = Math.min(from + limit, total);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("logs", all.subList(from, to));
        return result;
    }

    // ── Internal ─────────────────────────────────────────────────────────────

    private void scheduleNext() {
        reportFuture = scheduler.schedule(this::reportTick, intervalSeconds, TimeUnit.SECONDS);
    }

    private void reportTick() {
        if (state != TaskState.running) return;

        int idx = cursor.getAndIncrement();
        if (idx >= points.size()) {
            state = TaskState.completed;
            return;
        }

        RoutePoint point = points.get(idx);
        boolean success = true;
        String msg = "ok";

        try {
            double lon = Double.parseDouble(point.getLon());
            double lat = Double.parseDouble(point.getLat());
            Map<String, Object> resp = externalApi.reportLocation(
                    caseId, reportTaskId, lon, lat,
                    handleUserId, handleOrgId, handleUserName,
                    caseFromOrgId, deviceToken);
            log.info("上报位置 [{}/{}] lon={} lat={} resp={}", idx + 1, points.size(), point.getLon(), point.getLat(), resp);
            Object respCode = resp != null ? resp.get("code") : null;
            if (respCode != null && !respCode.toString().equals("200") && !respCode.toString().equals("0")) {
                success = false;
                msg = resp.get("msg") != null ? resp.get("msg").toString() : "上报失败";
            }
        } catch (Exception e) {
            success = false;
            msg = e.getMessage();
        }

        ReportLog entry = new ReportLog();
        entry.setSeq(idx + 1);
        entry.setTimestamp(LocalDateTime.now().format(FMT));
        entry.setLon(point.getLon());
        entry.setLat(point.getLat());
        entry.setSuccess(success);
        entry.setResponseCode(success ? 200 : 500);
        entry.setResponseMsg(msg);
        logs.add(entry);
        lastLog = entry;

        if (state == TaskState.running && cursor.get() < points.size()) {
            scheduleNext();
        } else if (cursor.get() >= points.size()) {
            state = TaskState.completed;
        }
    }

    private void cancelFuture() {
        if (reportFuture != null && !reportFuture.isDone()) {
            reportFuture.cancel(false);
        }
    }
}
