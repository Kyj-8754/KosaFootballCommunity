<template>
  <div class="container mt-4">
    <h2 class="mb-4 text-center">⚽ 매치 등록</h2>

    <form @submit.prevent="onSubmit">
      <!-- 매치 제목 -->
      <div class="mb-3">
        <label class="form-label">매치 제목</label>
        <input type="text" v-model="title" class="form-control" placeholder="예: 7:7 풋살 매치 모집" />
      </div>

      <!-- 상세 설명 -->
      <div class="mb-3">
        <label class="form-label">상세 설명</label>
        <textarea v-model="description" class="form-control" rows="5" placeholder="매치에 대한 설명을 작성해주세요."></textarea>
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
import { ref, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router' // ✅ 이 줄 중요!
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const manager_no = inject('userNo')

const svcid = history.state?.svcid || null
const userNo = history.state?.userNo || null
const slot_date = history.state?.slot_date || ''
const start_time = history.state?.start_time || ''
const reservation_type = history.state?.reservation_type || ''
const reservation_id = history.state?.reservation_id || ''

const title = ref('')
const description = ref('')
const gender = ref('all')

const onSubmit = async () => {
  try {
    // datetime 조합
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