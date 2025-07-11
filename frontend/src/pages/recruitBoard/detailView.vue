<template>
  <div class="container my-5" style="max-width: 900px;">
    <h2 class="fw-bold mb-3"></h2>

    <div v-if="recruit">
      <h4 class="mb-3">{{ recruit.title }}</h4>
      <div v-html="recruit.content"></div>

      <ul>
        <li><strong>조회수:</strong> {{ recruit.view_count }}</li>
        <li><strong>등록일:</strong> {{ formatDate(recruit.reg_date) }}</li>
      </ul>

      <div class="mb-3">

        <!-- [기존] 팀장이면 "수정하기" 버튼 -->
        <button
          v-if="recruit.user_no === userNo"
          class="btn btn-outline-success btn-sm me-2"
          @click="goEdit"
        >
          수정하기
        </button>

        <!-- [수정/추가] 상태 값(status)에 따라 버튼/문구를 세분화해서 노출 -->
        <template v-if="recruit.user_no !== userNo">
          <button
            v-if="status === 'none' && isLoggedIn"
            class="btn btn-outline-primary btn-sm me-2"
            @click="handleApply"
          >
            가입 신청
          </button>

          <button
            v-else-if="status === 'pending'"
            class="btn btn-outline-secondary btn-sm me-2"
            @click="handleCancel"
          >
            가입 신청 취소
          </button>

          <span v-else-if="status === 'approved'" class="badge bg-success align-middle">
            이미 클럽 멤버입니다
          </span>

          <span v-else-if="status === 'rejected'" class="badge bg-danger align-middle">
            신청이 거절되었습니다
          </span>

          <span v-else-if="status === 'canceled'" class="badge bg-warning text-dark align-middle">
            신청이 취소되었습니다
          </span>
        </template>
        <!-- [설명] status 값에 따라 신청/취소/완료/거절 상태를 명확히 보여줌 -->

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
const userNo = inject('userNo')
const router = useRouter()
const route = useRoute()

// ✅ 반응형 변수들
const recruit = ref(null)
// const isApplied = ref(false) // [삭제] 상태 값은 아래 status로 일원화
const status = ref('none') // [추가] pending/approved/rejected/canceled/none

// ✅ 로그인 체크(토큰 존재 여부)
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
const goEdit = () => {
  if (!recruit.value || !recruit.value.bno) return
  router.push(`/recruitBoard/${recruit.value.bno}/updateForm`)
}

// ✅ 내 가입 신청 상태를 불러오는 함수
const fetchApplyStatus = async () => {
  if (!userNo.value) return
  try {
    // [수정] isApplied가 아니라 status 값을 받아옴
    const res = await axios.get('/club_api/apply/status', {
      params: {
        bno: route.params.bno,
        user_no: userNo.value
      }
    })
    // [수정/추가] status 값 반환을 기대함(백엔드 수정 필요)
    if (res.data && typeof res.data.status === 'string') {
      status.value = res.data.status
    } else {
      status.value = 'none'
    }
  } catch (e) {
    status.value = 'none'
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
  if (!bno || !userNo?.value) {
    alert('모집글 정보 또는 사용자 정보가 없습니다.')
    return
  }

  try {
    await axios.post('/club_api/apply', {
      bno: bno,
      appli_user_no: Number(userNo.value)
    }, {
      headers: {
        Authorization: `Bearer ${token?.value}`
      }
    })

    // [수정] isApplied.value = true → status.value = 'pending'
    status.value = 'pending'
    alert('✅ 가입 신청이 완료되었습니다.')
  } catch (e) {
    // [기존] 오류 핸들링
    console.error('가입 신청 실패:', e)
    if (e.response && e.response.data) {
      alert(`❌ 가입 신청 실패: ${e.response.data}`)
    } else {
      alert('❌ 가입 신청 중 오류가 발생했습니다.')
    }
  }
}

// ✅ 가입 취소 처리 함수
const handleCancel = async () => {
  const bno = recruit.value?.bno
  if (!bno || !userNo?.value) {
    alert('모집글 정보 또는 사용자 정보가 없습니다.')
    return
  }
  try {
    await axios.delete('/club_api/apply', {
      data: {
        bno: bno,
        appli_user_no: Number(userNo.value)
      },
      headers: {
        Authorization: `Bearer ${token?.value}`
      }
    })
    // [수정] isApplied.value = false → status.value = 'canceled'
    status.value = 'canceled'
    alert('✅ 가입 신청이 취소되었습니다.')
  } catch (e) {
    console.error('가입 취소 실패:', e)
    if (e.response && e.response.data) {
      alert(`❌ 가입 취소 실패: ${e.response.data}`)
    } else {
      alert('❌ 가입 취소 중 오류가 발생했습니다.')
    }
  }
}

// ✅ 날짜 포맷 함수
const formatDate = (dateTime) => {
  if (!dateTime || typeof dateTime !== 'string') return ''
  return dateTime.split(' ')[0].split('T')[0]
}

// ✅ 컴포넌트 마운트시 모집글/가입 상태를 모두 불러옴
onMounted(async () => {
  await fetchRecruit()
  console.log('recruit.value:', recruit.value)
  console.log('작성자 user_no:', recruit.value?.user_no, typeof recruit.value?.user_no)
  console.log('내 userNo:', userNo.value, typeof userNo.value)
  await fetchApplyStatus()
})
</script>
