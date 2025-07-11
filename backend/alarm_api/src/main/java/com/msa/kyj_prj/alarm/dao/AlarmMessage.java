package com.msa.kyj_prj.alarm.dao;


public interface AlarmMessage {
	String getType();
	String getSenderId();
	String getReceiverId();
	String getMessage();
	String getUrl();
}
