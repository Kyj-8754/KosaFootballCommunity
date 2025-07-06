package com.msa.kyj_prj.alarm;

import lombok.Data;

@Data
public class AlarmMessageDTO implements AlarmMessage {
    private String type;       // 예: JOIN_REQUEST, JOIN_APPROVED
    private String senderId;   // 보낸 사람
    private String receiverId; // 받는 사람 (알림 대상)
    private int clubId;        // 클럽 ID
    private String message;    // 메시지 내용
    private String url; // 알림 클릭 시 이동할 경로
    // 기본 생성자
    public AlarmMessageDTO() {}

    // 모든 필드를 받는 생성자 (필요에 따라)
    public AlarmMessageDTO(String type, String senderId, String receiverId, String message, String url) {
        this.type = type;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.url = url;
    }

    @Override
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    @Override
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    @Override
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    // toString 등 필요한 메소드 추가 가능
}