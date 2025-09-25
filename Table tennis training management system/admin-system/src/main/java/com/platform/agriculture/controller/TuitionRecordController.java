package com.platform.agriculture.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.agriculture.domain.dto.PageQueryDTO;
import com.platform.agriculture.domain.dto.TuitionRecordQueryDTO;
import com.platform.agriculture.domain.entity.TuitionRecord;
import com.platform.agriculture.service.ITuitionRecordService;
import com.platform.agriculture.utils.AjaxResult;
import com.platform.agriculture.utils.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 学费交易记录表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@RestController
@RequestMapping("/tuitionRecord")
@RequiredArgsConstructor
public class TuitionRecordController {
    private final ITuitionRecordService tuitionRecordService;
    @GetMapping("/list")
    public AjaxResult<Page<TuitionRecord>> list(TuitionRecordQueryDTO tuitionRecordQueryDTO){
        Page<TuitionRecord> page = new Page<>(tuitionRecordQueryDTO.getPageNum(), tuitionRecordQueryDTO.getPageSize());
        this.tuitionRecordService.lambdaQuery()
                .eq(StringUtils.isNotBlank(tuitionRecordQueryDTO.getRecordNo()), TuitionRecord::getRecordNo, tuitionRecordQueryDTO.getRecordNo())
                .or()
                .eq(StringUtils.isNotBlank(tuitionRecordQueryDTO.getPaymentMethod()), TuitionRecord::getPaymentMethod, tuitionRecordQueryDTO.getPaymentMethod())
                .page(page);
        return AjaxResult.success(page);
    }
    @GetMapping("/getById/{id}")
    public AjaxResult<TuitionRecord> getById(@PathVariable Long id){
        return AjaxResult.success(this.tuitionRecordService.getById(id));
    }
    @DeleteMapping("/delete/{id}")
    public AjaxResult<Void> delete(@PathVariable Long id){
        this.tuitionRecordService.removeById(id);
        return AjaxResult.success();
    }
    @PostMapping("/save")
    public AjaxResult<TuitionRecord> save(@RequestBody TuitionRecord tuitionRecord){
        if (tuitionRecord.getId() == null) {
            TuitionRecord one = this.tuitionRecordService.lambdaQuery()
                    .eq(TuitionRecord::getRecordNo, tuitionRecord.getRecordNo())
                    .eq(TuitionRecord::getUsername, tuitionRecord.getUsername())
                    .one();
            if (one != null) {
                return AjaxResult.error("已缴费过！");
            }
        }
        this.tuitionRecordService.saveOrUpdate(tuitionRecord);
        return AjaxResult.success(tuitionRecord);
    }

    @GetMapping("/getByUserId/{userId}")
    public AjaxResult<Page<TuitionRecord>> getByUserId(PageQueryDTO pageQueryDTO, @PathVariable Long userId){
        Page<TuitionRecord> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());
        this.tuitionRecordService.lambdaQuery()
                .eq(TuitionRecord::getUserId, userId)
                .page(page);
                return AjaxResult.success(page);
    }
    @GetMapping("/getByUser") // 获取学员的缴费记录
    public AjaxResult<List<TuitionRecord>> getByUser(HttpServletRequest request){
        Long userId = CommonUtils.getUserId(request);
        List<TuitionRecord> tuitionRecordList = this.tuitionRecordService.lambdaQuery()
                .eq(TuitionRecord::getUserId, userId)
                .list();
        return AjaxResult.success(tuitionRecordList);
    }
}
