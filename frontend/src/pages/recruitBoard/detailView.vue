<template>
  <div class="container my-5" style="max-width: 900px;">
    <h2 class="fw-bold mb-3"></h2>

    <div v-if="recruit">
      <h4 class="mb-3">{{ recruit.title }}</h4>
      <div v-html="recruit.content"></div>

      <ul>
        <!-- <li><strong>작성자:</strong> {{ recruit.user_no }}</li> -->
        <li><strong>조회수:</strong> {{ recruit.view_count }}</li>
        <li><strong>등록일:</strong> {{ formatDate(recruit.reg_date) }}</li>
      </ul>

      <div class="mb-3">
        <!-- 버튼은 항상 보이지만, 클릭 시 로그인 여부 판단 -->
        <button
          class="btn btn-outline-primary btn-sm me-2"
          :disabled="isApplied"
          @click="handleApply"
        >
          {{ isApplied ? '가입 신청 완료' : '가입 신청' }}
        </button>
      </div>

      <router-link to="/recruitBoard" class="btn btn-secondary btn-sm">목록으로</router-link>
    </div>

    <div v-else class="alert alert-warning">해당 모집글이 존재하지 않습니다.</div>
  </div>
</template>

<script setup>
import { ref, inject, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

// ✅ 전역 주입값
const token = inject('token')
const userId = inject('userId') // ✅ 추가: 로그인된 사용자 ID 주입
const router = useRouter()
const route = useRoute()

// ✅ 반응형 변수들
const recruit = ref(null)
const isApplied = ref(false)

const isLoggedIn = computed(() => !!token?.value)

// ✅ 모집글 가져오기
const fetchRecruit = async () => {
  const bno = route.params.bno
  try {
    const response = await axios.get(`/recruits_api/${bno}`)
    recruit.value = response.data
  } catch (e) {
    alert('❌ 모집글 불러오기 실패')
    console.error('모집글 조회 실패', e)
  }
}

// ✅ 가입 신청 처리
const handleApply = async () => {
  if (!isLoggedIn.value) {
    alert('가입 신청을 하려면 로그인해야 합니다.')
    router.push('/member/loginForm')
    return
  }

  const bno = recruit.value?.bno
  if (!bno || !userId?.value) {
    alert('모집글 정보 또는 사용자 정보가 없습니다.')
    return
  }

  try {
    // ✅ appli_user_no를 함께 보냄
    await axios.post('/club_api/apply', {
      bno: bno,
      appli_user_no: userId.value
    }, {
      headers: {
        Authorization: `Bearer ${token?.value}`
      }
    })

    isApplied.value = true
    alert('✅ 가입 신청이 완료되었습니다.')
  } catch (e) {
    console.error('가입 신청 실패:', e)
    alert('❌ 가입 신청 중 오류가 발생했습니다.')
  }
}

// ✅ 날짜 포맷 함수
const formatDate = (dateTime) => {
  if (!dateTime || typeof dateTime !== 'string') return ''
  return dateTime.split(' ')[0].split('T')[0]
}

// ✅ 컴포넌트 로드시 모집글 불러오기
onMounted(() => {
  fetchRecruit()
})
</script>

