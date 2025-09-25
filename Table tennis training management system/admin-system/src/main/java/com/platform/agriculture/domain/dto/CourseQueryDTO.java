package com.platform.agriculture.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CourseQueryDTO extends PageQueryDTO{
    // coach_username
    private String coachUsername;
    // campus_id
    private Integer campusId;
}
