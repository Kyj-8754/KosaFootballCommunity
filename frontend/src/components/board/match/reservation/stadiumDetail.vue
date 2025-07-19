<template>
  <div class="recruit-stadium-detail container mt-4">
    <div class="row g-4">
      <!-- 좌: 이미지 -->
      <div class="col-md-5">
        <img :src="stadium.img_PATH" class="img-fluid rounded shadow" alt="stadium image" />
      </div>

      <!-- 우: 요금 + 달력 -->
      <div class="col-md-7">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h5 class="fw-bold mb-0">이용 요금</h5>
          <span class="fs-4 fw-bold text-primary">{{ stadium.price }}원</span>
        </div>
        <v-calendar
          is-expanded
          :attributes="calendarAttributes"
          @dayclick="onDayClick"
        />
      </div>
    </div>

    <!-- 버튼 -->
    <div class="d-flex justify-content-between mt-3">
      <button class="btn btn-secondary" @click="$emit('back')">목록</button>
      <button class="btn btn-primary" :disabled="!selectedDate" @click="reserve">예약</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  svcid: { type: String, required: true }
})
const emit = defineEmits(['back', 'reserve'])

const stadium = ref({})
const selectedDate = ref(null)
const availableDates = ref([])

// 달력 표시 속성
const calendarAttributes = computed(() => [
  {
    key: 'available',
    dates: availableDates.value,
    highlight: true,
    contentClass: 'available-date'
  },
  selectedDate.value
    ? {
        key: 'selected',
        dates: selectedDate.value,
        highlight: true,
        contentClass: 'selected-date'
      }
    : null
])

const onDayClick = (day) => {
  // 임시 수정
  const dateStr = new Date(day.date).toLocaleDateString('ko-KR', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit'
}).replace(/\./g, '').replace(/ /g, '-').replace(/-$/, '');
  // const dateStr = day.date.toISOString().slice(0, 10)
  if (!availableDates.value.includes(dateStr)) return
  selectedDate.value = dateStr
}

const reserve = () => {
  emit('reserve', selectedDate.value)
}

onMounted(async () => {
  const res = await axios.get('/stadium_api/stadium/detailView', {
    params: { SVCID: props.svcid }
  })
  stadium.value = res.data.stadiumDB.stadium
  availableDates.value = res.data.stadiumDB.slot.map(s => s.slot_DATE)
})
</script>

<style scoped>
.available-date {
  background-color: #e5f1ff !important;
  color: #0d6efd;
  border-radius: 50%;
}
.selected-date {
  background-color: black !important;
  color: white !important;
  border-radius: 50%;
  font-weight: bold;
  box-shadow: 0 0 0 2px white, 0 0 0 4px #0d6efd;
}
</style>
