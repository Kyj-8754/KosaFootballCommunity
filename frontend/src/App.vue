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
    <scrollUp />
  </div>
</template>

<script setup>
import { provide, ref, computed, onMounted, watch } from 'vue';
import Header from '@/components/Header.vue';
import NavArea from '@/components/NavArea.vue';
import Footer from '@/components/Footer.vue';
import AlarmToast from '@/components/common/AlarmToast.vue';
import { connectWebSocket } from '@/utils/stomp';
import { useAlarmStore } from '@/stores/alarmStore';
import scrollUp from '@/components/scrollUp.vue'
import { injectSetToken } from '@/utils/tokenGenerator.js'

const alarmStore = useAlarmStore();



const token = ref(localStorage.getItem('accessToken') || '')
// 토큰 설정 함수
const setToken = (newToken) => {
  token.value = newToken
  if (newToken) {
    localStorage.setItem('accessToken', newToken)
  } else {
    localStorage.removeItem('accessToken')
  }
}
injectSetToken(setToken)

onMounted(() => {
  const savedToken = localStorage.getItem('accessToken')
  if (savedToken) {
    token.value = savedToken
  }
})

// ✅ JWT Payload 디코딩 함수
const decodeJwtPayload = (tokenStr) => {
  try {
    const base64Payload = tokenStr.split('.')[1]
    const decoded = atob(base64Payload)
    const payload = JSON.parse(decoded)
    
    // userName만 디코딩 (서버에서 encode 했을 경우만)
    if (payload.userName) {
      payload.userName = decodeURIComponent(payload.userName.replace(/\+/g, ' '))
    }

    return payload
  } catch (e) {
    console.error("JWT 디코딩 실패:", e)
    return {}
  }
}

// payload에서 각 속성 추출 (token이 null이면 null 반환)
const payload = computed(() => token.value ? decodeJwtPayload(token.value) : {})
const userId = computed(() => payload.value.userId || null)
const userNo = computed(() => payload.value.userNo || null)
const userName = computed(() => payload.value.userName || null)
const authCode = computed(() => payload.value.authCode || null)
const loginType = computed(() => payload.value.loginType || null)

// 로그아웃 함수
const logout = () => {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  token.value = null
  location.href = '/'
}

// 전역으로 token만 제공
provide('setToken', setToken)
provide('token', token)
provide('logout', logout)
provide('userId', userId)
provide('userNo', userNo)
provide('userName', userName)
provide('authCode', authCode)
provide('loginType', loginType)


onMounted(() => {

  // ✅ JWT에서 추출한 userNo를 사용해 웹소켓 연결
  if (userNo.value) {
    connectWebSocket(userNo.value, (msg) => {
      alarmStore.pushAlarm(msg);
    });
  }
  // ✅ userNo 값이 바뀔 때(로그인/로그아웃) 웹소켓 연결 재설정
  watch(userNo, (newNo, oldNo) => {
    if (newNo) {
      connectWebSocket(newNo, (msg) => {
        alarmStore.pushAlarm(msg);
      });
    }
    // (참고: 필요하면 이전 소켓 연결 해제 로직 추가 가능)
  });
  // ⚠️ 추후 로그인 연동 시 userNo 값을 JWT에서 동적으로 할당하도록 수정
});

</script>
