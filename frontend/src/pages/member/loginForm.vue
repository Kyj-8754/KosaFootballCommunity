<template>
  <div class="logincontainer">
    <form class="login-form" @submit.prevent="login">
      <h2 class="text-center mb-4 fw-bold">로그인</h2>

      <div class="mb-3">
        <label for="userid" class="form-label">아이디</label>
        <input
          v-model="userId"
          type="text"
          name="userid"
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
          name="passwd"
          id="passwd"
          class="form-control"
          placeholder="비밀번호를 입력해주세요"
        />
      </div>

      <button type="submit" class="btn btn-primary w-100">로그인</button>
    </form>

    <hr class="my-4" />

    <!-- 소셜 로그인 영역 -->
    <div class="social-login">
      <div class="mb-3">
        <div id="google-container"></div>
      </div>

      <div class="mb-3" id="naverIdLogin"></div>

      <div class="mb-3">
        <a :href="kakaoLoginUrl" class="btn btn-warning w-100">
          <img
            src="//k.kakaocdn.net/14/dn/btqCn0WEmI3/nijroPfbpCa4at5EIsjyf0/o.jpg"
            alt="카카오 로그인"
            style="max-width: 400px; max-height: 60px; width: 100%"
          />
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const userId = ref('')
const userPwd = ref('')
const token = inject('token')
const router = useRouter()

// 일반 로그인
const login = async () => {
  try {
    const res = await axios.post(
      '/api/generateToken',
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

// Kakao 로그인 페이지 리디렉션 방식
const kakaoLoginUrl =
  'https://kauth.kakao.com/oauth/authorize?response_type=code' +
  '&client_id=eba49f92d2d08fc3bbcf6b90a7c4c8f8' +
  '&redirect_uri=http://localhost:8080/auth/kakao/callback'

// Google GIS 방식 핸들러
window.handleCredentialResponse = async (response) => {
  const idToken = response.credential
  console.log('구글 ID Token:', idToken)
  try {
    const res = await axios.post('http://localhost:8080/auth/google/callback', {
      idToken
    }, {
      withCredentials: true
    })
    localStorage.setItem('accessToken', res.data.accessToken)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    token.value = res.data.accessToken
    router.push('/')
  } catch (err) {
    console.error('구글 로그인 실패:', err.response?.data || err.message)
    alert('구글 로그인 실패')
  }
}

// SDK 로딩
onMounted(() => {
  // Google Identity Services (GIS)
  const googleScript = document.createElement('script')
  googleScript.src = 'https://accounts.google.com/gsi/client'
  googleScript.async = true
  googleScript.defer = true
  googleScript.onload = () => {
    window.google.accounts.id.initialize({
      client_id: '479145555361-ajgg3cbgeug85vthaa1k6v2h9tt6vu8.apps.googleusercontent.com',
      callback: handleCredentialResponse,
    })
    window.google.accounts.id.renderButton(
      document.getElementById('google-container'),
      { theme: 'outline', size: 'large' }
    )
  }
  document.head.appendChild(googleScript)

  // Kakao
  const kakaoScript = document.createElement('script')
  kakaoScript.src = 'https://developers.kakao.com/sdk/js/kakao.js'
  kakaoScript.onload = () => {
    window.Kakao.init('eba49f92d2d08fc3bbcf6b90a7c4c8f8')
  }
  document.head.appendChild(kakaoScript)

  // Naver
  const naverScript = document.createElement('script')
  naverScript.src = 'https://static.nid.naver.com/js/naveridlogin_js_sdk_2.0.0.js'
  naverScript.charset = 'utf-8'
  naverScript.onload = () => {
    const naverLogin = new window.naver.LoginWithNaverId({
      clientId: 'rRcRANKAtJPn85gefRCh',
      callbackUrl: 'http://localhost:8080/auth/naver/callback',
      isPopup: false,
      loginButton: { color: 'green', type: 3, height: 60 },
    })
    naverLogin.init()
  }
  document.head.appendChild(naverScript)
})
</script>

<style scoped>
.logincontainer {
  max-width: 500px;
  margin: 100px auto;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
  background-color: white;
}
.social-login {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
</style>
