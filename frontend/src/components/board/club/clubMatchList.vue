<template>
  <div class="p-4">
    <h2 class="text-xl font-semibold mb-4">클럽 매치 리스트</h2>
    <div v-if="loading">불러오는 중...</div>
    <div v-else-if="matches.length === 0">매치가 없습니다.</div>
    <ul v-else class="space-y-2">
      <li
        v-for="match in matches"
        :key="match.match_id"
        class="p-4 border rounded shadow flex justify-between items-center"
      >
        <p class="font-medium">{{ match.match_title }}</p>
        <p class="text-sm text-gray-600">
          {{ formatDate(match.match_date) }}
        </p>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  clubId: {
    type: Number,
    required: true,
  },
})

const matches = ref([])
const loading = ref(false)

const fetchMatches = async () => {
  loading.value = true
  try {
    const response = await axios.get('/board_api/match/club/matches', {
      params: { clubId: props.clubId },
    })
    matches.value = response.data
  } catch (err) {
    console.error('❌ 매치 로딩 실패:', err)
  } finally {
    loading.value = false
  }
}

// 날짜 포맷터 함수 (YYYY-MM-DD HH:mm 형식)
const formatDate = (raw) => {
  const d = new Date(raw)
  return `${d.getFullYear()}-${(d.getMonth() + 1)
    .toString()
    .padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d
    .getHours()
    .toString()
    .padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

// clubId가 변경될 때마다 재요청
watch(() => props.clubId, fetchMatches, { immediate: true })
</script>

<style scoped>
/* 필요 시 스타일 추가 */
</style>