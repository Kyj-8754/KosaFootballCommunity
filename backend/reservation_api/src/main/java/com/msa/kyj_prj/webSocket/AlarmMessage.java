package com.msa.kyj_prj.webSocket;

public interface AlarmMessage {
    String getType();
    String getSenderId();
    String getReceiverId();
    String getMessage();
    String getUrl();
}