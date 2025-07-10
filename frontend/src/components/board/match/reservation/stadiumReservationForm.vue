<template>
  <div class="container mt-4">
    <h3>구장 예약</h3>

    <!-- 🏟️ 구장 정보 -->
    <div class="card mb-3">
      <div class="card-header">구장 정보</div>
      <div class="card-body">
        <h5 class="card-title">{{ stadium.svcnm }}</h5>
        <p class="card-text">주소: {{ stadium.adres }}</p>
        <p class="card-text">운영시간: {{ stadium.v_MIN }} ~ {{ stadium.v_MAX }}</p>
        <p class="card-text">가격: {{ stadium.price }}</p>
      </div>
    </div>

    <!-- 👤 유저 정보 -->
    <div class="card mb-3">
      <div class="card-header">예약자 정보</div>
      <div class="card-body">
        <p><strong>이름:</strong> {{ user.userName }}</p>
        <p><strong>전화 번호:</strong> {{ user.userPhone }}</p>
      </div>
    </div>

    <!-- 📅 예약 정보 -->
    <div class="card mb-3">
      <div class="card-header">예약 정보</div>
      <div class="card-body">
        <div class="mb-3">
          <label class="form-label">예약 날짜</label>
          <div class="form-control-plaintext">{{ date }}</div>
        </div>
        <div class="mb-3">
          <label class="form-label">예약 시간</label>
          <select v-model="reservation.slot_id" class="form-select">
            <option disabled value="">시간을 선택하세요</option>
            <option
              v-for="slot in timeSlots"
              :key="slot.value"
              :value="slot.value"
              :disabled="slot.disabled"
            >
              {{ slot.label }}
            </option>
          </select>
        </div>
        <div class="mb-3">
          <label class="form-label">예약 유형</label>
          <select v-model="reservation.reservation_type" class="form-select">
            <option disabled value="">선택하세요</option>
            <option value="social">소셜</option>
            <option value="match">매치</option>
          </select>
        </div>
        <div class="d-flex justify-content-between">
          <button class="btn btn-secondary" @click="$emit('back')">이전</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import axios from 'axios'

const props = defineProps({
  svcid: { type: String, required: true },
  date: { type: String, required: true }
})
const emit = defineEmits(['back', 'complete'])

const token = inject('token')
const userNo = inject('userNo')
const user = ref({})
const stadium = ref({})
const slotList = ref([])

const reservation = ref({
  slot_id: '',
  reservation_type: '',
  user_no: null,
  price: 0
})

// ⏱️ 시간 슬롯 처리
const timeSlots = computed(() => {
  return (slotList.value || []).map(slot => {
    const label = `${slot.startTime} ~ ${slot.endTime}`
    return {
      label: slot.reservationStatus === 'reserved' ? `${label} (예약됨)` : label,
      value: slot.slotid,
      disabled: slot.reservationStatus === 'reserved'
    }
  })
})

const getReservationData = () => {
  if (!reservation.value.slot_id || !reservation.value.reservation_type) {
    return null
  }
  return { ...reservation.value }
}

defineExpose({
  getReservationData
})


onMounted(async () => {
  // 유저 정보
  const userRes = await axios.get('/login_api/mypage/detailView', {
    params: { userNo: userNo.value },
    headers: {
      Authorization: `Bearer ${token.value}`
      }
  })
  user.value = userRes.data.member
  reservation.value.user_no = userNo.value

  // 구장 정보
  const stadiumRes = await axios.get('/stadium_api/stadium/detailView', {
    params: { SVCID: props.svcid }
  })
  stadium.value = stadiumRes.data.stadiumDB.stadium
  reservation.value.price = stadium.value.price

  // 예약 가능한 슬롯
  const slotRes = await axios.post('/reservation_api/reservation/reservationForm', {
    SVCID: props.svcid,
    date: props.date
  })
  slotList.value = slotRes.data.slots || []
})
</script>
