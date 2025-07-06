package com.msa.kyj_prj.alarm;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.RequiredArgsConstructor;

/**
 * ✅ STOMP(WebSocket) + REST API 혼용 컨트롤러 ⚠️ 프록시 설정에 의해 /alarm_api → /alarm 으로
 * rewrite 됨 (vite.config.js 참고)
 */
@Controller
//@JsonTypeInfo 
//@JsonSubTypes
@RequestMapping("/alarm") // 프론트 요청: /alarm_api → 백엔드 실제 경로: /alarm
@RequiredArgsConstructor
public class AlarmController {

	private final SimpMessageSendingOperations messagingTemplate;

	/**
	 * ✅ STOMP 메시지 수신 (프론트 stompClient.send 로 보낼 때 사용) 예:
	 * stompClient.send('/alarm/message', {}, JSON.stringify(...))
	 */
	@MessageMapping("/alarm/message")
	public void handleAlarmMessage(AlarmMessage message) {
		System.out.println("📥 STOMP 수신 메시지: " + message);
		System.out.println("🚩 브로드캐스트 경로: /topic/alarm/" + message.getReceiverId());

		messagingTemplate.convertAndSend("/topic/alarm/" + message.getReceiverId(), message);
	}

	/**
	 * ✅ REST 방식 알림 전송 (다른 서버에서 호출 가능) 프론트 요청: POST /alarm_api/send → 실제 백엔드 매핑:
	 * POST /alarm/send
	 */
	@PostMapping("/send")
	@ResponseBody
	public void sendAlarmViaRest(@RequestBody AlarmMessage message) {
		System.out.println("📥 REST 수신 메시지: " + message);
		System.out.println("🚩 브로드캐스트 경로: /topic/alarm/" + message.getReceiverId());
		messagingTemplate.convertAndSend("/topic/alarm/" + message.getReceiverId(), message);
	}
}
