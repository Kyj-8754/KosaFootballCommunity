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
                  <label for="userid" class="form-label">이메일</label>
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
                <input type="text" v-model="form.userPhone" id="phone" class="form-control" placeholder="전화번호를 입력해주세요" maxlength="16">
              </div>

              <div class="mb-3 d-flex gap-2">
                <div class="flex-grow-1">
                  <label for="zipcode" class="form-label">우편번호</label>
                  <input type="text" v-model="form.userZipcode" id="zipcode" class="form-control" placeholder="우편번호를 입력해주세요" readonly>
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

// 회원가입 제출
const onSubmit = async () => {
  if (!validClicked.value) {
    alert('이메일 중복 확인을 해주세요')
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
      form.userZipcode = data.zonecode
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
</script>
