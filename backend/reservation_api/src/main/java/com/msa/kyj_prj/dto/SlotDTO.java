package com.msa.kyj_prj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "게시판 목록 및 페이징 응답 DTO")
public class SlotDTO {
	@Schema(description = "예약 슬롯 PK", example = "SVC123")
	private String slotid;		// 슬롯 pk값
	@Schema(description = "축구장 서비스ID", example = "SVC123")
	private String svcid; 		// 서비스 아이디
	@Schema(description = "이용 날짜", example = "SVC123")
	private String slotDate;	// 이용 날짜
	@Schema(description = "이용 시작 시간", example = "SVC123")
	private String startTime;	// 이용 시작 시간
	@Schema(description = "이용 종료 시간", example = "SVC123")
	private String endTime;	// 이용 종료 시간
	@Schema(description = "예약 상태", example = "SVC123")
	private String reservationStatus;	// 예약 스테이터스
}
