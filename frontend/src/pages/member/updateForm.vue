<template>
  <div class="update-container">
    <form @submit.prevent="onSubmit" class="update-form">
      <h3>회원 정보 수정</h3>

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
				<label class="form-label">성별</label>
				<select v-model="form.userGender" class="form-select" required>
					<option value="" disabled>성별을 선택해주세요</option>
					<option value="M">남성</option>
					<option value="F">여성</option>
				</select>
			</div>
      <div class="mb-3">
        <label for="phone" class="form-label">전화번호</label>
        <input type="text" id="phone" v-model="form.userPhone" maxlength="11" class="form-control" />
      </div>

      <div class="mb-3 d-flex gap-2">
        <div class="flex-grow-1">
          <label for="zipcode" class="form-label">우편번호</label>
          <input type="text" id="zipcode" v-model="form.userPostCode" class="form-control" readonly />
        </div>
        <div class="mt-4">
          <input type="button" @click="handleFindZipcode" value="우편찾기" class="btn btn-outline-secondary" />
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
        <router-link :to="{ name: 'Member_MyPage', query: { userNo: userNo } }" class="btn btn-outline-secondary">취소</router-link>
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

const form = ref({
  userNo: '',
  userId: '',
  userName: '',
  userBirth: '',
  userPhone: '',
  userPostCode: '',
  userAddr: '',
  userDetailAddr: '',
  userGender: ''
})

let originalData = ref({})
let isPostcodeLoaded = false

const fetchMemberDetail = async () => {
  const userNo = route.query.userNo
  console.log('[fetchMemberDetail] userNo:', userNo)

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
    originalData.value = data // 전체 원본 저장

    form.value = {
      userNo: data.userNo,
      userId: data.userId,
      userName: data.userName,
      userBirth: formatDate(data.userBirth),
      userPhone: data.userPhone,
      userPostCode: data.userPostCode,
      userAddr: data.userAddr,
      userDetailAddr: data.userDetailAddr,
      userGender: data.userGender
    }
  } catch (err) {
    console.error('[회원 정보 조회 실패]', err)
    alert('회원 정보를 불러오지 못했습니다.')
    router.push('/')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return date.toISOString().slice(0, 10)
  } catch {
    console.warn('[날짜 파싱 실패]', dateStr)
    return ''
  }
}

const handleFindZipcode = () => {
  if (!window.daum || !window.daum.Postcode) {
    alert('우편번호 API가 아직 로딩되지 않았습니다.')
    return
  }

  new window.daum.Postcode({
    oncomplete: (data) => {
      form.value.userPostCode = data.zonecode
      form.value.userAddr = data.address
    }
  }).open()
}

onMounted(() => {
  fetchMemberDetail()

  if (!window.daum?.Postcode && !isPostcodeLoaded) {
    const script = document.createElement('script')
    script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
    script.onload = () => {
      isPostcodeLoaded = true
      console.log('✅ 우편번호 스크립트 로드 완료')
    }
    script.onerror = () => {
      console.error('❌ 우편번호 스크립트 로드 실패')
    }
    document.body.appendChild(script)
  }
})

const onSubmit = async () => {

  try {
    const res = await fetch(`/login_api/mypage/update?userNo=${form.value.userNo}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json;charset=utf-8',
				Authorization: `Bearer ${token.value}`
      },
      body: JSON.stringify(form.value)
    })

    const result = await res.json()
    alert(result.res_msg)
    if (result.res_code !== '400') {
      router.push({
				name: 'Member_MyPage',
				query: { userNo: form.value.userNo }
			})
    }
  } catch (err) {
    console.error('[회원 정보 수정 중 오류]', err)
    alert('회원 정보 수정 중 오류 발생')
  }
}
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
