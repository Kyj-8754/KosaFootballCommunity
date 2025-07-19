package com.msa.kyj_prj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
	@Schema(description = "축구장서비스ID", example = "SVC123")
	private int reservation_id;
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private int slot_id;
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private String reservation_type;
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private String reserved_at;
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private int user_no;
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private String status;
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private String cancelled_at;
	@Schema(description = "가격", example = "10000")
    private int price;

	@Schema(description = "서비스명", example = "SVC123")
	private String svcnm;

	// 조인된 slot 정보
	@Schema(description = "이용 날짜", example = "SVC123")
    private String slot_date;
	@Schema(description = "이용 시작 시간", example = "SVC123")
    private String start_time;
	@Schema(description = "이용 종료 시간", example = "SVC123")
    private String end_time;
	@Schema(description = "구장 서비스ID", example = "SVC123")
    private String svcid;
    
    // 조인된 payment 정보
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private String payment_status;
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private String paid_at;
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private String method;
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private int amount;
	@Schema(description = "축구장서비스ID", example = "SVC123")
    private String tid;
    
}
