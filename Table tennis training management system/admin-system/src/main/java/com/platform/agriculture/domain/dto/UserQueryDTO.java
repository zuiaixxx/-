package com.platform.agriculture.domain.dto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data // Lombok 注解，自动生成 getter 和 setter
public class UserQueryDTO extends PageQueryDTO{

    @Parameter(description = "名字查找")
    private String name;
    @Parameter(description = "状态查找")
    private Integer status;
}
