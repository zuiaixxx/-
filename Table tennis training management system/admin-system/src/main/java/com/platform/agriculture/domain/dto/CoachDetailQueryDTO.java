package com.platform.agriculture.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CoachDetailQueryDTO extends PageQueryDTO{
    private String username; //关联用户表的username
    private String level; // 教练等级：初级、中级、高级
    private Integer campusId;
}
