package com.rescue.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.dto.ApiResponse;
import com.rescue.dto.rescue.DispatchTechnicianRequest;
import com.rescue.dto.rescue.TechnicianActionRequest;
import com.rescue.entity.RescueCase;
import com.rescue.service.RescueCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class RescueCaseController {

    private final RescueCaseService rescueCaseService;

    @GetMapping
    public ApiResponse<Page<RescueCase>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(rescueCaseService.pageList(page, size, keyword));
    }

    @GetMapping("/{caseId}")
    public ApiResponse<RescueCase> detail(@PathVariable String caseId) {
        return ApiResponse.ok(rescueCaseService.getDetail(caseId));
    }

    @PostMapping("/{caseId}/dispatch")
    public ApiResponse<Void> dispatch(@PathVariable String caseId,
                                      @Valid @RequestBody DispatchTechnicianRequest req) {
        rescueCaseService.dispatchTechnician(caseId, req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{caseId}/technician/sign")
    public ApiResponse<Void> technicianSign(@PathVariable String caseId,
                                            @Valid @RequestBody TechnicianActionRequest req) {
        rescueCaseService.technicianSign(caseId, req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{caseId}/technician/arrive")
    public ApiResponse<Void> technicianArrive(@PathVariable String caseId,
                                              @Valid @RequestBody TechnicianActionRequest req) {
        rescueCaseService.technicianArrive(caseId, req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{caseId}/technician/start")
    public ApiResponse<Void> technicianStart(@PathVariable String caseId,
                                             @Valid @RequestBody TechnicianActionRequest req) {
        rescueCaseService.technicianStart(caseId, req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{caseId}/technician/complete")
    public ApiResponse<Void> technicianComplete(@PathVariable String caseId,
                                                @Valid @RequestBody TechnicianActionRequest req) {
        rescueCaseService.technicianComplete(caseId, req);
        return ApiResponse.ok(null);
    }
}
