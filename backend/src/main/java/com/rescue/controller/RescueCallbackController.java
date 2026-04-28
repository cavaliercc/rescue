package com.rescue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rescue.dto.ApiResponse;
import com.rescue.dto.rescue.OrderCallbackDTO;
import com.rescue.service.RescueCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/callback")
@RequiredArgsConstructor
public class RescueCallbackController {

    private final RescueCaseService rescueCaseService;
    private final ObjectMapper objectMapper;

    @PostMapping("/order")
    public ApiResponse<Void> orderCallback(@RequestBody OrderCallbackDTO dto) {
        try {
            String rawJson = objectMapper.writeValueAsString(dto);
            rescueCaseService.handleOrderCallback(dto, rawJson);
            return ApiResponse.ok(null);
        } catch (Exception e) {
            log.error("Order callback processing failed for caseId={}: {}", dto.getCaseId(), e.getMessage());
            return ApiResponse.fail("回调处理失败: " + e.getMessage());
        }
    }
}
