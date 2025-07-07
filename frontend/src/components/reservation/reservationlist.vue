<template>
  <div class="reservation-list">
    <div v-if="!reservations || reservations.length === 0">예약 정보가 없습니다.</div>
    <ul v-else>
      <li v-for="item in reservations" :key="item.reservation_id">
        <router-link :to="{name: 'reservation_Confirm', params: {reservationId: item.reservation_id}}" >
          {{ item.svcid }} - {{ item.slot_id }} - {{ item.status }}
        </router-link>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 👇 props 받기
const props = defineProps({
  user_no: {
    type: [String, Number],
    required: true
  }
})

const reservations = ref([])

const fetchReservations = async () => {
  const res = await axios.post('/reservation_api/reservation/list', {
      user_no: props.user_no   // 필요 시 props로 받아 처리 가능
  })
  reservations.value = res.data.reservationList
}

onMounted(fetchReservations)
</script>