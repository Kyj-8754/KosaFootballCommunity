<template>
  <div class="container mt-4">
    <h2 class="mb-4 text-center">⚽ 매치 등록</h2>

    <form @submit.prevent="onSubmit">
      <!-- 매치 제목 -->
      <div class="mb-3">
        <label class="form-label">매치 제목</label>
        <input
          type="text"
          v-model="title"
          class="form-control"
          placeholder="예: 7:7 풋살 매치 모집"
          maxlength="30"
        />
      </div>

      <!-- 상세 설명 -->
      <div class="mb-3">
        <label class="form-label">상세 설명</label>
        <textarea
          v-model="description"
          class="form-control"
          rows="5"
          placeholder="매치에 대한 설명을 작성해주세요."
          maxlength="3000"
        ></textarea>
      </div>

      <!-- 성별 제한 -->
      <div class="mb-3">
        <label class="form-label">성별 제한</label>
        <select v-model="gender" class="form-select">
          <option value="all">전체</option>
          <option value="male">남성만</option>
          <option value="female">여성만</option>
        </select>
      </div>

      <!-- (임시) 제출 버튼 -->
      <div class="text-center">
        <button type="submit" class="btn btn-success">등록하기</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const manager_no = inject('userNo')
const authCode = inject('authCode')

// 🛑 필수 데이터 (없으면 이동 차단)
const svcid = history.state?.svcid || null
const userNo = history.state?.userNo || null
const slot_date = history.state?.slot_date || ''
const start_time = history.state?.start_time || ''
const reservation_type = history.state?.reservation_type || ''
const reservation_id = history.state?.reservation_id || ''
const board_id = history.state?.board_id || null

// ✅ 입력 값 유효성 및 권한 검사
onMounted(() => {
  if (!authCode || (authCode.value !== 'ROLE_A1' && authCode.value !== 'ROLE_A2')) {
    alert('접근 권한이 없습니다.')
    router.replace({ name: 'boardList' })
    return
  }

  if (!svcid || !userNo || !slot_date || !start_time || !reservation_type || !reservation_id) {
    alert('잘못된 접근입니다. 예약 정보를 확인할 수 없습니다.')
    router.replace({ name: 'boardList' }) // 또는 다른 적절한 경로
    return
  }
})

const title = ref('')
const description = ref('')
const gender = ref('all')

const onSubmit = async () => {
  try {
    const matchDate = `${slot_date}T${start_time}`

    const payload = {
      match_title: title.value,
      match_description: description.value,
      gender_condition: gender.value,
      match_date: matchDate,
      user_no: userNo,
      manager_no: manager_no?.value ?? null,
      svcid: svcid,
      match_code: reservation_type ? String(reservation_type) : 'social',
      reservation_id: reservation_id
    }

    await axios.post('/board_api/match/register', payload)

    if (board_id) {
      try {
        await axios.delete(`/board_api/board/${board_id}`)
        console.log(`📌 게시글 ${board_id} 삭제 완료`)
      } catch (deleteErr) {
        console.error('❌ 게시글 삭제 실패:', deleteErr)
        alert('매치는 등록되었지만, 기존 게시글 삭제에 실패했습니다.')
      }
    }

    alert('매치가 성공적으로 등록되었습니다.')
    router.push({ name: 'matchList' })

  } catch (err) {
    console.error('매치 등록 실패:', err)
    if (err.response) {
      console.error('🧨 서버 응답 상태:', err.response.status)
      console.error('🧨 서버 응답 데이터:', err.response.data)
    }
    alert('매치 등록 중 오류가 발생했습니다.')
  }
}
</script>

<style scoped>
textarea.form-control {
  resize: none;
}
</style>