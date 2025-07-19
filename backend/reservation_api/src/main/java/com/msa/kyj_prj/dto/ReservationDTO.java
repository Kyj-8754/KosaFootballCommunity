package com.msa.kyj_prj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
	@Schema(description = "예약 ID", example = "1")
	private int reservation_id;
	@Schema(description = "예약 슬롯 ID", example = "1")
    private int slot_id;
	@Schema(description = "예약 타입", example = "social")
    private String reservation_type;
	@Schema(description = "예약 시간", example = "2025-07-16 09:54:12")
    private String reserved_at;
	@Schema(description = "예약 회원", example = "2")
    private int user_no;
	@Schema(description = "예약 상태", example = "reserved")
    private String status;
	@Schema(description = "취소 시간", example = "2025-07-16 12:45:12")
    private String cancelled_at;
	@Schema(description = "가격", example = "10000")
    private int price;

	@Schema(description = "서비스명", example = "SVC123")
	private String svcnm;

	// 조인된 slot 정보
	@Schema(description = "가능한 날짜", example = "2025-07-18")
    private String slot_date;
	@Schema(description = "이용 시작 시간", example = "10:00:00")
    private String start_time;
	@Schema(description = "이용 종료 시간", example = "12:00:00")
    private String end_time;
	@Schema(description = "구장 서비스ID", example = "S190522144836251426")
    private String svcid;
    
    // 조인된 payment 정보
	@Schema(description = "결제 상태", example = "paid")
    private String payment_status;
	@Schema(description = "결제 시간", example = "2025-07-16 10:54:12")
    private String paid_at;
	@Schema(description = "결제 방식", example = "KAKAO")
    private String method;
	@Schema(description = "결제 가격", example = "100000")
    private int amount;
	@Schema(description = "결제 TID", example = "T878bc93757b75da0901")
    private String tid;
    
}
