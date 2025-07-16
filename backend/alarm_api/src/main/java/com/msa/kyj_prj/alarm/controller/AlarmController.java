package com.msa.kyj_prj.alarm.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // spring만 import!
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.msa.kyj_prj.alarm.dto.Alarm;
import com.msa.kyj_prj.alarm.dto.AlarmMessageDTO;
import com.msa.kyj_prj.alarm.service.AlarmHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/alarm")
@RequiredArgsConstructor
@Tag(name = "Alarm", description = "알림(WebSocket/REST) API")
public class AlarmController {

	private final SimpMessageSendingOperations messagingTemplate;
	private final AlarmHistoryService alarmHistoryService;

	// ✅ STOMP 메시지 수신 (Swagger로 문서화 불가)
	@MessageMapping("/alarm/message")
	public void handleAlarmMessage(AlarmMessageDTO message) {
		// ...생략 (동일)
	}

	@Operation(summary = "REST 방식 알림 전송", description = "AlarmMessageDTO 객체를 받아 알림 DB 저장 및 실시간 푸시(WebSocket)를 수행합니다.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "알림 전송/저장 성공"),
			@ApiResponse(responseCode = "400", description = "잘못된 요청") })
	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "알림 메시지 정보(AlarmMessageDTO) 예: { receiverId: '3', senderId: '7', type: 'club_apply', message: '신청이 도착했습니다', url: '/club/7' }")
	@PostMapping("/send")
	@ResponseBody
	public void sendAlarmViaRest(@RequestBody AlarmMessageDTO message // ← spring만 import!
	) {
		System.out.println("📥 REST 수신 메시지: " + message);
		System.out.println("🚩 브로드캐스트 경로: /topic/alarm/" + message.getReceiverId());

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
