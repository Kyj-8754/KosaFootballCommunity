<template>
  <div class="recent-match-container">
    <!-- 클럽 매치 리스트 -->
    <div class="match-item-list">
      <div
        v-for="match in matches"
        :key="match.match_id"
        class="match-wrapper"
      >
        <div class="match-item">
          <div class="date-col">
            <div>{{ formatDate(match.match_date) }}</div>
          </div>

          <div class="info-col">
            <div class="summary">
              {{ formatTime(match.match_date) }} {{ match.match_title }}
            </div>
          </div>

          <div class="button-col">
            <button
              v-if="!match.applied"
              @click="applyToMatch(match.match_id)"
              class="btn btn-apply"
            >
              참가
            </button>
            <button
              v-else
              @click="cancelMatch(match.match_id)"
              class="btn btn-cancel"
            >
              참가 취소
            </button>
          </div>

          <div class="type-col">
            <div class="badge" :class="'status-' + match.match_status">
              {{ getStatusLabel(match.match_status) }}
            </div>
            <div class="badge gender" v-if="match.gender_condition !== 'all'">
              {{ match.gender_condition === 'male' ? '남성전용' : '여성전용' }}
            </div>
            <div class="badge gender" v-else>성별무관</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import axios from 'axios'

const props = defineProps({
  clubId: {
    type: Number,
    required: true,
  },
})

const matches = ref([])
const userNo = inject('userNo')

const applyToMatch = async (matchId) => {
  try {
    await axios.post('/board_api/match/apply/approve', {
      match_id: matchId,
      club_id: props.clubId,
      user_no: userNo.value,
    })
    fetchClubMatches()
  } catch (err) {
    console.error('❌ 참가 실패:', err)
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
    fetchClubMatches()
  } catch (err) {
    console.error('❌ 취소 실패:', err)
  }
}

const checkUserApplied = async (matchId) => {
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

const fetchClubMatches = async () => {
  try {
    const res = await axios.get('/board_api/match/club/matches', {
      params: { clubId: props.clubId },
    })

    const rawMatches = res.data

    const withApplied = await Promise.all(
      rawMatches.map(async (match) => {
        const applied = await checkUserApplied(match.match_id)
        return { ...match, applied }
      })
    )

    matches.value = withApplied
  } catch (err) {
    console.error('❌ 클럽 매치 목록을 불러오지 못했습니다:', err)
  }
}

onMounted(fetchClubMatches)

const formatDate = (str) => {
  const date = new Date(str)
  return `${date.getMonth() + 1}월 ${date.getDate()}일`
}

const formatTime = (str) => {
  const date = new Date(str)
  return `${date.getHours().toString().padStart(2, '0')}:00시`
}

const getStatusLabel = (code) => {
  switch (code) {
    case 'waiting': return '대기중'
    case 'active': return '진행중'
    case 'completed': return '진행 완료'
    case 'cancelled': return '취소됨'
    default: return code
  }
}
</script>

<style scoped>
.recent-match-container {
  padding: 1rem;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 10px;
}

.match-item-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.match-wrapper {
  display: flex;
  flex-direction: column;
}

.match-item {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 12px;
  background-color: #fff;
}

.date-col {
  width: 80px;
  text-align: center;
  font-weight: bold;
  color: #333;
}

.info-col {
  flex: 1;
  padding: 0 12px;
}

.summary {
  font-weight: 600;
  color: #111;
}

.type-col {
  white-space: nowrap;
  font-size: 14px;
}

.badge {
  margin-right: 6px;
  margin-top: 4px;
  padding: 2px 6px;
  font-size: 12px;
  border-radius: 4px;
  display: inline-block;
  color: white;
}

.status-waiting { background-color: #ffc107; }
.status-active { background-color: #007bff; }
.status-completed { background-color: #28a745; }
.status-cancelled { background-color: #dc3545; }

.gender {
  background-color: #6c757d;
}

.btn {
  padding: 6px 12px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease;
}

.button-col {
  margin-right: 12px; /* ✅ 오른쪽 여백 추가 */
}

.btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.btn:active {
  transform: scale(0.98);
}

.btn-apply {
  background-color: #28a745;
  color: white;
}

.btn-apply:hover {
  background-color: #218838;
}

.btn-cancel {
  background-color: #dc3545;
  color: white;
}

.btn-cancel:hover {
  background-color: #c82333;
}
</style>