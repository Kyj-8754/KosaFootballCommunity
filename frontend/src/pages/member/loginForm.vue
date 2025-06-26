<template>
  <div class="logincontainer">
    <form class="login-form" @submit.prevent="login">
      <h2 class="text-center mb-4 fw-bold">로그인</h2>

      <div class="mb-3">
        <label for="userid" class="form-label">아이디</label>
        <input
          v-model="userId"
          type="text"
          id="userid"
          class="form-control"
          placeholder="아이디를 입력해주세요"
        />
      </div>

      <div class="mb-3">
        <label for="passwd" class="form-label">비밀번호</label>
        <input
          v-model="userPwd"
          type="password"
          id="passwd"
          class="form-control"
          placeholder="비밀번호를 입력해주세요"
        />
      </div>

      <button type="submit" class="btn btn-primary w-100">로그인</button>
    </form>

    <hr class="my-4" />

    <!-- 소셜 로그인 -->
    <div class="social-login">
      <button class="btn btn-danger w-100" @click="loginWith('google')">Google 로그인</button>
      <button class="btn btn-success w-100" @click="loginWith('naver')">Naver 로그인</button>
      <button class="btn btn-warning w-100" @click="loginWith('kakao')">Kakao 로그인</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const userId = ref('')
const userPwd = ref('')
const router = useRouter()

const login = async () => {
  try {
    const res = await axios.post(
      '/security_api/generateToken',
      {
        userId: userId.value,
        userPwd: userPwd.value,
      },
      { withCredentials: true }
    )

    localStorage.setItem('accessToken', res.data.accessToken)
    localStorage.setItem('refreshToken', res.data.refreshToken)
		token.value = res.data.accessToken
    router.push('/')
  } catch (err) {
    alert('로그인 실패: ' + (err.response?.data?.res_msg || '오류 발생'))
    userId.value = ''
    userPwd.value = ''
  }
}

// ✅ 소셜 로그인 URL 백엔드에서 받아 리디렉트
const loginWith = async (provider) => {
  try {
    const res = await axios.get(`/login_api/oauth/authorize/${provider}`)
    const url = res.data.authorizationUrl
    window.location.href = url
  } catch (err) {
    alert(`소셜 로그인 실패: ${provider}`)
  }
}
</script>
