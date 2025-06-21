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
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const userId = ref('')
const userPwd = ref('')
const router = useRouter()

const login = async () => {
  try {
    const response = await axios.post('/api/generateToken', {
      userId: userId.value,
      userPwd: userPwd.value
    },{
      withCredentials: true
    })

    const data = response.data
    if (data.res_code === "400") {
      alert(data.res_msg)
      userId.value = ''
      userPwd.value = ''
    } else {
      router.push('/')
    }
  } catch (error) {
    if (error.response && error.response.data?.res_msg) {
      alert(error.response.data.res_msg)
    } else {
      alert("로그인 요청 중 오류가 발생했습니다.")
    }
    userPwd.value = ''
  }
}
</script>

