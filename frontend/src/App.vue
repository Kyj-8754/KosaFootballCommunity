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
     <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue'
import NavArea from './components/NavArea.vue'
import Footer from './components/Footer.vue'

import { provide, ref, computed, onMounted } from 'vue'

// 1. 토큰 상태
const token = ref('')

// 2. 토큰 설정 함수
const setToken = (newToken) => {
  token.value = newToken
  if (newToken) {
    localStorage.setItem('accessToken', newToken)
  } else {
    localStorage.removeItem('accessToken')
  }
}

// 3. 마운트 시 로컬스토리지에서 토큰 로딩
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
      payload.userName = decodeURIComponent(payload.userName)
    }

    return payload
  } catch (e) {
    console.error("JWT 디코딩 실패:", e)
    return {}
  }
}

// ✅ payload에서 각 속성 추출 (token이 null이면 null 반환)
const payload = computed(() => token.value ? decodeJwtPayload(token.value) : {})

const userId = computed(() => payload.value.userId || null)
const userNo = computed(() => payload.value.userNo || null)
const userName = computed(() => payload.value.userName || null)
const authCode = computed(() => payload.value.authCode || null)

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
</script>
