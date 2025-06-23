<template>
  <div class="bg-white rounded-xl border p-3 shadow-sm overflow-x-auto">
    <table class="table-auto text-center w-full min-w-[480px]">
      <thead>
        <tr class="text-xs text-gray-500 border-b">
          <th class="px-2 py-1">시간</th>
          <th class="px-2 py-1">날씨</th>
          <th class="px-2 py-1">기온 / 강수확률</th>
          <th class="px-2 py-1">강수형태</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="item in forecasts"
          :key="item.fcst_time"
          class="text-sm border-b last:border-none hover:bg-gray-50"
        >
          <td class="px-2 py-1">{{ formatTime(item.fcst_time) }}</td>
          <td class="px-2 py-1 text-lg">{{ getIcon(item) }}</td>
          <td class="px-2 py-1">{{ item.TMP }}℃ / {{ item.POP }}%</td>
          <td class="px-2 py-1">{{ ptyText(item.PTY) }}</td>
        </tr>
      </tbody>
    </table>
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
