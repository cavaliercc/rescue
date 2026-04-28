package com.rescue.service;

import com.rescue.dto.rescue.DispatchTechnicianRequest;
import com.rescue.dto.rescue.TechnicianActionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalRescueApiService {

    private final RestTemplate restTemplate;

    @Value("${external.rescue.base-url:https://sos.jsecode.com}")
    private String baseUrl;

    public Map<String, Object> reportLocation(String caseId, String reportTaskId,
            double lon, double lat, String handleUserId, String handleOrgId,
            String handleUserName, Integer caseFromOrgId, String deviceToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("caseId", caseId);
        body.put("taskId", reportTaskId);
        body.put("rescueRealAddressLon", lon);
        body.put("rescueRealAddressLat", lat);
        body.put("handleUserId", handleUserId);
        body.put("handleOrgId", handleOrgId);
        body.put("handleUserName", handleUserName);
        body.put("caseFromOrgId", caseFromOrgId != null ? caseFromOrgId : 0);
        body.put("deviceToken", deviceToken);
        body.put("appVersion", "android app v3.9.9");
        body.put("version", "3.9.9");
        body.put("dataTransMethod", 1);
        body.put("handleMethod", "32");
        body.put("rescueType", "1");
        body.put("rescueReason", 1);
        body.put("rescueVehId", 0);
        body.put("roadType", 0);
        body.put("waitTimelenMin", 0);
        body.put("dragFloorNum", 0);
        body.put("wheelGroupNum", 0);
        body.put("estArrDur", 0);
        body.put("carOwnerObjection", 0);
        return post("/api/rms/jis/jisSign", body);
    }

    public Map<String, Object> dispatchTechnician(String caseId, DispatchTechnicianRequest req) {
        Map<String, Object> body = new HashMap<>();
        body.put("caseId", caseId);
        body.put("dataTransMethod", req.getDataTransMethod());
        body.put("rescueJsId", req.getRescueJsId());
        body.put("rescueVehId", req.getRescueVehId());
        body.put("realRescueCategory", req.getRealRescueCategory());
        body.put("projectUnit", req.getProjectUnit());
        return post("/api/rms/supplierDispatch", body);
    }

    public Map<String, Object> technicianSign(String caseId, TechnicianActionRequest req) {
        req.setHandleMethod("30");
        return post("/api/rms/jis/jisSign", buildTechBody(caseId, req));
    }

    public Map<String, Object> technicianArrive(String caseId, TechnicianActionRequest req) {
        req.setHandleMethod("32");
        return post("/api/rms/jis/jisArrive", buildTechBody(caseId, req));
    }

    public Map<String, Object> technicianStart(String caseId, TechnicianActionRequest req) {
        req.setHandleMethod("34");
        return post("/api/rms/jis/jisStart", buildTechBody(caseId, req));
    }

    public Map<String, Object> technicianComplete(String caseId, TechnicianActionRequest req) {
        req.setHandleMethod("36");
        return post("/api/rms/jis/jisComplete", buildTechBody(caseId, req));
    }

    private Map<String, Object> buildTechBody(String caseId, TechnicianActionRequest req) {
        Map<String, Object> body = new HashMap<>();
        body.put("caseId", caseId);
        body.put("taskId", req.getTaskId());
        body.put("handleUserId", req.getHandleUserId());
        body.put("handleOrgId", req.getHandleOrgId());
        body.put("handleUserName", req.getHandleUserName());
        body.put("handleMethod", req.getHandleMethod());
        body.put("rescueRealAddressLat", req.getRescueRealAddressLat());
        body.put("rescueRealAddressLon", req.getRescueRealAddressLon());
        body.put("rescueReason", req.getRescueReason());
        body.put("rescueType", req.getRescueType());
        body.put("rescueVehId", req.getRescueVehId());
        body.put("roadType", req.getRoadType());
        body.put("waitTimelenMin", req.getWaitTimelenMin());
        body.put("dragFloorNum", req.getDragFloorNum());
        body.put("wheelGroupNum", req.getWheelGroupNum());
        body.put("estArrDur", req.getEstArrDur());
        body.put("carOwnerObjection", req.getCarOwnerObjection());
        body.put("caseFromOrgId", req.getCaseFromOrgId() != null ? req.getCaseFromOrgId() : 0);
        body.put("dataTransMethod", req.getDataTransMethod());
        body.put("appVersion", req.getAppVersion());
        body.put("version", req.getVersion());
        body.put("deviceToken", req.getDeviceToken());
        return body;
    }

    private String getEsbToken() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String token = attrs.getRequest().getHeader("X-Esb-Token");
                return StringUtils.hasText(token) ? token : null;
            }
        } catch (Exception ignored) {}
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String path, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = getEsbToken();
        if (token != null) {
            headers.set("esb-token", token);
        }
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        String url = baseUrl + path;
        log.info("Calling external API: {} (esb-token={})", url, token != null ? "present" : "missing");
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map<String, Object> result = response.getBody();
            log.info("External API response [{}]: {}", url, result);
            if (result != null) {
                Object retCode = result.get("retCode");
                if (retCode instanceof Number && ((Number) retCode).intValue() != 0) {
                    String retMsg = result.getOrDefault("retMsg", "未知错误").toString();
                    throw new RuntimeException("外部接口返回错误 [" + retCode + "]: " + retMsg);
                }
            }
            return result;
        } catch (RuntimeException e) {
            log.error("External API call failed [{}]: {}", url, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("External API call failed [{}]: {}", url, e.getMessage());
            throw new RuntimeException("外部接口调用失败: " + e.getMessage(), e);
        }
    }
}
