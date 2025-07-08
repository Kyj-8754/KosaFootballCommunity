<template>
  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <div class="container d-flex justify-content-center align-items-center min-vh-100">
          <div class="card shadow p-4 rounded w-100" style="max-width: 600px;">
            <h1 class="text-center mb-4">회원가입</h1>
            <form @submit.prevent="onSubmit">
              <div class="mb-3 d-flex align-items-center gap-2">
                <div class="flex-grow-1">
                  <label for="userid" class="form-label">아이디</label>
                  <input type="text" v-model="form.userId" id="userid" class="form-control" placeholder="아이디를 입력해주세요" required>
                  <span v-if="useridMsg" class="text-danger">{{ useridMsg }}</span>
                </div>
                <input type="button" value="중복확인" class="btn btn-secondary mt-4" @click="checkDuplicateId">
              </div>
              <div class="mb-3">
                <label for="passwd" class="form-label">비밀번호</label>
                <input type="password" v-model="form.userPwd" id="passwd" class="form-control" placeholder="비밀번호를 입력해주세요" required>
                <span v-if="passwdMsg" class="text-danger">{{ passwdMsg }}</span>
              </div>

              <div class="mb-3">
                <label for="passwd2" class="form-label">비밀번호 확인</label>
                <input type="password" v-model="form.userPwd2" id="passwd2" class="form-control" placeholder="비밀번호를 다시 입력해주세요" required>
              </div>

              <div class="mb-3">
                <label for="name" class="form-label">이름</label>
                <input type="text" v-model="form.userName" id="name" class="form-control" placeholder="이름을 입력해주세요" required>
              </div>

              <div class="mb-3">
                <label for="birth" class="form-label">생년월일</label>
                <input type="date" v-model="form.userBirth" id="birth" class="form-control" required>
              </div>

              <div class="mb-3">
                <label class="form-label">성별</label>
                <select v-model="form.userGender" class="form-select" required>
                  <option value="" disabled>성별을 선택해주세요</option>
                  <option value="M">남성</option>
                  <option value="F">여성</option>
                </select>
              </div>

              <div class="mb-3">
                <label for="phone" class="form-label">전화번호</label>
                <div class="d-flex gap-2">
                  <input type="text" v-model="form.userPhone" id="phone" class="form-control" placeholder="전화번호를 입력해주세요" maxlength="11">
                  <input type="button" value="인증 요청" class="btn btn-outline-primary" @click="sendSmsCode">
                </div>
              </div>

              <div class="mb-3" v-if="isCodeSent">
                <label for="smsCode" class="form-label">인증 코드</label>
                <div class="d-flex gap-2">
                  <input type="text" v-model="smsCode" id="smsCode" class="form-control" placeholder="인증 코드를 입력해주세요">
                  <input type="button" value="확인" class="btn btn-outline-success" @click="verifySmsCode">
                </div>
                <span v-if="smsCodeMsg" :class="smsCodeValid ? 'text-success' : 'text-danger'">{{ smsCodeMsg }}</span>
              </div>

              <div class="mb-3 d-flex gap-2">
                <div class="flex-grow-1">
                  <label for="zipcode" class="form-label">우편번호</label>
                  <input type="text" v-model="form.userPostcode" id="zipcode" class="form-control" placeholder="우편번호를 입력해주세요" readonly>
                </div>
                <div class="mt-4">
                  <input type="button" value="우편찾기" class="btn btn-outline-secondary" @click="findZipcode">
                </div>
              </div>

              <div class="mb-3">
                <label for="address" class="form-label">주소</label>
                <input type="text" v-model="form.userAddr" id="address" class="form-control" placeholder="주소를 입력해주세요" readonly>
              </div>

              <div class="mb-4">
                <label for="detail_address" class="form-label">상세주소</label>
                <input type="text" v-model="form.userDetailAddr" id="detail_address" class="form-control" placeholder="상세주소를 입력해주세요">
              </div>

              <div class="d-flex justify-content-between">
                <input type="submit" value="회원가입" class="btn btn-primary">
                <input type="reset" value="초기화" class="btn btn-secondary" @click="onReset">
              </div>
            </form>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 회원가입 폼 상태
const form = reactive({
  userId: '',
  userPwd: '',
  userPwd2: '',
  userName: '',
  userBirth: '',
  userPhone: '',
  userPostcode: '',
  userAddr: '',
  userDetailAddr: '',
  userGender: ''
})

