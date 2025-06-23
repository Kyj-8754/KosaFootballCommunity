package com.msa.kyj_prj.alarm;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

// REST + STOMP 둘 다 지원하기 위해 @RestController 대신 @Controller 사용
@Controller
@RequestMapping("/api/alarm") // REST용 prefix
@RequiredArgsConstructor
public class AlarmController {

    private final SimpMessageSendingOperations messagingTemplate;

    // ✅ STOMP WebSocket 메시지 수신 (클라이언트가 WebSocket으로 보낼 때 사용)
    @MessageMapping("/alarm/message")
    public void handleAlarmMessage(AlarmMessageDTO message) {
        System.out.println("📥 STOMP 수신 메시지: " + message);

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + message.getClubId(),
                message
        );
    }

    // ✅ REST API 호출용 (club_api → alarm_api 알림 전송할 때 사용)
    @PostMapping("/send")
    @ResponseBody
    public void sendAlarmViaRest(@RequestBody AlarmMessageDTO message) {
        System.out.println("📥 REST 수신 메시지: " + message);

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + message.getClubId(),
                message
        );
    }
}
