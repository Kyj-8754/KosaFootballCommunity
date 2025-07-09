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
      <div>
        <router-link :to="{ name: 'Member_FindId'}" class="text-decoration-none">아이디 찾기 | </router-link>
        <router-link :to="{ name: 'Member_FindPwd'}" class="text-decoration-none">비밀번호 찾기</router-link>
      </div>
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
import { ref, inject, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

// ✅ [추가] Pinia userStore import
import { useUserStore } from '@/stores/userStore'
const userStore = useUserStore()

const userId = ref('')
const userPwd = ref('')
const router = useRouter()
const route = useRoute()
const token = inject('token')

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
		console.error("❌ 실패 상태코드:", err.response?.status);
  	console.error("❌ 실패 응답:", err.response?.data);
  }
}

onMounted(() => {
  const accessToken = route.query.accessToken
  const refreshToken = route.query.refreshToken

  // 쿼리 파라미터가 있을 때만 소셜 로그인 처리
  if (accessToken !== undefined || refreshToken !== undefined) {
    if (accessToken && refreshToken) {
      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', refreshToken)
      if (token) token.value = accessToken
      router.push('/')
    } else {
      alert('소셜 로그인 실패')
      router.push('/login')
    }
  }
})

// ✅ 소셜 로그인 URL 백엔드에서 받아 리디렉트
const loginWith = async (provider) => {
  try {
    const res = await axios.get(`/login_api/oauth/authorize/${provider}`)
    const url = res.data.authorizationUrl
    window.location.href = url
  } catch (err) {
    const response = err.response?.data
    
    if (response?.res_code === 'need_register') {

      const { provider, providerId, nickname, profileImage } = response

      router.push({
        name: 'Member_SocialRegister', // ← 문자열 끝에 공백 있었던 거 제거
        query: {
          provider,
          providerId,
          nickname,
          profileImage
        }
      })
    } // ✅ 로그인 성공 시 토큰 저장 및 페이지 이동
    else if (response?.res_code === '200' && response.accessToken) {
      localStorage.setItem('accessToken', response.accessToken)
      localStorage.setItem('refreshToken', response.refreshToken)
      token.value = response.accessToken
      router.push('/')
    }else {
      alert(`소셜 로그인 실패: ${provider}`)
      console.error(err)
    }
  }
}
</script>