// 메시지 및 상태
const useridMsg = ref('')
const passwdMsg = ref('')
const validClicked = ref(false)

const smsCode = ref('')
const smsCodeMsg = ref('')
const smsCodeValid = ref(false)
const isCodeSent = ref(false)

// 아이디 중복 확인
const checkDuplicateId = async () => {
  if (!form.userId || form.userId.length < 8) {
    useridMsg.value = '아이디는 8자 이상이어야 합니다'
    validClicked.value = false
    return
  }

  try {
    const response = await fetch('/login_api/user/na/isExistUserId', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=utf-8' },
      body: JSON.stringify({ userId: form.userId })
    })

    const json = await response.json()

    if (json.existUserId) {
      useridMsg.value = `${form.userId}는 이미 존재하는 아이디입니다`
      validClicked.value = false
    } else {
      useridMsg.value = ''
      alert(`${form.userId}는 사용 가능한 아이디입니다`)
      validClicked.value = true
    }
  } catch (error) {
    alert('중복 확인 중 오류가 발생했습니다')
    console.error(error)
    validClicked.value = false
  }
}

// 문자 전송
const sendSmsCode = async () => {
  if (!form.userPhone) {
    alert('전화번호를 입력해주세요')
    return
  }

  try {
    // 1. 전화번호 중복 체크
    const checkRes = await fetch('/login_api/user/na/isExistPhone', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=utf-8' },
      body: JSON.stringify({ userPhone: form.userPhone })
    })

    const checkJson = await checkRes.json()
    if (checkJson.res_code === '409') {
      alert(checkJson.res_msg) // 이미 가입된 번호
      return
    }

    // 2. 인증 코드 요청
    const res = await fetch('/login_api/user/na/smsCode', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ userPhone: form.userPhone })
    })

    const json = await res.json()
    if (json.res_code === '200') {
      alert('인증 코드가 전송되었습니다')
      isCodeSent.value = true
    } else {
      alert(json.res_msg || '전송 실패')
    }
  } catch (err) {
    console.error('SMS 인증 오류:', err)
    alert('서버 오류로 인증 실패')
  }
}

// 인증번호 검증
const verifySmsCode = async () => {
  try {
    const res = await fetch('/login_api/user/na/verify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        userPhone: form.userPhone,
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

// 회원가입 제출
const onSubmit = async () => {
  if (!validClicked.value) {
    alert('아이디 중복 확인을 해주세요')
    return
  }

  const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]).{8,}$/
  if (!pwRegex.test(form.userPwd)) {
    passwdMsg.value = '영문자, 숫자, 특수문자를 포함한 8자 이상이어야 합니다'
    form.userPwd = ''
    form.userPwd2 = ''
    return
  }

  if (form.userPwd !== form.userPwd2) {
    passwdMsg.value = '비밀번호가 일치하지 않습니다'
    form.userPwd = ''
    form.userPwd2 = ''
    return
  }

  passwdMsg.value = ''
  const payload = { ...form }
  delete payload.userPwd2

  if (!smsCodeValid.value) {
  alert('전화번호 인증을 완료해주세요')
  return
  }

  try {
    const response = await fetch('/login_api/user/na/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=utf-8' },
      body: JSON.stringify(payload)
    })

    const json = await response.json()

    if (json.res_code === '200') {
      alert(json.res_msg)
      router.push('/member/loginForm')
    } else {
      alert(json.res_msg || '회원가입에 실패했습니다.')
    }
  } catch (error) {
    console.error('회원가입 요청 중 오류 발생:', error)
    alert('서버 오류로 회원가입에 실패했습니다.')
  }
}

// 입력 초기화
const onReset = () => {
  Object.keys(form).forEach(key => form[key] = '')
  useridMsg.value = ''
  passwdMsg.value = ''
  validClicked.value = false
}

// 다음 주소 API 로드
const findZipcode = () => {
  new window.daum.Postcode({
    oncomplete: function (data) {
      form.userPostcode = data.zonecode
      form.userAddr = data.address
    }
  }).open()
}

onMounted(() => {
  if (!window.daum?.Postcode) {
    const script = document.createElement('script')
    script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
    document.body.appendChild(script)
  }
})

watch(() => form.userId, () => {
  validClicked.value = false
})

watch(() => form.userPhone, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    isCodeSent.value = false
    smsCodeValid.value = false
    smsCode.value = ''
    smsCodeMsg.value = ''
  }
})
</script>
