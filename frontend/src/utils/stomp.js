// 📁 src/utils/stomp.js
import { useToast } from 'vue-toastification';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';


let stompClient = null;

/**
 * WebSocket 연결 및 사용자별 알림 구독
 * @param {int} userNo - 로그인된 사용자 ID
 * @param {function} onMessage - 수신 시 실행할 콜백 함수
 */
export function connectWebSocket(userNo, onMessage) {
  if (!userNo) {
    console.warn('❗ userNo가 없습니다. WebSocket 연결이 생략됩니다.');
    return;
  }

  const stompClient = Stomp.over(() => new SockJS('http://localhost:8120/ws')); // 🟢 factory 전달, auto reconnect OK


// ✅ 로그 비활성화 (에러 방지 방식)
   stompClient.debug = () => {}; // ← 로그 비활성화

  stompClient.connect({}, () => {

    const topicPath = `/topic/alarm/${userNo}`; // ✅ PK 기준 경로

    stompClient.subscribe(topicPath, (message) => {
      try {
        const data = JSON.parse(message.body);
        if (onMessage) onMessage(data); // 콜백 함수 존재 시 실행
        // 또는 여기서 바로 토스트 출력도 가능
        // const toast = useToast();
        // toast.success(data.message || '새 알림!', { position: 'bottom-right' });
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
    });
  }
}