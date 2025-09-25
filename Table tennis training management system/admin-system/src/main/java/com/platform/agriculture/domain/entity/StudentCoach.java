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
 * 学员选择教练关系表（使用 id 关联）
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@Getter
@Setter
@ToString
@TableName("student_coach")
public class StudentCoach implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学员 user.id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 学员姓名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 教练 user.id
     */
    @TableField("coach_id")
    private Long coachId;

    /**
     * 教练姓名
     */
    @TableField("coach_name")
    private String coachName;

    @TableField("is_approved")
    private Boolean isApproved;

    @TableField("apply_time")
    private LocalDateTime applyTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
