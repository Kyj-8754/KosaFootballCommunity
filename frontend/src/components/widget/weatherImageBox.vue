<!-- WeatherImageBox.vue -->
<template>
  <div class="text-center text-gray-600 text-sm border rounded h-24 flex items-center justify-center">
    <img :src="imageSrc" alt="날씨 이미지" style="width: 48px; height: 48px; object-fit: contain;" />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  sky: String,
  pty: String
})

const imageSrc = computed(() => {
  if (props.pty === '1' || props.pty === '2') return new URL('@/assets/weather/rain.png', import.meta.url).href
  if (props.pty === '3') return new URL('@/assets/weather/snow.png', import.meta.url).href
  if (props.sky === '1') return new URL('@/assets/weather/sunny.png', import.meta.url).href
  if (props.sky === '3') return new URL('@/assets/weather/cloudy.png', import.meta.url).href
  if (props.sky === '4') return new URL('@/assets/weather/overcast.png', import.meta.url).href
  return new URL('@/assets/weather/unknown.png', import.meta.url).href
})
</script>

