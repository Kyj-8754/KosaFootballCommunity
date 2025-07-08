<template>
  <h1 class="text-center mb-4">아이디 찾기</h1>
  <form @submit.prevent="onSubmit" class="find-form">
    <div class="mb-3">
      <label for="name" class="form-label">이름</label>
      <input
        type="text"
        v-model="form.userName"
        id="name"
        class="form-control"
        placeholder="이름을 입력해주세요"
        required
      />
    </div>

    <div class="mb-3">
      <label for="phone" class="form-label">전화번호</label>
      <div class="d-flex gap-2">
        <input
          type="text"
          v-model="form.userPhone"
          id="phone"
          class="form-control"
          placeholder="전화번호를 입력해주세요"
          maxlength="11"/>
        <input
          type="button"
          value="인증 요청"
          class="btn btn-outline-primary"
          @click="sendSmsCode"/>
      </div>
    </div>

    <!-- 인증 코드 입력 -->
    <div class="mb-3" v-if="isCodeSent">
      <label for="smsCode" class="form-label">인증 코드</label>
      <div class="d-flex gap-2">
        <input
          type="text"
          v-model="smsCode"
          id="smsCode"
          class="form-control"
          placeholder="인증 코드를 입력해주세요"/>
        <input
          type="button"
          value="확인"
          class="btn btn-outline-success"
          @click="verifySmsCode"/>
      </div>
      <span
        v-if="smsCodeMsg"
        :class="smsCodeValid ? 'text-success' : 'text-danger'">
        {{ smsCodeMsg }}
      </span>
    </div>
    <div class="d-grid">
      <button type="submit" class="btn btn-primary">아이디 찾기</button>
    </div>
  </form>
</template>

<script setup>
import { ref,watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = ref({
  userName: '',
  userPhone: ''
})

const smsCode = ref('')
const smsCodeMsg = ref('')
const smsCodeValid = ref(false)
const isCodeSent = ref(false)

// 문자 전송
const sendSmsCode = async () => {
  if (!form.value.userPhone) {
    alert('전화번호를 입력해주세요')
    return
  }

  try {
    const res = await fetch('/login_api/user/na/smsCode', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ userPhone: form.value.userPhone })
    })

    const json = await res.json()
    if (json.res_code === '200') {
      alert('인증 코드가 전송되었습니다')
      isCodeSent.value = true
    } else {
      alert(json.res_msg || '전송 실패')
    }
  } catch (err) {
    console.error('SMS 전송 오류:', err)
    alert('서버 오류로 전송 실패')
  }
}

// 인증번호 검증
const verifySmsCode = async () => {
  try {
    const res = await fetch('/login_api/user/na/verify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        userPhone: form.value.userPhone,
        smsCode: smsCode.value
      })
    })

    const json = await res.json()
    if (json.res_code === '200') {
      smsCodeMsg.value = '인증에 성공했습니다.'
      smsCodeValid.value = true
    } else {
      smsCodeMsg.value = json.res_msg || '인증 실패'
      smsCodeValid.value = false
    }
  } catch (err) {
    console.error('인증 오류:', err)
    smsCodeMsg.value = '서버 오류로 인증 실패'
    smsCodeValid.value = false
  }
}

// 아이디 찾기 제출
const onSubmit = async () => {

  if (!smsCodeValid.value) {
  alert('전화번호 인증을 완료해주세요')
  return
  }

  try {
    const response = await fetch('/login_api/user/na/findId', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=utf-8' },
      body: JSON.stringify({
        userName: form.value.userName,
        userPhone: form.value.userPhone
      })

    })

    const json = await response.json()

    if (json.res_code === '200') {
      alert(json.res_msg)
      router.push('/member/loginForm')
    } else {
      alert(json.res_msg || '아이디 찾기에 실패했습니다.')
    }
  } catch (error) {
    console.error('아이디 찾기 요청 중 오류 발생:', error)
    alert('서버 오류로 아이디 찾기에 실패했습니다.')
  }
}

watch(() => form.value.userPhone, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    isCodeSent.value = false
    smsCodeValid.value = false
    smsCode.value = ''
    smsCodeMsg.value = ''
  }
})
</script>

<style scoped>
.find-form {
  max-width: 400px;
  margin: 0 auto;
}
</style>
