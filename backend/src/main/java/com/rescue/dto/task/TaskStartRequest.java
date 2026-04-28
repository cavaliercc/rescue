package com.rescue.dto.task;

import lombok.Data;

import java.util.List;

@Data
public class TaskStartRequest {
    private String caseId;
    private String workId;
    private CoordPoint origin;
    private CoordPoint destination;
    private double speed;
    private int interval;

    // jisSign fields for GPS position reporting
    private String reportTaskId;
    private String handleUserId;
    private String handleOrgId;
    private String handleUserName;
    private Integer caseFromOrgId;
    private String deviceToken;

    @Data
    public static class CoordPoint {
        private String address;
        private String lon;
        private String lat;
    }
}
