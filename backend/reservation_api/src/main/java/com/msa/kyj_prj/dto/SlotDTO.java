package com.msa.kyj_prj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SlotDTO {
	private String slotid;		// 슬롯 pk값
	private String svcid; 		// 서비스 아이디
	private String slotDate;	// 이용 날짜
	private String startTime;	// 이용 시작 시간
	private String endTime;	// 이용 종료 시간
	private String reservationStatus;	// 예약 스테이터스
}
