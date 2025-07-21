package com.msa.do_login.webSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component // 반드시 추가!
@RequiredArgsConstructor
@Slf4j
public class WebSocket {

	private final RestTemplate restTemplate;

	@Value("${alarm.api.url}")
	private String alarmApiUrl;
	

	public void sendFriendRequestAlarm(String type, String senderId, String senderUserName, String receiverId) {
	  
		 // [로그1] 메서드 진입, 파라미터 확인
        log.info("🟢 sendFriendRequestAlarm 진입: type={}, senderId={}, senderUserName={}, receiverId={}",
                type, senderId, senderUserName, receiverId);
		
	    FriendAlarmMessageDTO alarm = new FriendAlarmMessageDTO();
	    alarm.setType("FRIEND_REQUEST");
        alarm.setSenderId(senderId);
        alarm.setSenderUserName(senderUserName);
        alarm.setReceiverId(receiverId);
        alarm.setMessage(senderUserName + "님이 친구 신청을 보냈습니다!");
	    alarm.setUrl("member/friend");
	    
	    try {
	        restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
	    } catch (Exception e) {
	    	log.error("🔴 FriendAlarmMessageDTO 알람 전송 실패!", e);
	    }
	}

	
	
	
	
	
	
	
}
