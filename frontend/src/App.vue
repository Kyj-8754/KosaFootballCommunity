<template>
  <div class="container">
    <Header />

    <div class="row">
      <div class="col-md-2">
        <NavArea />
      </div>
      <div class="col-md-10">
        <router-view />
      </div>
    </div>

    <AlarmToast /> <!-- 🔔 알림 토스트 전역 표시 -->

    <Footer />
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import Header from '@/components/Header.vue';
import NavArea from '@/components/NavArea.vue';
import Footer from '@/components/Footer.vue';
import AlarmToast from '@/components/common/AlarmToast.vue';
import { connectWebSocket } from '@/utils/stomp';
import { useAlarmStore } from '@/stores/alarmStore';

const alarmStore = useAlarmStore();

onMounted(() => {
  // ✅ 테스트용 유저 ID 하드코딩 (알림 수신 대상)
  const testUserId = 'user001';

  // ✅ WebSocket 연결 및 메시지 수신 시 알림 push
  connectWebSocket(testUserId, (msg) => {
    alarmStore.pushAlarm(msg);
  });

  // ⚠️ 추후 로그인 연동 시 여기서 실제 사용자 정보에서 userId 가져오도록 수정 필요
});
</script>
