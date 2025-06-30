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

      <button type="submit" class="btn btn-primary">로그인</button>
    </form>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

// ✅ [추가] Pinia userStore import
import { useUserStore } from '@/stores/userStore'
const userStore = useUserStore()

const userId = ref('')
const userPwd = ref('')
const token = inject('token')
const router = useRouter()

const login = async () => {
  try {
    const response = await axios.post('/login_api/generateToken', {
      userId: userId.value,
      userPwd: userPwd.value
    }, {
      withCredentials: true
    })
    console.log('✅ 로그인 응답:', response.data)
    const data = response.data

    // ✅ 큰을 로컬스토리지에 저장
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    token.value = data.accessToken

    // ✅ 로그인 성공 시 userStore에도 정보 저장!
    // 반드시 서버 응답에서 user_no, user_name, accessToken(토큰)이 넘어와야 한다.
    userStore.setUser({
      user_no: data.user_no,         // 서버에서 내려준 유저 PK (DB 컬럼명)
      user_name: data.user_name,     // 서버에서 내려준 이름 (DB 컬럼명)
      jwt: data.accessToken          // 서버에서 내려준 accessToken (토큰)
    })

    // ✅ 홈 화면으로 이동
    router.push('/')
  } catch (error) {
    if (error.response && error.response.status === 401) {
      alert("로그인 실패: " + error.response.data.res_msg)
    } else {
      alert("로그인 요청 중 오류가 발생했습니다.")
    }
    userId.value = ''
    userPwd.value = ''
  }
}
</script>
