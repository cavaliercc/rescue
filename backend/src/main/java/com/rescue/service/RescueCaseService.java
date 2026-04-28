package com.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.dto.rescue.DispatchTechnicianRequest;
import com.rescue.dto.rescue.OrderCallbackDTO;
import com.rescue.dto.rescue.TechnicianActionRequest;
import com.rescue.entity.RescueCase;

public interface RescueCaseService {

    void handleOrderCallback(OrderCallbackDTO dto, String rawJson);

    Page<RescueCase> pageList(int page, int size, String keyword);

    RescueCase getDetail(String caseId);

    void dispatchTechnician(String caseId, DispatchTechnicianRequest req);

    void technicianSign(String caseId, TechnicianActionRequest req);

    void technicianArrive(String caseId, TechnicianActionRequest req);

    void technicianStart(String caseId, TechnicianActionRequest req);

    void technicianComplete(String caseId, TechnicianActionRequest req);
}
