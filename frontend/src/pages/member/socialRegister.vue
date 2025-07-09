<template>
  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <div class="container d-flex justify-content-center align-items-center min-vh-100">
          <div class="card shadow p-4 rounded w-100" style="max-width: 600px;">
            <h1 class="text-center mb-4">회원가입</h1>
            <form @submit.prevent="onSubmit">
              <div class="mb-3">
                <label for="name" class="form-label">이름</label>
                <input type="text" v-model="form.userName" id="name" class="form-control" readonly>
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
                  <label for="postcode" class="form-label">우편번호</label>
                  <input type="text" v-model="form.userPostcode" id="postcode" class="form-control" placeholder="우편번호를 입력해주세요" readonly>
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
import { reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 소셜 회원가입용 폼
const form = reactive({
  provider: '',
  providerId: '',
  userName: '',
  userBirth: '',
  userGender: '',
  userPhone: '',
  userPostcode: '',
  userAddr: '',
  userDetailAddr: ''
})

const onSubmit = async () => {
  // 전화번호 숫자만 필터링
  form.userPhone = form.userPhone.replace(/[^0-9]/g, '')

  // 필수 항목 유효성 검사
  if (!form.userBirth || !form.userGender || !form.userPhone || !form.userPostcode || !form.userAddr) {
    alert('모든 필수 정보를 입력해주세요.')
    return
  }

  try {
    const response = await fetch('/login_api/oauth/na/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=utf-8' },
      body: JSON.stringify(form)
    })

    const json = await response.json()

    if (json.res_code === '200') {
      alert(json.res_msg)
      router.push({ path: '/member/loginForm' }) // query 제거해서 이동
    } else {
      alert(json.res_msg || '회원가입에 실패했습니다.')
    }
  } catch (error) {
    console.error('회원가입 요청 중 오류 발생:', error)
    alert('서버 오류로 회원가입에 실패했습니다.')
  }
}

const onReset = () => {
  form.userBirth = ''
  form.userGender = ''
  form.userPhone = ''
  form.userPostcode = ''
  form.userAddr = ''
  form.userDetailAddr = ''
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
  const query = route.query

  if (!query.provider || !query.providerId || !query.nickname) {
    alert('잘못된 접근입니다.')
    router.replace('/member/loginForm')
    return
  }

  form.provider = query.provider
  form.providerId = query.providerId
  form.userName = query.nickname || query.name || '이름없음'

  // 다음 주소 API 스크립트 동적 로드
  if (!window.daum?.Postcode) {
    const script = document.createElement('script')
    script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
    document.body.appendChild(script)
  }
})
</script>
