package com.rescue.controller;

import com.rescue.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalOrderController {

    private final RestTemplate restTemplate;

    @Value("${external.rescue.base-url:https://sos.jsecode.com}")
    private String baseUrl;

    @SuppressWarnings("unchecked")
    @GetMapping("/order/{caseId}")
    public ApiResponse<Map<String, Object>> queryOrder(
            @PathVariable String caseId,
            @RequestHeader(value = "X-Sos-Token", required = false) String token) {
        String url = baseUrl + "/api/order/queryOrderByCaseId/" + caseId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null && !token.isBlank()) {
            headers.set("esb-token", token);
        }
        try {
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
            Map<String, Object> body = resp.getBody();
            if (body == null) return ApiResponse.fail("外部接口无响应");
            Object retCode = body.get("retCode");
            if (!"1".equals(String.valueOf(retCode))) {
                return ApiResponse.fail(body.getOrDefault("retMsg", "查询失败").toString());
            }
            return ApiResponse.ok((Map<String, Object>) body.get("detail"));
        } catch (Exception e) {
            log.error("Query order failed caseId={}: {}", caseId, e.getMessage());
            return ApiResponse.fail("查询失败: " + e.getMessage());
        }
    }
}
