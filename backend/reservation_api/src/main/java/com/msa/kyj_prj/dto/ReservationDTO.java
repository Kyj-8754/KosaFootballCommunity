package com.msa.kyj_prj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
	private int reservation_id;
    private int slot_id;
    private String reservation_type;
    private String reserved_at;
    private int user_no;
    private String status;
    private String cancelled_at;
    private int price;

    // 조인된 slot 정보
    private String slot_date;
    private String start_time;
    private String end_time;
    private String svcid;
    
    // 조인된 payment 정보
    private String payment_status;
    private String paid_at;
    private String method;
    private int amount;
    private String tid;
    
    
    private String svcnm;

}
