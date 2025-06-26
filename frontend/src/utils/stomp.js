// 📁 src/utils/stomp.js
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let stompClient = null;

/**
 * WebSocket 연결 및 사용자별 알림 구독
 * @param {string} userId - 로그인된 사용자 ID
 * @param {function} onMessage - 수신 시 실행할 콜백 함수
 */
export function connectWebSocket(userId, onMessage) {
  if (!userId) {
    console.warn('❗ userId가 없습니다. WebSocket 연결이 생략됩니다.');
    return;
  }

  const socket = new SockJS('http://localhost:8086/ws'); // WebSocket 백엔드 URL
  stompClient = Stomp.over(socket);

// ✅ 로그 비활성화 (에러 방지 방식)
  stompClient.debug = () => {}; // ← 여기가 중요!
  

  stompClient.connect({}, () => {
    console.log('🟢 WebSocket 연결 성공');

    // 📨 사용자별 알림 구독
    const topicPath = `/topic/alarm/${userId}`;
    stompClient.subscribe(topicPath, (message) => {
      try {
        const data = JSON.parse(message.body);
        console.log('📩 알림 수신:', data);
        onMessage?.(data); // 콜백 함수 존재 시 실행
      } catch (e) {
        console.error('🔴 메시지 파싱 오류:', e);
      }
    });

  }, (error) => {
    console.error('🔴 WebSocket 연결 실패:', error);
  });
}

/**
 * WebSocket 연결 해제
 */
export function disconnectWebSocket() {
  if (stompClient?.connected) {
    stompClient.disconnect(() => {
      console.log('🛑 WebSocket 연결 해제');
    });
  }
}
