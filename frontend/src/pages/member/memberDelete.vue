<template>
  <form @submit.prevent="onSubmit" style="max-width: 400px; margin: auto;">
    <h2>회원 탈퇴</h2>
    <div class="form-group mb-3">
      <label for="password">비밀번호를 입력하세요</label>
      <input v-model="password" id="password" type="password" required class="form-control" />
    </div>
    <button type="submit" class="btn btn-danger w-100">회원 탈퇴</button>
  </form>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const token = inject('token')
const logout = inject('logout')
const loginType = inject('loginType')
const route = useRoute()

const password = ref('')

const onSubmit = async () => {
  console.log('전달할 loginType:', loginType.value)
  const userNo = route.query.userNo
  if (!userNo) {
    alert('회원 정보가 없습니다.')
    return
  }

  const confirmDelete = confirm('정말로 탈퇴하시겠습니까?')
  if (!confirmDelete) return

  try {
    const res = await axios.post('/login_api/user/deleteMember', {
      userNo,
      password: password.value,
      loginType: loginType.value
    }, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    })

    alert(res.data.res_msg)

    if (res.data.res_code === '200') {
      logout()
    }
  } catch (err) {
    console.error('회원 탈퇴 오류:', err)
    alert('회원 탈퇴에 실패했습니다.')
  }
}
</script>
