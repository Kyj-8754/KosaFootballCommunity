<template>
  <div class="match-result-tab">
    <div v-if="sets.length === 0">결과 데이터가 없습니다.</div>

    <div v-else>
      <!-- 탭 버튼 -->
      <div class="tab-buttons">
        <button
          v-for="(_, index) in sets"
          :key="index"
          @click="activeSetIndex = index"
          :class="{ active: activeSetIndex === index }"
        >
          세트 {{ index + 1 }}
        </button>
      </div>

      <!-- 세트별 결과 카드 -->
      <div class="result-set-card" v-if="currentSet">
        <div class="score-row">
          <!-- 왼쪽 팀 -->
          <div class="team">
            <div class="club-name">{{ teamA.name }}</div>
            <div class="score">{{ teamA.score }}</div>
          </div>

          <!-- 중앙 -->
          <div class="middle-info">
            <div class="status">경기 종료</div>
            <div class="date-place">세트 {{ activeSetIndex + 1 }}</div>
          </div>

          <!-- 오른쪽 팀 -->
          <div class="team right">
            <div class="club-name">{{ teamB.name }}</div>
            <div class="score">{{ teamB.score }}</div>
          </div>
        </div>

        <!-- 양쪽으로 나눈 하이라이트 로그 -->
        <div class="highlight-split">
            <div class="highlight-side left">
                <p
                v-for="log in getTeamHighlights(currentSet, teamA.club_id)"
                :key="log.log_id"
                >
                {{ formatHighlight(log, 'left') }}
                </p>
            </div>
            <div class="highlight-side right">
                <p
                v-for="log in getTeamHighlights(currentSet, teamB.club_id)"
                :key="log.log_id"
                >
                {{ formatHighlight(log, 'right') }}
                </p>
            </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import axios from 'axios'

// props
const props = defineProps({
  matchId: { type: Number, required: true }
})

const sets = ref([])
const activeSetIndex = ref(0)

const currentSet = computed(() => sets.value[activeSetIndex.value])

const teamA = computed(() => getTeams(currentSet.value)[0] || { name: '팀 A', score: 0 })
const teamB = computed(() => getTeams(currentSet.value)[1] || { name: '팀 B', score: 0 })

onMounted(async () => {
  try {
    const res = await axios.get(`/board_api/match-log/sets/${props.matchId}`)
    console.log('✅ API 응답:', res.data)
    sets.value = res.data
  } catch (err) {
    console.error('경기 결과 조회 실패:', err)
  }
})


// 팀 점수 및 승패 계산
function getTeams(setLogs) {
  const teams = {}
  for (const log of setLogs) {
    if (log.log_type === '경기 참가') {
      teams[log.club_id] = {
        club_id: log.club_id,
        name: `클럽 ${log.club_id}`,
        score: 0,
        result: '대기 중'
      }
    }
  }

  for (const log of setLogs) {
    if (log.log_type === '골') {
      teams[log.club_id].score += 1
    } else if (log.log_type === '자책골') {
      const opp = Object.keys(teams).find(id => Number(id) !== log.club_id)
      if (opp) teams[opp].score += 1
    } else if (['승리', '패배', '무승부'].includes(log.log_type)) {
      teams[log.club_id].result = log.log_type
    }
  }

  // ✅ club_id 기준 정렬
  return Object.values(teams).sort((a, b) => a.club_id - b.club_id)
}


function getTeamHighlights(setLogs, clubId) {
  return setLogs.filter(log =>
    ['골', '자책골', '옐로 카드', '레드 카드'].includes(log.log_type) &&
    log.club_id === clubId
  )
}

function formatHighlight(log, side = 'left') {
  const user = log.user_no ? `선수 ${log.user_no}` : ''
  const type = log.log_type
  const time = log.log_memo || '' // ex: 54' 같은 분 정보가 log_memo에 들어온다고 가정
  if (side === 'left') {
    return `${user} ${time} ${type}`
  } else {
    return `${type} ${time} ${user}`
  }
}
</script>

<style scoped>
.match-result-tab {
  padding: 12px;
}

.tab-buttons {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tab-buttons button {
  padding: 6px 12px;
  background-color: #eee;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

.tab-buttons button.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}

.result-set-card {
  background-color: #003366;
  color: white;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  text-align: center;
}

.score-row {
  display: flex;
  justify-content: space-between;
  align-items: center; /* ✅ 세로 정렬 정렬 */
  margin-bottom: 12px;
}

.team {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center; /* ✅ 중앙 정렬 */
  justify-content: center;
  gap: 4px;
}

.middle-info {
  flex: 1;
  font-size: 14px;
  text-align: center;
}

.score {
  font-size: 36px;
  font-weight: bold;
  line-height: 1;
}

.club-name {
  font-size: 16px;
}

.highlight-split {
  display: flex;
  justify-content: center;       /* ✅ 전체를 가운데 정렬 */
  margin: 0 auto;                /* ✅ 중앙 정렬 */
  background-color: #002855;
  padding: 10px;
  border-radius: 6px;
  font-size: 14px;
  gap: 40px;                     /* ✅ 좌우 간격 조절 */
  position: relative;
}

.highlight-side {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.highlight-side.left {
  align-items: flex-end; /* ✅ 오른쪽 정렬 */
}

.highlight-side.right {
  align-items: flex-start; /* ✅ 왼쪽 정렬 */
}

/* ✅ 중간 경계선 (선택사항) */
.highlight-split::before {
  content: '';
  width: 1px;
  background-color: rgba(255, 255, 255, 0.3);
  position: absolute;
  top: 10px;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
}
</style>
