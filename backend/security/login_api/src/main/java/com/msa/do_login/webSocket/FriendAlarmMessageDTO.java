package com.msa.do_login.webSocket;

import lombok.Data;

@Data
public class FriendAlarmMessageDTO implements AlarmMessage {
		private String type;         // 예: 친구 신청, 승인/거절
	    private String senderId;     // 보낸 사람
	    private String senderUserName; // 보낸 사람 이름
	    private String receiverId;   // 받는 사람
	    private String message;      // 메시지 내용
	    private String url;          // 알림 클릭 시 이동 경로

	 // private 
//	    private 
	    
	    public FriendAlarmMessageDTO(String type, String senderId, String receiverId, String message, String url) {
	        this.type = type;
	        this.senderId = senderId;
	        this.receiverId = receiverId;
	    //  this.변수명 = 변수Id;
	        this.message = message;
	        this.url = url;
	    }
	    
	public FriendAlarmMessageDTO() {}
}
