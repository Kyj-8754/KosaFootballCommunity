<template>
  <div>
    <!-- 버튼을 오른쪽에 강제 정렬하고 사이즈 축소 -->
    <div class="toggle-wrapper">
      <div
        @click="isExpanded = !isExpanded"
        class="toggle-button"
      >
        <span>단기 예보 보기</span>
        <span>{{ isExpanded ? '▲' : '▼' }}</span>
      </div>
    </div>

    <!-- 펼쳐지는 예보 영역 -->
    <div
      v-if="isExpanded"
      class="bg-white rounded-xl border p-3 shadow-sm mt-2"
    >
      <table class="table-auto text-center w-full min-w-[480px]">
        <thead>
          <tr class="text-xs text-gray-500 border-b">
            <th class="px-2 py-1">시간</th>
            <th class="px-2 py-1">날씨</th>
            <th class="px-2 py-1">기온 / 강수확률</th>
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
          </tr>
        </tbody>
      </table>
          <!-- 안내 문구 -->
    <div class="update-info">※ 단기예보는 매일 0시/12시 갱신됩니다.</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  forecasts: Array
})

const isExpanded = ref(false)

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

<style scoped>
.toggle-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 4px;
}

.toggle-button {
  font-size: 12px;
  color: #666;
  padding: 2px 6px;
  border-radius: 4px;
  background-color: #f9f9f9;
  cursor: pointer;
  user-select: none;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: background-color 0.2s ease-in-out;
}

.toggle-button:hover {
  background-color: #e5e7eb; /* hover:bg-gray-100 */
}

.update-info {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
  text-align: left;
}
</style>