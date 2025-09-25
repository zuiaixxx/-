package com.platform.agriculture.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 课程预约记录表
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@Getter
@Setter
@ToString
@TableName("booking_record")
public class BookingRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 预约单号
     */
    @TableField("booking_no")
    private String bookingNo;

    /**
     * 学员 username
     */
    @TableField("student_username")
    private String studentUsername;

    /**
     * 学员姓名
     */
    @TableField("student_name")
    private String studentName;

    /**
     * 教练 username
     */
    @TableField("coach_username")
    private String coachUsername;

    /**
     * 教练姓名
     */
    @TableField("coach_name")
    private String coachName;

    /**
     * 校区编号
     */
    @TableField("campus_id")
    private String campusId;

    /**
     * 对应课程ID（逻辑外键）
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 上课日期
     */
    @TableField("date")
    private LocalDate date;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalTime endTime;

    /**
     * 时长（分钟）
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 球台编号
     */
    @TableField("court_number")
    private String courtNumber;

    /**
     * 状态：pending, confirmed, cancelled, completed
     */
    @TableField("status")
    private String status;

    /**
     * 课时费
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 支付状态：unpaid, paid, refunded
     */
    @TableField("payment_status")
    private String paymentStatus;

    /**
     * 取消方 username
     */
    @TableField("cancel_initiator")
    private String cancelInitiator;

    /**
     * 取消原因
     */
    @TableField("cancel_reason")
    private String cancelReason;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
