package com.msa.kyj_prj.alarm;  // 경로는 alarm/ 또는 dto/ 등 자유롭게

import lombok.Data;

@Data
public class AlarmMessageDTO {
    private String type;
    private String senderId;
    private String receiverId;
    private int clubId;
    private String message;
    private String url; // 알림 클릭 시 이동할 경로

}
