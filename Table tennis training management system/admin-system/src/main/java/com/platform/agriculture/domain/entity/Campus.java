package com.platform.agriculture.domain.entity;

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
 * 校区信息表（逻辑外键）
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@Getter
@Setter
@ToString
@TableName("campus")
public class Campus implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId("id")
    private Integer id;

    /**
     * 校区编号（主键，如：ZX001, FX002）
     */
    @TableField("campus_no")
    private String campusNo;

    /**
     * 校区名称
     */
    @TableField("campus_name")
    private String campusName;

    /**
     * 类型：C-中心校区，B-分校区
     */
    @TableField("campus_type")
    private String campusType;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 联系人姓名
     */
    @TableField("contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 联系邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 逻辑外键：校区管理员用户名
     */
    @TableField("admin_username")
    private String adminUsername;

    /**
     * 校区图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private LocalDateTime createdTime;

    /**
     * 最后更新时间
     */
    @TableField("updated_time")
    private LocalDateTime updatedTime;
}
