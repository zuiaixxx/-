package com.platform.agriculture.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CampusQueryDTO extends PageQueryDTO{
    private String keyword;
    private String campusType; // 类型：C-中心校区，B-分校区
}
