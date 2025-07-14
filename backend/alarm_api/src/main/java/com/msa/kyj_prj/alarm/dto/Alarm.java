package com.msa.kyj_prj.alarm.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Alarm {
    private int alarm_id;
    private int receiver_id;
    private int sender_id;   // NULL 허용
    private String type;
    private String message;
    private String url;
    private String read_yn;
    private LocalDateTime created_at;

    // JOIN 결과 (선택)
    private String receiver_name;
    private String sender_name;
}
