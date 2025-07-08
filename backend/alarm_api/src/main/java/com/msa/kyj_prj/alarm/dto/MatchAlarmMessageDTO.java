package com.msa.kyj_prj.alarm.dto;

import com.msa.kyj_prj.alarm.dao.AlarmMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchAlarmMessageDTO implements AlarmMessage {
    private String type;
    private String senderId;
    private String receiverId;
    private String message;
    private String url;
    private Long matchId;
    private String matchTitle;

}
