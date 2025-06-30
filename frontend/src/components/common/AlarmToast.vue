<template>
  <!-- 최신 알림 있을 때만 표시 -->
  <div class="toast-container" v-if="visible && latestAlarm" :key="latestAlarm.message">
    <div class="toast">
      <p>{{ latestAlarm.message }}</p>  <!-- ✅ 실제 필드는 message -->
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { useAlarmStore } from '@/stores/alarmStore';

const store = useAlarmStore();
const visible = ref(false);

// 🔽 최신 알림 메시지 가져오기 (store 배열의 첫 항목)
const latestAlarm = computed(() => store.alarms[0]);

// 🔔 알림이 새로 생기면 자동으로 토스트 보여주기
watch(
  () => store.alarms.length,
  (newLength, oldLength) => {
    if (newLength > oldLength && latestAlarm.value) {
      visible.value = true;
      setTimeout(() => {
        visible.value = false;
      }, 3000); // 3초 후 자동 숨김
    }
  }
);
</script>

<style scoped>
.toast-container {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}
.toast {
  background-color: #333;
  color: white;
  padding: 12px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.3);
  transition: all 0.3s ease-in-out;
  opacity: 0.95;
}
</style>
