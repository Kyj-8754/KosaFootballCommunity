package com.msa.kyj_prj.match.alarm;

public interface AlarmMessage {
    String getType();
    String getSenderId();
    String getReceiverId();
    String getMessage();
    String getUrl();
}