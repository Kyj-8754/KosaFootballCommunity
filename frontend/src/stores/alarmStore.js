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


      // 📌 알림 추가
      this.alarms.unshift({
        ...alarm,
        read: false,
        received_at: new Date(),
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
