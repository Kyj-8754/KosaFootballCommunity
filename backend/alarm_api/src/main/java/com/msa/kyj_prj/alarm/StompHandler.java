package com.msa.kyj_prj.alarm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor { // ✅ 인터페이스로 변경
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            log.info("🔗 WebSocket CONNECT 요청: {}", accessor.getUser());
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            log.info("📩 SUBSCRIBE 요청: {}", accessor.getDestination());
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            log.info("❌ DISCONNECT 요청");
        }

        return message;
    }
}
