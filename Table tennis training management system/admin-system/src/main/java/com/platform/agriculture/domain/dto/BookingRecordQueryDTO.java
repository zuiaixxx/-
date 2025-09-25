package com.platform.agriculture.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BookingRecordQueryDTO extends PageQueryDTO{
//campus_id
    private Integer campusId;
    // booking_no
    private String bookingNo;
    // 状态：pending, confirmed, cancelled, completed
    private String status;
}
