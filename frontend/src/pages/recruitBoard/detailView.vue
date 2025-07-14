<template>
  <div class="container my-5" style="max-width: 900px;">
    <h2 class="fw-bold mb-3"></h2>

    <div v-if="recruit">
      <h4 class="mb-3">{{ recruit.title }}</h4>

      <!-- ✅ 모집 상태 표시 -->
      <span class="badge"
            :class="recruit.is_closed === 1 ? 'bg-secondary' : 'bg-success'">
        {{ recruit.is_closed === 1 ? '모집 마감' : '모집 중' }}
      </span>

      <div v-html="recruit.content"></div>

      <ul>
        <li><strong>조회수:</strong> {{ recruit.view_count }}</li>
        <li><strong>등록일:</strong> {{ formatDate(recruit.reg_date) }}</li>
      </ul>

      <div class="mb-3">
        <!-- ✅ 작성자용 버튼: 수정 + 마감 + 삭제 -->
        <template v-if="recruit.user_no === userNo">
          <button class="btn btn-outline-success btn-sm me-2" @click="goEdit">
            수정하기
          </button>

          <button
            v-if="recruit.is_closed === 0"
            class="btn btn-outline-danger btn-sm me-2"
            @click="handleCloseRecruit"
          >
            모집 마감
          </button>

          <!-- ✅ 삭제 버튼 -->
          <button
            class="btn btn-outline-dark btn-sm me-2"
            @click="handleDeleteRecruit"
          >
            삭제하기
          </button>
        </template>

        <!-- ✅ 비작성자 버튼들 (모집 중일 때만 신청 가능) -->
        <template v-else>
          <template v-if="recruit.is_closed === 0">
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
          </template>

          <!-- 상태별 뱃지 -->
          <span v-if="status === 'approved'" class="badge bg-success align-middle">
            이미 클럽 멤버입니다
          </span>

          <span v-else-if="status === 'rejected'" class="badge bg-danger align-middle">
            신청이 거절되었습니다
          </span>

          <span v-else-if="status === 'canceled'" class="badge bg-warning text-dark align-middle">
            신청이 취소되었습니다
          </span>

          <!-- ✅ 모집 마감 후 안내 문구 -->
          <span v-if="recruit.is_closed === 1" class="text-muted small ms-2">
            ※ 모집이 마감되어 신청할 수 없습니다.
          </span>
        </template>
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

const token = inject('token') 
const userNo = inject('userNo')
const router = useRouter()
const route = useRoute()

const recruit = ref(null)
const status = ref('none')
const isLoggedIn = computed(() => !!token?.value)

// ✅ 모집글 조회
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

// ✅ 모집 마감 처리
const handleCloseRecruit = async () => {
  if (!confirm("정말 모집을 마감하시겠습니까?")) return
  try {
    await axios.put(`/recruits_api/${recruit.value.bno}/close`, null, {
      params: { user_no: userNo.value },
      headers: { Authorization: `Bearer ${token.value}` }
    })
    recruit.value.is_closed = 1
    alert("✅ 모집이 마감되었습니다.")
  } catch (e) {
    console.error("모집 마감 실패:", e)
    alert("❌ 모집 마감 처리에 실패했습니다.")
  }
}

// ✅ 모집글 삭제 처리
const handleDeleteRecruit = async () => {
  if (!confirm("정말 삭제하시겠습니까?")) return
  try {
    await axios.delete(`/recruits_api/${recruit.value.bno}`, {
      params: { user_no: userNo.value },
      headers: { Authorization: `Bearer ${token.value}` }
    })
    alert("✅ 모집글이 삭제되었습니다.")
    router.push('/recruitBoard')
  } catch (e) {
    console.error("모집글 삭제 실패:", e)
    alert("❌ 모집글 삭제에 실패했습니다.")
  }
}

// ✅ 가입 상태 조회
const fetchApplyStatus = async () => {
  if (!userNo.value) return
  try {
    const res = await axios.get('/club_api/apply/status', {
      params: {
        bno: route.params.bno,
        user_no: userNo.value
      }
    })
    if (res.data && typeof res.data.status === 'string') {
      status.value = res.data.status
    } else {
      status.value = 'none'
    }
  } catch (e) {
    status.value = 'none'
  }
}

// ✅ 가입 신청
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
      headers: { Authorization: `Bearer ${token?.value}` }
    })

    status.value = 'pending'
    alert('✅ 가입 신청이 완료되었습니다.')
  } catch (e) {
    console.error('가입 신청 실패:', e)
    alert('❌ 가입 신청 실패')
  }
}

// ✅ 신청 취소
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
      headers: { Authorization: `Bearer ${token?.value}` }
    })
    status.value = 'canceled'
    alert('✅ 가입 신청이 취소되었습니다.')
  } catch (e) {
    console.error('가입 취소 실패:', e)
    alert('❌ 가입 취소 실패')
  }
}

// ✅ 날짜 포맷
const formatDate = (dateTime) => {
  if (!dateTime || typeof dateTime !== 'string') return ''
  return dateTime.split(' ')[0].split('T')[0]
}

// ✅ 마운트 시 데이터 로딩
onMounted(async () => {
  await fetchRecruit()
  await fetchApplyStatus()
})
</script>
