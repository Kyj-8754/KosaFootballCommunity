package com.msa.kyj_prj.alarm;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final SimpMessageSendingOperations messagingTemplate;

    public void processAlarm(AlarmMessageDTO message) {
        String type = message.getType();

        if (type == null) {
            System.out.println("❌ 메시지 타입이 null입니다.");
            return;
        }

        switch (type) {
            case "JOIN_REQUEST" -> {
                System.out.println("🟡 가입 신청 요청 처리 중...");
                // 팀장에게 실시간 알림 전송
                messagingTemplate.convertAndSendToUser(
                        message.getReceiverId(),
                        "/queue/alert",
                        message
                );
            }

            case "JOIN_APPROVED" -> {
                System.out.println("🟢 가입 승인 처리 중...");
                // 신청자에게 실시간 알림 전송
                messagingTemplate.convertAndSendToUser(
                        message.getReceiverId(),
                        "/queue/alert",
                        message
                );
            }

            default -> {
                System.out.println("ℹ️ 일반 알림 처리");
                // 특정 클럽 방에 알림 전송
                messagingTemplate.convertAndSend(
                        "/sub/chat/room/" + message.getClubId(),
                        message
                );
            }
        }
    }
}
