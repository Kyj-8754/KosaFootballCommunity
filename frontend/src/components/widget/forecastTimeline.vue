<!-- ForecastTimeline.vue -->
<template>
  <div class="bg-white rounded border p-2 space-y-2">
    <div
      v-for="item in forecasts"
      :key="item.fcst_time"
      class="border-b pb-1 last:border-none"
    >
      <div class="text-xs text-gray-500">
        {{ formatTime(item.fcst_time) }}
      </div>
      <div class="text-sm">
        {{ getIcon(item) }} {{ item.TMP }}℃ / {{ item.POP }}% / {{ ptyText(item.PTY) }}
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  forecasts: Array
})

function formatTime(t) {
  return `${t?.slice(0, 2)}:${t?.slice(2)}`
}

function getIcon(item) {
  if (item.PTY === '1' || item.PTY === '2') return '🌧'
  if (item.PTY === '3') return '❄️'
  if (item.SKY === '1') return '☀️'
  if (item.SKY === '3') return '⛅'
  if (item.SKY === '4') return '☁️'
  return '❓'
}

function ptyText(code) {
  const map = {
    '0': '없음',
    '1': '비',
    '2': '비/눈',
    '3': '눈',
    '4': '소나기'
  }
  return map[code] || '-'
}
</script>
