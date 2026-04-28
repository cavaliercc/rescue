package com.rescue.dto.rescue;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TechnicianActionRequest {

    @NotBlank
    private String taskId;

    @NotBlank
    private String handleUserId;

    private String handleOrgId;
    private String handleUserName;
    private String handleMethod;

    private Double rescueRealAddressLat;
    private Double rescueRealAddressLon;

    private Integer rescueReason;
    private String rescueType;
    private Integer rescueVehId = 0;
    private Integer roadType = 0;
    private Integer waitTimelenMin = 0;
    private Integer dragFloorNum = 0;
    private Integer wheelGroupNum = 0;
    private Integer estArrDur = 0;
    private Integer carOwnerObjection = 0;
    private Integer caseFromOrgId;
    private Integer dataTransMethod = 1;
    private String appVersion = "android app v3.9.9";
    private String version = "3.9.9";
    private String deviceToken;
}
