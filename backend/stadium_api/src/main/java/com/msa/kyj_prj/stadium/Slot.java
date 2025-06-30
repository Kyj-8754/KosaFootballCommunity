package com.msa.kyj_prj.stadium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class Slot {
	private String SLOT_ID;		// 슬롯 pk값
	private String SVCID;		// 서비스 번호
	private String SLOT_DATE;	// 이용 날짜
	private String START_TIME;	// 이용 시작 시간
	private String END_TIME;	// 이용 종료 시간
}
