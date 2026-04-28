package com.rescue.dto.rescue;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DispatchTechnicianRequest {

    @NotBlank
    private String rescueJsId;

    private Integer rescueVehId;
    private String realRescueCategory;
    private Integer dataTransMethod = 0;
    private String projectUnit = "";
}
