package com.msa.kyj_prj.alarm;

import lombok.Data;

@Data
public class AlarmMessageDTO {
    private String type;       // 예: JOIN_REQUEST, JOIN_APPROVED
    private String senderId;   // 보낸 사람
    private String receiverId; // 받는 사람 (알림 대상)
    private int clubId;        // 클럽 ID
    private String message;    // 메시지 내용
}
