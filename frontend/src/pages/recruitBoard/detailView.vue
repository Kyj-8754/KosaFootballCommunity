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
     <!-- 팀장이면 "수정하기" 버튼, 팀장이 아니면 가입/취소 버튼 -->
  <button
    v-if="recruit.user_no === userNo"
    class="btn btn-outline-success btn-sm me-2"
    @click="goEdit"
  >
    수정하기
  </button>
  <!--  버튼 조건 분기: 작성자가 아닐 때만 버튼 노출 -->
<button
  v-if="!isApplied && isLoggedIn && userNo !== undefined && recruit.user_no !== userNo"
  class="btn btn-outline-primary btn-sm me-2"
  @click="handleApply"
>
  가입 신청
</button>


  <button
    v-else-if="isApplied && recruit.user_no !== userNo.value"
    class="btn btn-outline-secondary btn-sm me-2"
    @click="handleCancel"
  >
    가입 취소
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
const userId = inject('userId')
const userNo = inject('userNo')
const router = useRouter()
const route = useRoute()

// ✅ 반응형 변수들
const recruit = ref(null)
const isApplied = ref(false) // 가입 상태(백엔드 연동)

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
    const res = await axios.get('/club_api/apply/status', {
      params: {
        bno: route.params.bno,
        user_no: userNo.value
      }
    })
    isApplied.value = res.data.is_applied
  } catch (e) {
    isApplied.value = false
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
      appli_user_no: Number(userNo.value)  // int 타입 변환
    }, {
      headers: {
        Authorization: `Bearer ${token?.value}`
      }
    })

    isApplied.value = true // 상태 즉시 반영
    alert('✅ 가입 신청이 완료되었습니다.')
  } catch (e) {
    // 🔴 [여기부터 수정/추가]
    console.error('가입 신청 실패:', e)
    // ✅ 서버에서 보낸 상세 메시지가 있으면 사용자에게 보여줌
    if (e.response && e.response.data) {
      alert(`❌ 가입 신청 실패: ${e.response.data}`)
    } else {
      alert('❌ 가입 신청 중 오류가 발생했습니다.')
    }
    // 🔴 [여기까지 수정/추가]
  }
}

// ✅ [추가] 가입 취소 처리 함수
const handleCancel = async () => {
  const bno = recruit.value?.bno
  if (!bno || !userNo?.value) {
    alert('모집글 정보 또는 사용자 정보가 없습니다.')
    return
  }
  try {
    await axios.delete('/club_api/apply', {
      data: { // axios delete는 body에 data로 보내야 함
        bno: bno,
        appli_user_no: Number(userNo.value)
      },
      headers: {
        Authorization: `Bearer ${token?.value}`
      }
    })
    isApplied.value = false // 상태 즉시 반영
    alert('✅ 가입 신청이 취소되었습니다.')
  } catch (e) {
    // 🔴 [여기부터 수정/추가]
    console.error('가입 취소 실패:', e)
    if (e.response && e.response.data) {
      alert(`❌ 가입 취소 실패: ${e.response.data}`)
    } else {
      alert('❌ 가입 취소 중 오류가 발생했습니다.')
    }
    // 🔴 [여기까지 수정/추가]
  }
}

// ✅ 날짜 포맷 함수
const formatDate = (dateTime) => {
  if (!dateTime || typeof dateTime !== 'string') return ''
  return dateTime.split(' ')[0].split('T')[0]
}

// // ✅ 컴포넌트 마운트시 모집글/가입 상태를 모두 불러옴
// onMounted(() => {
//   fetchRecruit()
//   fetchApplyStatus()
// })

onMounted(async () => {
  await fetchRecruit()   // 모집글 정보 먼저 가져오기
  console.log('recruit.value:', recruit.value)
  console.log('작성자 user_no:', recruit.value?.user_no, typeof recruit.value?.user_no)
  console.log('내 userNo:', userNo.value, typeof userNo.value)
  await fetchApplyStatus()
})

</script>
