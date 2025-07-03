<template>
  <div class="board-recruit-extra mt-4">
    <stadiumList
      v-if="currentView === 'list'"
      @select="handleSelectStadium"
    />

    <stadiumDetail
      v-else-if="currentView === 'detail'"
      :svcid="selectedSVCID"
      @reserve="goToReservation"
      @back="goToList"
    />

    <reservationForm
      v-else-if="currentView === 'reserve'"
      ref="reservationFormRef"
      :svcid="selectedSVCID"
      :date="selectedDate"
      @complete="goToResult"
      @back="goToDetail"
    />

    <!--
    <reservationResult
      v-else-if="currentView === 'result'"
      :reservationId="reservationId"
      @back="goToList"
    />
    -->
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

import stadiumList from '@/components/board/match/reservation/stadiumList.vue'
import stadiumDetail from '@/components/board/match/reservation/stadiumDetail.vue'
import reservationForm from '@/components/board/match/reservation/stadiumReservationForm.vue'
import reservationResult from '@/components/board/match/reservation/stadiumReservationResult.vue'

const currentView = ref('list') // 'list' | 'detail' | 'reserve' | 'result'
const selectedSVCID = ref(null)
const selectedDate = ref(null)
const reservationId = ref(null)

// 리스트 → 상세
const handleSelectStadium = (svcid) => {
  selectedSVCID.value = svcid
  currentView.value = 'detail'
}

// 상세 → 예약
const goToReservation = (date) => {
  selectedDate.value = date
  currentView.value = 'reserve'
}

// 예약 → 결과
const goToResult = (id) => {
  reservationId.value = id
  currentView.value = 'result'
}

// 뒤로가기 핸들링
const goToList = () => currentView.value = 'list'
const goToDetail = () => currentView.value = 'detail'

const reservationFormRef = ref(null)

const requestReservation = async () => {
  if (currentView.value !== 'reserve') {
    alert('예약 정보를 먼저 입력해주세요.')
    return null
  }

  const data = reservationFormRef.value?.getReservationData?.()
  if (!data) {
    alert('예약 정보를 모두 입력해주세요.')
    return null
  }

  try {
    const res = await axios.post('/reservation_api/reservation/reservation_std', data)
    return res.data
  } catch (err) {
    console.error('예약 실패:', err)
    return null
  }
}

// expose 함수 외부에서 사용 가능하게 하기
defineExpose({
  requestReservation
})
</script>

<style scoped>
.board-recruit-extra {
  border-top: 2px dashed #ccc;
  padding-top: 1.5rem;
}
</style>
