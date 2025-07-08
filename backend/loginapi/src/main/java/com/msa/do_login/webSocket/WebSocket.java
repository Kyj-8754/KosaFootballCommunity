package com.msa.do_login.webSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;

import com.msa.do_login.webSocket.FriendAlarmMessageDTO;

@Component // 반드시 추가!
@RequiredArgsConstructor
public class WebSocket {

	private final RestTemplate restTemplate;

	@Value("${alarm.api.url}")
	private String alarmApiUrl;
	
	
	
	// com.msa.do_login.webSocket.Websocket.java
	public void sendFriendRequestAlarm(String type, String senderId, String senderUserName, String receiverId) {
	  
	    FriendAlarmMessageDTO alarm = new FriendAlarmMessageDTO();
	    alarm.setType("FRIEND_REQUEST");
        alarm.setSenderId(senderId);
        alarm.setSenderUserName(senderUserName);
        alarm.setReceiverId(receiverId);
        alarm.setMessage(senderUserName + "님이 친구 신청을 보냈습니다!");
	    alarm.setUrl("/friend");
	    
	    try {
	        restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
	    } catch (Exception e) {
	        // 필요시 로그만 남김
	    }
	}

	
	
	
	
	
	
	
}
