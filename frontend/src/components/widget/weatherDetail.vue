<!-- WeatherDetail.vue -->
<template>
  <div class="text-sm text-gray-800 space-y-1">
    <div>기온: {{ data.TMP }}℃</div>
    <div>습도: {{ data.REH }}%</div>
    <div>풍속: {{ data.WSD }} m/s</div>
    <div>하늘상태: {{ skyLabel }}</div>
    <div>강수형태: {{ ptyLabel }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: Object
})

const skyLabel = computed(() => {
  const map = { '1': '맑음', '3': '구름많음', '4': '흐림' }
  return map[props.data?.SKY] || '-'
})

const ptyLabel = computed(() => {
  const map = { '0': '없음', '1': '비', '2': '비/눈', '3': '눈', '4': '소나기' }
  return map[props.data?.PTY] || '-'
})
</script>
