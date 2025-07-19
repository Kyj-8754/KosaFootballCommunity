package com.msa.kyj_prj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class Slot {
	@Schema(description = "예약 슬롯 PK", example = "1")
	private String SLOT_ID;		// 슬롯 pk값
	@Schema(description = "구장 서비스ID", example = "S190522144836251426")
	private String SVCID;		// 서비스 번호
	@Schema(description = "이용 날짜", example = "2025-07-15")
	private String SLOT_DATE;	// 이용 날짜
	@Schema(description = "이용 시작 시간", example = "08:00:00")
	private String START_TIME;	// 이용 시작 시간
	@Schema(description = "이용 종료 시간", example = "10:00:00")
	private String END_TIME;	// 이용 종료 시간
}
