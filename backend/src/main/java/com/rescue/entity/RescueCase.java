package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("rescue_case")
public class RescueCase {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String caseId;

    /** 10=已派单, 20=已签收, 30=已调度技师, 40=技师已签收, 50=技师已到达, 60=技师已开始, 70=已完成 */
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

    private LocalDateTime caseReptTime;
    private LocalDateTime acceptTime;

    private Long caseToOrgId;
    private String caseToOrgName;
    private LocalDateTime caseToOrgSignTime;

    private String taskId;
    private String rescueJsId;
    private Integer rescueVehId;

    private String rawData;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
