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

const userId = ref('')
const userPwd = ref('')
const token = inject('token')
const router = useRouter()
const login = async () => {
  try {
    const response = await axios.post('/login_api/generateToken', {
      userId: userId.value,
      userPwd: userPwd.value
    },{
      withCredentials: true
    })
		console.log('✅ 로그인 응답:', response.data)
		const data = response.data
    // 로컬 스토리지에 저장
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)

		token.value = data.accessToken
    router.push('/')
  } catch (error) {
    if (error.response.status === 401) {
    	alert("로그인 실패: " + error.response.data.res_msg);
  	}else {
      alert("로그인 요청 중 오류가 발생했습니다.")
    }
		userId.value = ''
    userPwd.value = ''
  }
}
</script>

