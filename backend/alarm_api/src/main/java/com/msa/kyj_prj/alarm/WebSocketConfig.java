package com.msa.kyj_prj.alarm;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
// @RequiredArgsConstructor // ⛔ StompHandler를 주입받지 않으므로 임시 제거
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // ⛔ 현재 미사용 처리 (추후 인증 처리 시 다시 활성화)
    // private final StompHandler stompHandler;

    // ✅ WebSocket 연결 엔드포인트 정의
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp").setAllowedOrigins("*").withSockJS();
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:5173").withSockJS();
    }

    // ✅ 메시지 브로커 설정 (SimpleBroker 사용)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/pub");
        registry.setUserDestinationPrefix("/user");
    }

    // ⛔ 인증 핸들러 비활성화 (나중에 필요 시 다시 활성화)
    // @Override
    // public void configureClientInboundChannel(ChannelRegistration registration) {
    //     registration.interceptors(stompHandler);
    // }
}
