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
 * 学费交易记录表
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@Getter
@Setter
@ToString
@TableName("tuition_record")
public class TuitionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;

    /**
     * 交易编号
     */
    @TableField("record_no")
    private String recordNo;

    /**
     * 用户 username
     */
    @TableField("username")
    private String username;

    /**
     * 用户姓名
     */
    @TableField("name")
    private String name;


    /**
     * 金额（正数）
     */
    @TableField("amount")
    private BigDecimal amount;


    /**
     * 支付方式：wechat, alipay, offline
     */
    @TableField("payment_method")
    private String paymentMethod;


    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    @TableField("create_time")
    private LocalDateTime createTime;
}
