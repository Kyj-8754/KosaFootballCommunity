package com.msa.do_login.webSocket;

public interface AlarmMessage {
    String getType();
    String getSenderId();
    String getReceiverId();
    String getMessage();
    String getUrl();
}