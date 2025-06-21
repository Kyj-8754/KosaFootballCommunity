package com.msa.kyj_prj.alarm;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AlarmController {

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/alarm/message")
    public void handleAlarmMessage(AlarmMessageDTO message) {
        // 콘솔에 수신 로그 출력
        System.out.println("📥 STOMP 수신 메시지: " + message);

        // 테스트용: 클럽 ID 기준으로 구독자들에게 메시지 전송
        messagingTemplate.convertAndSend(
        	    "/sub/chat/room/" + message.getClubId(),
        	    message  // 전체 객체 그대로 보내기
        	);

    }
}
