package com.msa.kyj_prj.alarm.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.msa.kyj_prj.alarm.dao.AlarmMessage;
import com.msa.kyj_prj.alarm.dto.Alarm;
import com.msa.kyj_prj.alarm.dto.AlarmMessageDTO;
import com.msa.kyj_prj.alarm.service.AlarmHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ✅ STOMP(WebSocket) + REST API 혼용 컨트롤러 ⚠️ 프록시 설정에 의해 /alarm_api → /alarm 으로
 * rewrite 됨 (vite.config.js 참고)
 */
@Controller
@Slf4j
@RequestMapping("/alarm") // 프론트 요청: /alarm_api → 백엔드 실제 경로: /alarm
@RequiredArgsConstructor
public class AlarmController {

	private final SimpMessageSendingOperations messagingTemplate;

	private final AlarmHistoryService alarmHistoryService;

//	 ✅ STOMP 메시지 수신 (프론트 stompClient.send 로 보낼 때 사용) 예:
//	  stompClient.send('/alarm/message', {}, JSON.stringify(...))

	@MessageMapping("/alarm/message")
	public void handleAlarmMessage(AlarmMessageDTO message) {
		System.out.println("📥 STOMP 수신 메시지: " + message);
		System.out.println("🚩 브로드캐스트 경로: /topic/alarm/" + message.getReceiverId());

		// 1. 실시간(WebSocket) 알림 push
		messagingTemplate.convertAndSend("/topic/alarm/" + message.getReceiverId(), message);

		// 2. 알림함(DB)에도 저장!
		Alarm alarm = new Alarm();
		alarm.setReceiver_id(Integer.parseInt(message.getReceiverId())); // user_no는 int로 변환
		try {
			alarm.setSender_id(Integer.parseInt(message.getSenderId()));
		} catch (Exception e) {
			alarm.setSender_id(0);
		}
		alarm.setType(message.getType());
		alarm.setMessage(message.getMessage());
		alarm.setUrl(message.getUrl());
		// created_at/read_yn 등은 DB/서비스에서 자동 처리

		alarmHistoryService.registerAlarm(alarm);
	}

	
//	  ✅ REST 방식 알림 전송 (다른 서버에서 호출 가능) 프론트 요청: POST /alarm_api/send → 실제 백엔드 매핑:
//	  POST /alarm/send
	 

	@PostMapping("/send")
	@ResponseBody
	public void sendAlarmViaRest(@RequestBody AlarmMessageDTO message) {
		System.out.println("📥 REST 수신 메시지: " + message);
		System.out.println("🚩 브로드캐스트 경로: /topic/alarm/" + message.getReceiverId());

		// 알림함(DB)에도 저장!
		log.info("알람 받아서 DB저장하기 =" + message.toString());
		Alarm alarm = new Alarm();
		alarm.setReceiver_id(Integer.parseInt(message.getReceiverId()));
		try {
			alarm.setSender_id(Integer.parseInt(message.getSenderId()));
		} catch (Exception e) {
			alarm.setSender_id(0);
		}
		alarm.setType(message.getType());
		alarm.setMessage(message.getMessage());
		alarm.setUrl(message.getUrl());

		alarmHistoryService.registerAlarm(alarm);

		messagingTemplate.convertAndSend("/topic/alarm/" + message.getReceiverId(), message);

	}

}
