// 📁 src/stores/alarmStore.js
import { defineStore } from 'pinia';
import { useToast } from 'vue-toastification';

export const useAlarmStore = defineStore('alarm', {
  state: () => ({
    alarms: [],         // 알림 목록
    unread_count: 0,    // 읽지 않은 알림 수
    currentUserId: null  // 🔒 (선택) 로그인 유저 ID 연동 시 사용
  }),

  actions: {
    // ✅ 로그인 시 호출
      setCurrentUserNo(userNo) { 
      this.currentUserNo = userNo;
    },
        
    
    // ✅ WebSocket 수신 시 호출
    pushAlarm(alarm) {
      const toast = useToast();

    // ✅ 내 user_no와 수신자(receiverId)가 다르면 무시 (보안/최적화)
      if (this.currentUserNo && String(alarm.receiverId) !== String(this.currentUserNo)) return;

      // 한국 시간대로 바꾸도록
      const getKoreanDateYYYYMMDD = () => {
          const now = new Date(); // 현재 시각 (서버의 시간대를 따름, 예: UTC)

          // 서버의 시간대와 상관없이 'Asia/Seoul' 시간대의 날짜를 얻기 위함
          // toLocaleString()으로 특정 시간대의 날짜/시간 문자열을 얻고, 이를 다시 Date 객체로 파싱
          const dateInKST = new Date(now.toLocaleString('en-US', { timeZone: 'Asia/Seoul' }));
          
          // 연, 월, 일 값을 추출합니다.
          const year = dateInKST.getFullYear();
          const month = (dateInKST.getMonth() + 1).toString().padStart(2, '0'); // 월은 0부터 시작하므로 +1
          const day = dateInKST.getDate().toString().padStart(2, '0');

          return `${year}${month}${day}`;
      };

      const today = getKoreanDateYYYYMMDD();

      // 📌 알림 추가
      this.alarms.unshift({
        ...alarm,
        read: false,
        received_at: today,
      });
      this.unread_count++;

      // ✅ 토스트 출력
      toast.success(alarm.message || '🔔 새로운 알림이 도착했습니다.', {
        position: 'bottom-right',
        timeout: 5000
      });
    },

    markAllAsRead() {
      this.alarms.forEach(alarm => alarm.read = true);
      this.unread_count = 0;
    },

    markAsRead(index) {
      if (!this.alarms[index].read) {
        this.alarms[index].read = true;
        this.unread_count = Math.max(0, this.unread_count - 1);
      }
    },

    clearAlarms() {
      this.alarms = [];
      this.unread_count = 0;
    },

  }
});
