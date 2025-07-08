<template>
  <div class="p-4">
    <h2 class="text-2xl font-bold mb-6 text-gray-800 flex items-center">
      📋 클럽 매치 리스트
    </h2>

    <div v-if="loading" class="text-gray-500">불러오는 중...</div>

    <div v-else-if="matches.length === 0" class="text-gray-500">
      매치가 없습니다.
    </div>

    <div v-else class="overflow-x-auto">
      <table class="min-w-full bg-white border border-gray-200 rounded-lg shadow-sm">
        <thead>
          <tr class="bg-gray-50 border-b border-gray-200">
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              매치 정보
            </th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
              참가 여부
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="match in matches"
            :key="match.match_id"
            class="hover:bg-gray-50 transition border-b border-gray-200 last:border-b-0"
          >
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex flex-col">
                <RouterLink
                  :to="{ name: 'matchDetail', params: { id: match.match_id } }"
                  class="text-base font-semibold text-blue-600 hover:underline"
                >
                  {{ match.match_title }}
                </RouterLink>
                <span class="text-sm text-gray-500 mt-1">
                  📅 {{ formatDate(match.match_date) }}
                </span>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right">
              <button
                v-if="!match.applied"
                @click="applyToMatch(match.match_id)"
                class="bg-green-500 hover:bg-green-600 text-black px-4 py-1.5 rounded-md text-sm font-medium shadow transition"
              >
                참가
              </button>

              <button
                v-else
                @click="cancelMatch(match.match_id)"
                class="bg-red-500 hover:bg-red-600 text-black px-4 py-1.5 rounded-md text-sm font-medium shadow transition"
              >
                참가 취소
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
// 기존 스크립트 내용은 동일합니다. 변경할 필요 없습니다.
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
const userNo = inject('userNo')

// 날짜 포맷
const formatDate = (raw) => {
  const d = new Date(raw)
  return `${d.getFullYear()}-${(d.getMonth() + 1)
    .toString()
    .padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d
    .getHours()
    .toString()
    .padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

// 신청 여부 확인
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

// 매치 목록 + 신청 여부 병합
const fetchMatches = async () => {
  loading.value = true
  try {
    const response = await axios.get('/board_api/match/club/matches', {
      params: { clubId: props.clubId },
    })

    const rawMatches = response.data

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

// 참가 신청
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

// 참가 취소
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

// 클럽 ID 변경 시 자동 로딩
watch(() => props.clubId, fetchMatches, { immediate: true })
</script>

<style scoped>
/* Tailwind 기반이라 커스텀 스타일 불필요 */
</style>