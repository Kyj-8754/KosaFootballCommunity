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
        <!-- 왼쪽: 제목 + 날짜 -->
        <div class="flex items-center space-x-4">
          <RouterLink
            :to="{ name: 'matchDetail', params: { id: match.match_id } }"
            class="text-blue-600 hover:underline font-medium"
          >
            {{ match.match_title }}
          </RouterLink>
          <span class="text-sm text-gray-500">
            {{ formatDate(match.match_date) }}
          </span>
        </div>

        <!-- 오른쪽: 버튼 -->
        <div>
          <button
            v-if="!match.applied"
            class="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded text-sm"
            @click="applyToMatch(match.match_id)"
          >
            참가
          </button>

          <button
            v-else
            class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-sm"
            @click="cancelMatch(match.match_id)"
          >
            참가 취소
          </button>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, watch, inject } from 'vue'
import axios from 'axios'
import { RouterLink } from 'vue-router'

const props = defineProps({
  clubId: {
    type: Number,
    required: true,
  },
})

const matches = ref([])
const loading = ref(false)

// 주입된 사용자 번호
const userNo = inject('userNo')

const formatDate = (raw) => {
  const d = new Date(raw)
  return `${d.getFullYear()}-${(d.getMonth() + 1)
    .toString()
    .padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d
    .getHours()
    .toString()
    .padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const checkAppliedStatus = async (matchId) => {
  try {
    const res = await axios.get('/board_api/match/applied', {
      params: {
        matchId,
        userNo: userNo.value,
      },
    })
    return res.data === true
  } catch (err) {
    console.error('❌ 신청 여부 확인 실패:', err)
    return false
  }
}

const fetchMatches = async () => {
  loading.value = true
  try {
    const response = await axios.get('/board_api/match/club/matches', {
      params: { clubId: props.clubId },
    })

    const rawMatches = response.data

    // 신청 여부 확인 후 상태 포함시킴
    const withStatus = await Promise.all(
      rawMatches.map(async (match) => {
        const applied = await checkAppliedStatus(match.match_id)
        return { ...match, applied }
      })
    )

    matches.value = withStatus
  } catch (err) {
    console.error('❌ 매치 로딩 실패:', err)
  } finally {
    loading.value = false
  }
}

const applyToMatch = async (matchId) => {
  try {
    await axios.post('/board_api/match/apply/approve', {
      match_id: matchId,
      club_id: props.clubId,
      user_no: userNo.value,
    })
    alert('✅ 참가 완료!')
    fetchMatches()
  } catch (err) {
    console.error('❌ 참가 실패:', err)
    alert('참가 실패: ' + (err.response?.data?.res_msg || err.message))
  }
}

const cancelMatch = async (matchId) => {
  try {
    await axios.delete('/board_api/match/cancel', {
      params: {
        matchId,
        userNo: userNo.value,
      },
    })
    alert('🚫 참가 취소 완료')
    fetchMatches()
  } catch (err) {
    console.error('❌ 취소 실패:', err)
    alert('취소 실패: ' + (err.response?.data?.res_msg || err.message))
  }
}

// 클럽 ID 변경 시 데이터 다시 가져옴
watch(() => props.clubId, fetchMatches, { immediate: true })
</script>

<style scoped>
/* 필요 시 스타일 추가 */
</style>