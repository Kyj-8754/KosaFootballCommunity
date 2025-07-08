<template>
  <div class="match-result-tab">
    <!-- 탭 전환 버튼 -->
    <div class="tab-buttons">
      <button
        v-for="(_, index) in sets"
        :key="'set-' + index"
        @click="() => { activeTab = 'set'; activeSetIndex = index }"
        :class="{ active: activeTab === 'set' && activeSetIndex === index }"
      >
        세트 {{ index + 1 }}
      </button>

      <button
        @click="activeTab = 'pom'"
        :class="{ active: activeTab === 'pom' }"
      >
        POM
      </button>
    </div>

    <!-- 세트별 결과 카드 -->
    <div class="result-set-card" v-if="activeTab === 'set' && currentSet.length > 0">
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


    <!-- POM 로그 카드 -->
    <div v-if="activeTab === 'pom'" class="result-set-card">
      <div v-if="poms.length === 0">POM 로그가 없습니다.</div>
        <div v-else class="pom-card-list">
          <div 
            class="pom-card" 
            v-for="log in poms" 
            :key="log.log_id"
          >
            <div class="pom-title">🏆 POM</div>
            <div class="pom-name">{{ log.user_name || '이름 없음' }}</div>
            <div class="pom-club">{{ log.club_name || '클럽 없음' }}</div>
            <div class="pom-stats">
              <template v-if="getPomStats(log.user_no)">
                골: {{ getPomStats(log.user_no).goal }} /
                도움: {{ getPomStats(log.user_no).assist }}
              </template>
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
const poms = ref([])
const activeTab = ref('set') // 'set' or 'pom'
const activeSetIndex = ref(0)

const currentSet = computed(() => sets.value[activeSetIndex.value] || [])

const teamA = computed(() => getTeams(currentSet.value)[0] || { name: '팀 A', score: 0 })
const teamB = computed(() => getTeams(currentSet.value)[1] || { name: '팀 B', score: 0 })

onMounted(async () => {
  try {
    const [setsRes, pomsRes] = await Promise.all([
      axios.get(`/board_api/match-log/sets/${props.matchId}`),
      axios.get(`/board_api/match-log/pom/${props.matchId}`)
    ])
    console.log('📦 sets 데이터:', setsRes.data)
    console.log('📦 poms 데이터:', pomsRes.data)
    sets.value = setsRes.data
    poms.value = pomsRes.data
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
        name: log.club_name || `클럽 ${log.club_id}`,  // ✅ 실제 이름 사용
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

  return Object.values(teams).sort((a, b) => a.club_id - b.club_id)
}

function getTeamHighlights(setLogs, clubId) {
  return setLogs.filter(log =>
    ['골', '자책골', '옐로 카드', '레드 카드'].includes(log.log_type) &&
    log.club_id === clubId
  )
}

function formatHighlight(log, side = 'left') {
  const user = log.user_name || `선수 ${log.user_no}` || ''  // ✅ 이름 우선
  const type = log.log_type
  const time = log.log_memo || '' // ex: 54' 같은 정보

  if (side === 'left') {
    return `${user} ${time} ${type}`
  } else {
    return `${type} ${time} ${user}`
  }
}

function getPomStats(userNo) {
  let goal = 0
  let assist = 0

  for (const set of sets.value) {
    for (const log of set) {
      if (log.user_no === userNo) {
        if (log.log_type === '골') goal++
        if (log.log_type === '도움') assist++
      }
    }
  }

  return { goal, assist }
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
/* 단일 컬럼 중앙 정렬용 */
.highlight-split.single-column {
  justify-content: center;
  align-items: center;
  text-align: center;
}

.highlight-split.single-column .highlight-side {
  align-items: center;
}

.pom-card-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
}

.pom-card {
  background-color: #001f3f;
  padding: 16px 24px;
  border-radius: 8px;
  color: white;
  text-align: center;
  min-width: 200px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}

.pom-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #ffcc00;
}

.pom-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
}

.pom-club {
  font-size: 16px;
  margin-bottom: 4px;
  opacity: 0.85;
}

.pom-stats {
  font-size: 14px;
  opacity: 0.9;
}
</style>
