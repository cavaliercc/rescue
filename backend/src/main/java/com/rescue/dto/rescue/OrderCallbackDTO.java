package com.rescue.dto.rescue;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderCallbackDTO {

    private String caseId;
    private Integer caseStatus;
    private String caseStatusName;

    private String vehNo;
    private String vehIdentiNo;

    private Integer rescueType;
    private String rescueTypeName;
    private Integer rescueCategory;
    private String rescueCategoryName;

    private String userName;
    private String userPhone;

    private String rescueAddress;
    private BigDecimal rescueLon;
    private BigDecimal rescueLat;

    private String rescueProvinceName;
    private String rescueCityName;
    private String rescueCountyName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime caseReptTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptTime;

    private Long caseToOrgId;
    private String caseToOrgName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime caseToOrgSignTime;
}
