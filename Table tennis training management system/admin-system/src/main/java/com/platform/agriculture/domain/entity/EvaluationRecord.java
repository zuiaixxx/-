package com.platform.agriculture.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 训练课评价记录表
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@Getter
@Setter
@ToString
@TableName("evaluation_record")
public class EvaluationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联预约单号
     */
    @TableField("booking_no")
    private String bookingNo;

    /**
     * 学员 username
     */
    @TableField("student_username")
    private String studentUsername;

    /**
     * 学员反馈：收获与教训
     */
    @TableField("student_feedback")
    private String studentFeedback;

    /**
     * 学员评分 1-5
     */
    @TableField("student_rating")
    private Byte studentRating;

    /**
     * 教练 username
     */
    @TableField("coach_username")
    private String coachUsername;

    /**
     * 教练反馈：表现与建议
     */
    @TableField("coach_feedback")
    private String coachFeedback;

    /**
     * 教练评分 1-5
     */
    @TableField("coach_rating")
    private Byte coachRating;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
