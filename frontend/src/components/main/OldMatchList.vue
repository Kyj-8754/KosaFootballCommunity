<template>
  <div class="recent-match-container">
    <!-- 완료된 매치 리스트 -->
    <div class="match-item-list">
      <div
        v-for="match in matches"
        :key="match.match_id"
        class="match-wrapper"
      >
        <div
          class="match-item"
          @click="toggleMatch(match.match_id)"
        >
          <div class="date-col">
            <div>{{ formatDate(match.match_date) }}</div>
          </div>

          <div class="info-col">
            <div class="summary">
              {{ formatTime(match.match_date) }} {{ match.match_title }}
            </div>
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

        <!-- 매치 클릭 시 하단에 결과 표시 -->
        <div v-if="openedMatchId === match.match_id" class="match-result-panel">
          <MatchResult :matchId="match.match_id" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import MatchResult from '@/components/board/match/matchResult.vue'

const matches = ref([])
const openedMatchId = ref(null)

const fetchCompletedMatches = async () => {
  try {
    const res = await axios.get('/board_api/match/recent-completed')
    matches.value = res.data
  } catch (err) {
    console.error('❌ 완료된 매치 목록을 불러오지 못했습니다:', err)
  }
}

const toggleMatch = (id) => {
  openedMatchId.value = (openedMatchId.value === id) ? null : id
}

onMounted(fetchCompletedMatches)

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
  cursor: pointer;
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

.match-result-panel {
  margin-top: 12px;
  padding: 12px;
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 8px;
}
</style>