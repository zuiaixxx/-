package com.platform.agriculture.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.agriculture.domain.dto.CoachDetailQueryDTO;
import com.platform.agriculture.domain.entity.CoachDetail;
import com.platform.agriculture.service.ICoachDetailService;
import com.platform.agriculture.utils.AjaxResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 教练员详情表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@RestController
@RequestMapping("/coachDetail")
@RequiredArgsConstructor
public class CoachDetailController {
    private final ICoachDetailService coachDetailService;
    @GetMapping("/list")
    public AjaxResult<Page<CoachDetail>> list(CoachDetailQueryDTO coachDetailQueryDTO){
        Page<CoachDetail> page = new Page<>(coachDetailQueryDTO.getPageNum(), coachDetailQueryDTO.getPageSize());

        coachDetailService.lambdaQuery()
                .like(StringUtils.isNotBlank(coachDetailQueryDTO.getUsername()), CoachDetail::getUsername, coachDetailQueryDTO.getUsername())
                .or()
                .eq(StringUtils.isNotBlank(coachDetailQueryDTO.getLevel()), CoachDetail::getLevel, coachDetailQueryDTO.getLevel())
                .eq(Objects.nonNull(coachDetailQueryDTO.getCampusId()), CoachDetail::getCampusId, coachDetailQueryDTO.getCampusId())
                .page(page);
                return AjaxResult.success(page);

    }
    @GetMapping("/getById/{id}")
    public AjaxResult<CoachDetail> getById(@PathVariable Long id){
        CoachDetail coachDetail = coachDetailService.getById(id);
        return AjaxResult.success(coachDetail);
    }
    @DeleteMapping("/delete/{id}")
    public AjaxResult<Void> delete(@PathVariable Long id){
        coachDetailService.removeById(id);
        return AjaxResult.success();
    }
    @PostMapping("/save")
    public AjaxResult<CoachDetail> save(@RequestBody CoachDetail coachDetail){
        coachDetailService.saveOrUpdate(coachDetail);
        return AjaxResult.success(coachDetail);
    }
    @GetMapping("/getByUsername/{username}") // 通过用户名查询教练的详细信息
    public AjaxResult<CoachDetail> getByUsername(@PathVariable String username){
        CoachDetail coachDetail = coachDetailService.lambdaQuery()
                .eq(CoachDetail::getUsername, username)
                .one();
        return AjaxResult.success(coachDetail);
    }

}
