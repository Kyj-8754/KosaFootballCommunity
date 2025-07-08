package com.msa.kyj_prj.alarm;

import lombok.Data;

@Data
public class MatchAlarmMessageDTO implements AlarmMessage {
    private String type;
    private String senderId;
    private String receiverId;
    private String message;
    private String url;
    private Long matchId;
    private String matchTitle;

    public MatchAlarmMessageDTO() {}
}
