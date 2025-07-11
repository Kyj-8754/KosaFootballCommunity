package com.msa.kyj_prj.match.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchAlarmMessageDTO implements AlarmMessage {
    private String type;         // 예: MATCH_RESULT, MATCH_INVITE 등
    private String senderId;     // 보낸 사람
    private String receiverId;   // 받는 사람
    private String message;      // 메시지 내용
    private String url;          // 알림 클릭 시 이동 경로

    // match만의 추가 필드 (필요시 자유롭게 추가)
    private Long matchId;
    private String matchTitle;
}
