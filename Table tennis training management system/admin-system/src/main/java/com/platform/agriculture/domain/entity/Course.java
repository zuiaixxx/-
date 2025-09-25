package com.platform.agriculture.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 课程排班表
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@Getter
@Setter
@ToString
@TableName("course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 教练 username
     */
    @TableField("coach_username")
    private String coachUsername;

    /**
     * 教练姓名（冗余）
     */
    @TableField("coach_name")
    private String coachName;

    /**
     * 校区编号
     */
    @TableField("campus_id")
    private String campusId;

    /**
     * 课程日期
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
     * 是否可预约：1=是，0=否
     */
    @TableField("available")
    private Boolean available;

    @TableField("img")
    private String img;

    /**
     * 球台编号
     */
    @TableField("court_number")
    private String courtNumber;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

}
