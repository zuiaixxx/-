package com.platform.agriculture.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TuitionRecordQueryDTO extends PageQueryDTO{
    // record_no
    private String recordNo;
    // payment_method 支付方式：wechat, alipay, offline
    private String paymentMethod;
}
