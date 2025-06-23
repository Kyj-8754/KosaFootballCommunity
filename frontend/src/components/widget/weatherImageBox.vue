<!-- WeatherImageBox.vue -->
<template>
  <div class="text-center text-gray-600 text-sm border rounded h-24 flex items-center justify-center">
    {{ displayText }}
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  sky: String,
  pty: String
})

const displayText = computed(() => {
  if (props.pty === '1' || props.pty === '2') return '🌧 비/비+눈'
  if (props.pty === '3') return '❄️ 눈'
  if (props.sky === '1') return '☀️ 맑음'
  if (props.sky === '3') return '⛅ 구름많음'
  if (props.sky === '4') return '☁️ 흐림'
  return '날씨 정보 없음'
})
</script>
