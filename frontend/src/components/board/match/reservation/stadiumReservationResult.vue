<template>
  <div class="container mt-4" v-if="reservation && Object.keys(reservation).length">
    <h3 class="text-center mb-4">예약 확인</h3>

    <!-- 🏟 구장 정보 -->
    <div class="card mb-3">
      <div class="card-header">구장 정보</div>
      <div class="card-body">
        <p><strong>구장명:</strong> {{ stadium.svcnm }}</p>
        <p><strong>주소:</strong> {{ stadium.adres }}</p>
        <p><strong>연락처:</strong> {{ stadium.telno }}</p>
      </div>
    </div>

    <!-- 👤 사용자 정보 -->
    <div class="card mb-3">
      <div class="card-header">사용자 정보</div>
      <div class="card-body">
        <p><strong>이름:</strong> {{ user.userName }}</p>
        <p><strong>전화번호:</strong> {{ user.userPhone }}</p>
      </div>
    </div>

    <!-- 📅 예약 정보 -->
    <div class="card mb-4">
      <div class="card-header">예약 정보</div>
      <div class="card-body">
        <p><strong>날짜:</strong> {{ reservation.slot_date }}</p>
        <p><strong>시간:</strong> {{ reservation.start_time }} ~ {{ reservation.end_time }}</p>
        <p><strong>유형:</strong> {{ reservation.reservation_type }}</p>
        <p><strong>가격:</strong> {{ reservation.price }} 원</p>
      </div>
    </div>

    <!-- 결제 버튼 -->
    <div class="text-center">
      <button class="btn btn-success me-2" @click="requestPayment">💳 결제하기</button>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { onMounted, ref } from 'vue'

const props = defineProps({
  reservationId: { type: String, required: true }
})

const user = ref({})
const stadium = ref({})
const reservation = ref({})

onMounted(async () => {
  try {
    const res = await axios.post('/reservation_api/reservation/reservation_confirm', {
      reservation_id: props.reservationId
    })

    console.log('예약 응답:', res.data)

    const reservationData = res.data.reservationDB
    if (!reservationData) {
      throw new Error('예약 데이터가 없습니다.')
    }

    reservation.value = reservationData

    const { user_no, svcid } = reservationData

    const userRes = await axios.get('/login_api/mypage/detailView', {
      params: { userNo: user_no }
    })
    user.value = userRes.data.member

    const stadiumRes = await axios.get('/stadium_api/stadium/detailView', {
      params: { SVCID: svcid }
    })
    stadium.value = stadiumRes.data.stadiumDB.stadium

  } catch (err) {
    console.error('예약 확인 실패:', err)
    alert('예약 정보를 불러오는 중 오류가 발생했습니다.')
  }
})

const requestPayment = () => {
  alert('💳 결제 로직은 추후 구현 예정입니다.')
}
</script>
