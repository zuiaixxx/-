package com.platform.agriculture.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.agriculture.domain.dto.BookingRecordQueryDTO;
import com.platform.agriculture.domain.entity.BookingRecord;
import com.platform.agriculture.domain.entity.User;
import com.platform.agriculture.service.IBookingRecordService;
import com.platform.agriculture.service.IUserService;
import com.platform.agriculture.utils.AjaxResult;
import com.platform.agriculture.utils.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 课程预约记录表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@RestController
@RequestMapping("/bookingRecord")
@RequiredArgsConstructor
public class BookingRecordController {
    private final IBookingRecordService bookingRecordService;
    private final IUserService userService;

    @GetMapping("/list")
    public AjaxResult<Page<BookingRecord>> list(BookingRecordQueryDTO bookingRecordQueryDTO){
        Page<BookingRecord> page = new Page<>(bookingRecordQueryDTO.getPageNum(), bookingRecordQueryDTO.getPageSize());
        this.bookingRecordService.lambdaQuery()
                .eq(StringUtils.isNotBlank(bookingRecordQueryDTO.getBookingNo()), BookingRecord::getBookingNo, bookingRecordQueryDTO.getBookingNo())
                .eq(StringUtils.isNotBlank(bookingRecordQueryDTO.getStatus()), BookingRecord::getStatus, bookingRecordQueryDTO.getStatus())
                .eq(Objects.nonNull(bookingRecordQueryDTO.getCampusId()), BookingRecord::getCampusId, bookingRecordQueryDTO.getCampusId())
                .page( page);
        return AjaxResult.success(page);

    }
    @GetMapping("/getById/{id}")
    public AjaxResult<BookingRecord> getById(@PathVariable Long id){
        BookingRecord bookingRecord = this.bookingRecordService.getById(id);
        return AjaxResult.success(bookingRecord);
    }
    @DeleteMapping("/delete/{id}")
    public AjaxResult<Void> delete(@PathVariable Long id){
        this.bookingRecordService.removeById(id);
        return AjaxResult.success();
    }
    @PostMapping("/save")
    public AjaxResult<BookingRecord> save(@RequestBody BookingRecord bookingRecord){


        if (bookingRecord.getId()==null){
            BookingRecord one = bookingRecordService.lambdaQuery()
                .eq(BookingRecord::getCourseId, bookingRecord.getCourseId())
                .eq(BookingRecord::getStudentName, bookingRecord.getStudentName())
                .one();
        if (one != null) {
            return AjaxResult.error("你已经预约过了");
        }
        }
        this.bookingRecordService.saveOrUpdate(bookingRecord);
        return AjaxResult.success(bookingRecord);
    }
    @GetMapping("/getByCoachBooking/")
    public AjaxResult<List<BookingRecord>> getByCoachBooking(HttpServletRequest request){
        Long coachId = CommonUtils.getUserId(request);
        User user = userService.getById(coachId);
        List<BookingRecord> bookingRecordList = this.bookingRecordService.lambdaQuery()
                .eq(BookingRecord::getCoachUsername, user.getUsername())
                .list();
        return AjaxResult.success(bookingRecordList);
    }
    @GetMapping("/getByStudentBooking/")
    public AjaxResult<List<BookingRecord>> getByStudentBooking(HttpServletRequest request){
        Long studentId = CommonUtils.getUserId(request);
        User user = userService.getById(studentId);
        List<BookingRecord> bookingRecordList = this.bookingRecordService.lambdaQuery()
                .eq(BookingRecord::getStudentUsername, user.getUsername())
                .list();
        return AjaxResult.success(bookingRecordList);
    }
}
