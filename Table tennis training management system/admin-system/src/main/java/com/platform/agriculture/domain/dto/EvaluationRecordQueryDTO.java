package com.platform.agriculture.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EvaluationRecordQueryDTO extends PageQueryDTO{
    //booking_no
    private String bookingNo;
}
