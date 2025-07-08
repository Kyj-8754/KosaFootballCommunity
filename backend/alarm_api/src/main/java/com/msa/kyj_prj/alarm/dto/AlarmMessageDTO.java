package com.msa.kyj_prj.alarm.dto;

import com.msa.kyj_prj.alarm.dao.AlarmMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클럽 모듈용 알림 메시지 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmMessageDTO implements AlarmMessage {
    private String type; // 예: JOIN_REQUEST, JOIN_APPROVED 등 (필수!)
    private String senderId; // 보낸 사람
    private String receiverId; // 받는 사람 (알림 대상)
    private int clubId; // 클럽 ID
    private String message; // 메시지 내용
    private String url; // 알림 클릭 시 이동할 경로 (null 허용)
}
