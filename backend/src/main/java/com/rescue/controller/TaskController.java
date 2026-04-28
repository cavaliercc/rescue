package com.rescue.controller;

import com.rescue.dto.ApiResponse;
import com.rescue.dto.task.TaskStartRequest;
import com.rescue.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/start")
    public ApiResponse<TaskService.StartResult> start(@RequestBody Map<String, Object> body) {
        TaskStartRequest req = new TaskStartRequest();
        req.setCaseId((String) body.get("caseId"));
        req.setWorkId((String) body.get("workId"));
        req.setSpeed(body.get("speed") instanceof Number n ? n.doubleValue() : 60.0);
        req.setInterval(body.get("interval") instanceof Number n ? n.intValue() : 30);
        req.setReportTaskId((String) body.get("reportTaskId"));
        req.setHandleUserId((String) body.get("handleUserId"));
        req.setHandleOrgId((String) body.get("handleOrgId"));
        req.setHandleUserName((String) body.get("handleUserName"));
        req.setCaseFromOrgId(body.get("caseFromOrgId") instanceof Number n ? n.intValue() : null);
        req.setDeviceToken((String) body.get("deviceToken"));

        @SuppressWarnings("unchecked")
        List<Map<String, String>> rawPoints = (List<Map<String, String>>) body.get("routePoints");

        List<TaskService.RoutePoint> points = new ArrayList<>();
        if (rawPoints != null) {
            for (Map<String, String> p : rawPoints) {
                points.add(new TaskService.RoutePoint(p.get("lon"), p.get("lat")));
            }
        }

        TaskService.StartResult result = taskService.start(req, points);
        return ApiResponse.ok(result);
    }

    @PostMapping("/pause")
    public ApiResponse<Void> pause() {
        taskService.pause();
        return ApiResponse.ok(null);
    }

    @PostMapping("/resume")
    public ApiResponse<Void> resume() {
        taskService.resume();
        return ApiResponse.ok(null);
    }

    @PostMapping("/stop")
    public ApiResponse<Void> stop() {
        taskService.stop();
        return ApiResponse.ok(null);
    }

    @GetMapping("/status")
    public ApiResponse<TaskService.TaskStatus> status() {
        return ApiResponse.ok(taskService.getStatus());
    }

    @GetMapping("/logs")
    public ApiResponse<Map<String, Object>> logs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.ok(taskService.getLogs(page, limit));
    }
}
