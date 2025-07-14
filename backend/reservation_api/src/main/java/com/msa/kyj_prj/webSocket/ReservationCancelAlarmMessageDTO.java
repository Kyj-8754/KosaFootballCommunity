package com.msa.kyj_prj.webSocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCancelAlarmMessageDTO implements AlarmMessage {
	private String type;         // 예: 친구 신청, 승인/거절
    private String senderId;     // 보낸 사람
    private String receiverId;   // 받는 사람
    private String message;      // 메시지 내용
    private String url;          // 알림 클릭 시 이동 경로

    private Long matchId;   // 연동된 매치 id (nullable)
    private String matchTitle; // 매치명(알림에서 쓸 수 있음, nullable)
}
