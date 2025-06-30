package com.msa.kyj_prj.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class Reservation {
	private int reservation_id;			// 예약 id (pk)
	private int slot_id;				// 예약 슬롯 id (fk)
	private String reservation_type;	// 예약 타입
	private String reservation_at;		// 예약 시간
	private int user_no;				// 유저NO
	private String status;				// 예약 상황
	private String cancelled_at;		// 취소 시간
}
