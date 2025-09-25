package com.platform.agriculture.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 教练员详情表
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@Getter
@Setter
@ToString
@TableName("coach_detail")
public class CoachDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 逻辑外键：关联 user 表 username
     */
    @TableField("username")
    private String username;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 教练等级：初级、中级、高级
     */
    @TableField("level")
    private String level;

    /**
     * 每小时收费
     */
    @TableField("hourly_rate")
    private BigDecimal hourlyRate;

    /**
     * 教练照片URL
     */
    @TableField("photo_url")
    private String photoUrl;

    /**
     * 比赛成绩描述
     */
    @TableField("achievements")
    private String achievements;

    /**
     * 审核状态：pending, approved, rejected
     */
    @TableField("status")
    private String status;

    /**
     * 所属校区编号（逻辑外键）
     */
    @TableField("campus_id")
    private String campusId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
