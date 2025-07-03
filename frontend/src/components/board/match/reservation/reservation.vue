<!-- BoardRecruitExtra.vue -->
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
      :svcid="selectedSVCID"
      :date="selectedDate"
      @complete="goToResult"
      @back="goToDetail"
    />

    <reservationResult
      v-else-if="currentView === 'result'"
      :reservationId="reservationId"
      @back="goToList"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'

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
</script>

<style scoped>
.board-recruit-extra {
  border-top: 2px dashed #ccc;
  padding-top: 1.5rem;
}
</style>
