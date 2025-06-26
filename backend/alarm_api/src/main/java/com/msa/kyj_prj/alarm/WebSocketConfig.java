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
    // ✅ 클라이언트에서 연결할 WebSocket 엔드포인트
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // ✅ CORS 유연 대응 (Spring 5.3 이상)
                .withSockJS();                  // ✅ SockJS fallback 지원
    }

    // ✅ STOMP 메시지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");          // ✅ 클라이언트가 subscribe할 경로 (알림 수신)
        registry.setApplicationDestinationPrefixes("/alarm"); // ✅ 클라이언트가 send할 경로 (알림 발신)
    }

    // ⛔ 인증 관련 핸들러는 현재 사용 안 함 (로그인 연동 시 활성화 가능)
    // @Autowired
    // private StompHandler stompHandler;
    //
    // @Override
    // public void configureClientInboundChannel(ChannelRegistration registration) {
    //     registration.interceptors(stompHandler);
    // }
}