package com.platform.agriculture.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.agriculture.domain.dto.CampusQueryDTO;
import com.platform.agriculture.domain.entity.Campus;
import com.platform.agriculture.service.ICampusService;
import com.platform.agriculture.utils.AjaxResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 校区信息表（逻辑外键） 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@RestController
@RequestMapping("/campus")
@RequiredArgsConstructor
public class CampusController {
    private final ICampusService campusService;
    @GetMapping("/list")
    public AjaxResult<Page<Campus>> list(CampusQueryDTO campusQueryDTO) {
        Page<Campus> page = new Page<>(campusQueryDTO.getPageNum(), campusQueryDTO.getPageSize());

        this.campusService.lambdaQuery()
                .like(StringUtils.isNotBlank(campusQueryDTO.getKeyword()), Campus::getCampusName, campusQueryDTO.getKeyword())
                .or()
                .like(StringUtils.isNotBlank(campusQueryDTO.getCampusType()), Campus::getCampusType, campusQueryDTO.getCampusType())
                .or()
                .like(StringUtils.isNotBlank(campusQueryDTO.getKeyword()), Campus::getAddress, campusQueryDTO.getKeyword())
                .or()
                .like(StringUtils.isNotBlank(campusQueryDTO.getKeyword()), Campus::getContactPerson, campusQueryDTO.getKeyword())
                .eq(StringUtils.isNotBlank(campusQueryDTO.getCampusType()), Campus::getCampusType, campusQueryDTO.getCampusType())
                .page( page);
        return AjaxResult.success(page);
    }
    @GetMapping("/getById/{id}")
    public AjaxResult<Campus> getById(@PathVariable Long id) {
        return AjaxResult.success(this.campusService.getById(id));
    }
    @DeleteMapping("/delete/{id}")
    public AjaxResult<Void> delete(@PathVariable Long id) {
        this.campusService.removeById(id);
        return AjaxResult.success();
    }
    @PostMapping("/save")
    public AjaxResult<Campus> save(@RequestBody Campus campus) {
        this.campusService.saveOrUpdate(campus);
        return AjaxResult.success(campus);
    }

}
