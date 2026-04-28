package com.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.dto.rescue.DispatchTechnicianRequest;
import com.rescue.dto.rescue.OrderCallbackDTO;
import com.rescue.dto.rescue.TechnicianActionRequest;
import com.rescue.entity.RescueCase;
import com.rescue.mapper.RescueCaseMapper;
import com.rescue.service.ExternalRescueApiService;
import com.rescue.service.RescueCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RescueCaseServiceImpl implements RescueCaseService {

    private final RescueCaseMapper rescueCaseMapper;
    private final ExternalRescueApiService externalApi;

    @Override
    public void handleOrderCallback(OrderCallbackDTO dto, String rawJson) {
        RescueCase existing = findByCaseId(dto.getCaseId());
        if (existing == null) {
            RescueCase rescueCase = buildFromCallback(dto, rawJson);
            rescueCase.setCreatedAt(LocalDateTime.now());
            rescueCase.setUpdatedAt(LocalDateTime.now());
            rescueCaseMapper.insert(rescueCase);
            log.info("New rescue case created: {}", dto.getCaseId());
        } else {
            updateFromCallback(existing, dto, rawJson);
            rescueCaseMapper.updateById(existing);
            log.info("Rescue case updated: {} -> status {}", dto.getCaseId(), dto.getCaseStatus());
        }
    }

    @Override
    public Page<RescueCase> pageList(int page, int size, String keyword) {
        LambdaQueryWrapper<RescueCase> wrapper = new LambdaQueryWrapper<RescueCase>()
                .orderByDesc(RescueCase::getCreatedAt);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(RescueCase::getCaseId, keyword)
                    .or().like(RescueCase::getVehNo, keyword)
                    .or().like(RescueCase::getUserName, keyword)
                    .or().like(RescueCase::getUserPhone, keyword)
                    .or().like(RescueCase::getRescueAddress, keyword));
        }
        return rescueCaseMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public RescueCase getDetail(String caseId) {
        RescueCase rescueCase = findByCaseId(caseId);
        if (rescueCase == null) {
            throw new RuntimeException("案例不存在: " + caseId);
        }
        return rescueCase;
    }

    @Override
    public void dispatchTechnician(String caseId, DispatchTechnicianRequest req) {
        externalApi.dispatchTechnician(caseId, req);
        try {
            RescueCase rescueCase = findByCaseId(caseId);
            if (rescueCase != null) {
                rescueCase.setCaseStatus(30);
                rescueCase.setCaseStatusName("已调度技师");
                rescueCase.setRescueJsId(req.getRescueJsId());
                rescueCase.setRescueVehId(req.getRescueVehId());
                rescueCase.setUpdatedAt(LocalDateTime.now());
                rescueCaseMapper.updateById(rescueCase);
            }
        } catch (Exception e) {
            log.warn("DB update skipped for caseId={} (dispatch): {}", caseId, e.getMessage());
        }
    }

    @Override
    public void technicianSign(String caseId, TechnicianActionRequest req) {
        updateStatusAfterExternalCall(caseId, req, 40, "技师已签收",
                () -> externalApi.technicianSign(caseId, req));
    }

    @Override
    public void technicianArrive(String caseId, TechnicianActionRequest req) {
        updateStatusAfterExternalCall(caseId, req, 50, "技师已到达",
                () -> externalApi.technicianArrive(caseId, req));
    }

    @Override
    public void technicianStart(String caseId, TechnicianActionRequest req) {
        updateStatusAfterExternalCall(caseId, req, 60, "技师已开始",
                () -> externalApi.technicianStart(caseId, req));
    }

    @Override
    public void technicianComplete(String caseId, TechnicianActionRequest req) {
        updateStatusAfterExternalCall(caseId, req, 70, "已完成",
                () -> externalApi.technicianComplete(caseId, req));
    }

    private void updateStatusAfterExternalCall(String caseId, TechnicianActionRequest req,
                                                int status, String statusName, Runnable apiCall) {
        apiCall.run();
        try {
            RescueCase rescueCase = findByCaseId(caseId);
            if (rescueCase != null) {
                if (StringUtils.hasText(req.getTaskId())) rescueCase.setTaskId(req.getTaskId());
                rescueCase.setCaseStatus(status);
                rescueCase.setCaseStatusName(statusName);
                rescueCase.setUpdatedAt(LocalDateTime.now());
                rescueCaseMapper.updateById(rescueCase);
            }
        } catch (Exception e) {
            log.warn("DB update skipped for caseId={} ({}): {}", caseId, statusName, e.getMessage());
        }
    }

    private RescueCase findByCaseId(String caseId) {
        return rescueCaseMapper.selectOne(
                new LambdaQueryWrapper<RescueCase>().eq(RescueCase::getCaseId, caseId));
    }

    private RescueCase buildFromCallback(OrderCallbackDTO dto, String rawJson) {
        RescueCase r = new RescueCase();
        r.setCaseId(dto.getCaseId());
        updateFromCallback(r, dto, rawJson);
        return r;
    }

    private void updateFromCallback(RescueCase r, OrderCallbackDTO dto, String rawJson) {
        r.setCaseStatus(dto.getCaseStatus());
        r.setCaseStatusName(dto.getCaseStatusName());
        r.setVehNo(dto.getVehNo());
        r.setVehIdentiNo(dto.getVehIdentiNo());
        r.setRescueType(dto.getRescueType());
        r.setRescueTypeName(dto.getRescueTypeName());
        r.setRescueCategory(dto.getRescueCategory());
        r.setRescueCategoryName(dto.getRescueCategoryName());
        r.setUserName(dto.getUserName());
        r.setUserPhone(dto.getUserPhone());
        r.setRescueAddress(dto.getRescueAddress());
        r.setRescueLon(dto.getRescueLon());
        r.setRescueLat(dto.getRescueLat());
        r.setRescueProvinceName(dto.getRescueProvinceName());
        r.setRescueCityName(dto.getRescueCityName());
        r.setRescueCountyName(dto.getRescueCountyName());
        r.setCaseReptTime(dto.getCaseReptTime());
        r.setAcceptTime(dto.getAcceptTime());
        r.setCaseToOrgId(dto.getCaseToOrgId());
        r.setCaseToOrgName(dto.getCaseToOrgName());
        r.setCaseToOrgSignTime(dto.getCaseToOrgSignTime());
        r.setRawData(rawJson);
        r.setUpdatedAt(LocalDateTime.now());
    }
}
