<template>
  <div class="container">
    <Header />

    <div class="row">
      <div>
        <router-view />
      </div>
    </div>

    <AlarmToast />
      <Footer />
    <scrollUp />

    <!-- 위치 날씨 위젯 -->
    <div class="floating-weather-widget" ref="widget" @mousedown="startDrag">
      <weatherWidget />
    </div>
  </div>
</template>


<script setup>
import { provide, ref, computed, onMounted, watch, onBeforeUnmount } from 'vue';
import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';
import AlarmToast from '@/components/common/AlarmToast.vue';
import { connectWebSocket } from '@/utils/stomp';
import { useAlarmStore } from '@/stores/alarmStore';
import scrollUp from '@/components/scrollUp.vue'
import { injectSetToken } from '@/utils/tokenGenerator.js'
import weatherWidget from './components/widget/weatherWidget.vue';

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

const widget = ref(null)

let isDragging = false
let offsetX = 0
let offsetY = 0

const startDrag = (e) => {
  isDragging = true
  const rect = widget.value.getBoundingClientRect()
  offsetX = e.clientX - rect.left
  offsetY = e.clientY - rect.top

  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', endDrag)
}

// 상하로만 이동
const onDrag = (e) => {
  if (!isDragging) return;

  const widgetEl = widget.value;
  const widgetRect = widgetEl.getBoundingClientRect();
  const widgetHeight = widgetRect.height;
  const viewportHeight = window.innerHeight;

  // ❌ left는 고정 (초기 위치 유지)
  const left = widgetEl.offsetLeft;

  // ✅ top만 계산
  let bottom = viewportHeight - (e.clientY + offsetY);

  // ✅ 화면 위아래로 나가지 않도록 제한
  if (bottom < 0) bottom = 0;
  if (bottom + widgetHeight > viewportHeight) {
    bottom = viewportHeight - widgetHeight;
  }

  widgetEl.style.left = `${left}px`;
  widgetEl.style.bottom = `${bottom}px`;
  widgetEl.style.right = 'auto';
};

const endDrag = () => {
  isDragging = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', endDrag)
}

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

<style scoped>
.floating-weather-widget {
  position: fixed;
  bottom: 0px;
  left: 0px;
  z-index: 999;
  cursor: grab;
}

.floating-weather-widget:active {
  cursor: grabbing;
}
</style>