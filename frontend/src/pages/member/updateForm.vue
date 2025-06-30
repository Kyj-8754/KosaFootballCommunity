<template>
  <div class="update-container">
    <form @submit.prevent="onSubmit" class="update-form">
      <h3>회원 정보 수정</h3>

      <!-- userNo 숨김 처리 -->
      <input type="hidden" v-model="form.userNo" />

      <div class="mb-3">
        <label for="name" class="form-label">이름</label>
        <input type="text" id="name" v-model="form.userName" required class="form-control" />
      </div>

      <div class="mb-3">
        <label for="birth" class="form-label">생년월일</label>
        <input type="date" id="birth" v-model="form.userBirth" required class="form-control" />
      </div>

      <div class="mb-3">
        <label for="phone" class="form-label">전화번호</label>
        <input type="text" id="phone" v-model="form.userPhone" maxlength="16" class="form-control" />
      </div>

      <div class="mb-3 d-flex gap-2">
        <div class="flex-grow-1">
          <label for="zipcode" class="form-label">우편번호</label>
          <input type="text" id="zipcode" v-model="form.userPostcode" class="form-control" readonly />
        </div>
        <div class="mt-4">
          <input type="button" @click="findZipcode" value="우편찾기" class="btn btn-outline-secondary" />
        </div>
      </div>

      <div class="mb-3">
        <label for="address" class="form-label">주소</label>
        <input type="text" id="address" v-model="form.userAddr" class="form-control" readonly />
      </div>

      <div class="mb-3">
        <label for="detail_address" class="form-label">상세주소</label>
        <input type="text" id="detail_address" v-model="form.userDetailAddr" class="form-control" />
      </div>

      <div class="link-area">
        <input type="submit" value="변경" class="btn btn-primary" />
        <router-link :to="`/detailView/${form.userNo}`" class="btn btn-outline-secondary">취소</router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const token = inject('token')

// ✅ userNo 포함
const form = ref({
  userNo: '',
  userId: '',
  userName: '',
  userBirth: '',
  userPhone: '',
  userPostcode: '',
  userAddr: '',
  userDetailAddr: ''
})

const fetchMemberDetail = async () => {
  const userNo = route.query.userNo
  if (!userNo) {
    alert('회원 번호가 전달되지 않았습니다.')
    router.push('/')
    return
  }

  try {
    const res = await axios.get(`/login_api/mypage/detailView?userNo=${userNo}`, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    })

    const data = res.data.member

    // ✅ 속성별 대입 + userNo 저장
    form.value.userNo = userNo
    form.value.userId = data.userId
    form.value.userName = data.userName
    form.value.userBirth = data.userBirth?.substring(0, 10) || ''
    form.value.userPhone = data.userPhone
    form.value.userPostcode = data.userPostcode
    form.value.userAddr = data.userAddr
    form.value.userDetailAddr = data.userDetailAddr
  } catch (err) {
    console.error('회원 정보 조회 실패:', err)
    alert('회원 정보를 불러오지 못했습니다.')
    router.push('/')
  }
}

const onSubmit = async () => {
  try {
    const res = await fetch('/api/update', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json;charset=utf-8'
      },
      body: JSON.stringify(form.value)
    })
    const result = await res.json()
    alert(result.res_msg)
    if (result.res_code !== '400') {
      router.push(`/detailView/${form.value.userId}`)
    }
  } catch (err) {
    console.error('회원 정보 수정 중 오류 발생:', err)
    alert('회원 정보 수정 중 오류 발생')
  }
}

const findZipcode = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      form.value.userPostcode = data.zonecode
      form.value.userAddr = data.address
    }
  }).open()
}

onMounted(() => {
  fetchMemberDetail()

  if (!window.daum?.Postcode) {
    const script = document.createElement('script')
    script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
    document.body.appendChild(script)
  }
})
</script>

<style scoped>
.update-container {
  display: flex;
  justify-content: center;
  margin-top: 60px;
}
.update-form {
  background-color: white;
  padding: 40px;
  width: 600px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.update-form h3 {
  text-align: center;
  font-weight: bold;
  margin-bottom: 30px;
}
.update-form label {
  font-weight: bold;
}
.update-form .form-control {
  margin-bottom: 15px;
}
.link-area {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
