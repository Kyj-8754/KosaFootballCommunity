<template>
  <form @submit.prevent="onSubmit" style="max-width: 400px; margin: auto;">
    <h2>비밀번호 변경</h2>
    <p>안전한 비밀번호로 내 정보를 보호하세요</p>
    <p style="color: red;">
      · 다른 아이디/사이트에서 사용한 적 없는 비밀번호<br>
      · 이전에 사용한 적 없는 비밀번호가 안전합니다.
    </p>

    <div class="form-group mb-3">
      <label>현재 비밀번호</label>
      <input type="password" v-model="form.currentPassword" required class="form-control" />
    </div>

    <div class="form-group mb-3">
      <label>새 비밀번호</label>
      <input type="password" v-model="form.newPassword" required class="form-control" />
      <span v-if="passwdMsg" class="text-danger">{{ passwdMsg }}</span>
    </div>

    <div class="form-group mb-3">
      <label>새 비밀번호 확인</label>
      <input type="password" v-model="form.confirmNewPassword" required class="form-control" />
    </div>

    <div class="form-group mt-4 d-flex gap-2">
      <input type="submit" value="확인" class="btn btn-success" />
      <input type="button" value="취소" class="btn btn-secondary" @click="onCancel" />
    </div>
  </form>
</template>

<script setup>
import { reactive, ref, inject } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const token = inject('token')
const userNo = inject('userNo')

const form = reactive({
  currentPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

const passwdMsg = ref('')

// 제출 처리
const onSubmit = async () => {
  console.log(userNo)
  const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]).{8,}$/

  if (!pwRegex.test(form.newPassword)) {
    passwdMsg.value = '영문자, 숫자, 특수문자를 포함한 8자 이상이어야 합니다'
    form.newPassword = ''
    form.confirmNewPassword = ''
    return
  }

  if (form.newPassword !== form.confirmNewPassword) {
    passwdMsg.value = '비밀번호가 일치하지 않습니다'
    form.newPassword = ''
    form.confirmNewPassword = ''
    return
  }

  passwdMsg.value = ''

  try {
    const response = await fetch('/login_api/user/updatePassword', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json;charset=utf-8',
        Authorization: `Bearer ${token.value}`
      },
      body: JSON.stringify({
        currentPassword: form.currentPassword,
        newPassword: form.newPassword,
        userNo : userNo.value
      })
    })

    const json = await response.json()

    if (json.res_code === '200') {
      alert(json.res_msg)
      router.push({ name: 'Member_MyPage', query: { userNo: userNo.value } })
    } else {
      alert(json.res_msg || '비밀번호 변경에 실패했습니다.')
    }
  } catch (error) {
    console.error('비밀번호 변경 중 오류 발생:', error)
    alert('서버 오류로 비밀번호 변경에 실패했습니다.')
  }
}

// 입력 초기화
const onCancel = () => {
  form.currentPassword = ''
  form.newPassword = ''
  form.confirmNewPassword = ''
  passwdMsg.value = ''
}
</script>
