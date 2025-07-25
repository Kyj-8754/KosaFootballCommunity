<template>
  <div class="log-input-form">
    <!-- 팀 선택 드롭다운 -->
    <LogTeamDropdown v-model="selectedTeam" :match-id="matchId" />

    <!-- 회원 선택 드롭다운 -->
    <LogMemberDropdown v-model="selectedMember" :team="selectedTeam" />

    <!-- 로그 코드 드롭다운 -->
    <LogCodeDropdown v-model="selectedLogCode" />

    <!-- 메모 입력 -->
    <input
      :type="isScoreLog ? 'number' : 'text'"
      v-model="memo"
      class="memo-input"
      :placeholder="isScoreLog ? '0~10 사이 숫자 입력' : '내용을 입력해주세요'"
      :min="isScoreLog ? 0 : null"
      :max="isScoreLog ? 10 : null"
    />

    <!-- 등록 버튼 -->
    <button @click="submitLog" class="submit-button">등록</button>

    <!-- 로그 리스트 컴포넌트 -->
    <LogList
      :logs="pagedLogs"
      :match-id="matchId"
      @delete="deleteLog"
      @update="updateLog"
    />

    <!-- 페이지네이션 (여기서 감싸기) -->
    <div class="pagination-wrapper">
      <Pagination
        :currentPage="currentPage"
        :totalPages="totalPages"
        @changePage="changePage"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router' // 추가
import axios from 'axios'
import LogTeamDropdown from '@/components/board/match/matchLogTeamDropdown.vue'
import LogMemberDropdown from '@/components/board/match/matchLogMemberDropdown.vue'
import LogCodeDropdown from '@/components/board/match/matchLogActionDropdown.vue'
import LogList from '@/components/board/match/matchLogList.vue'
import Pagination from '@/components/pagination.vue'

const route = useRoute() // 추가

const selectedTeam = ref('')
const selectedMember = ref('')
const selectedLogCode = ref('')
const memo = ref('')
const logs = ref([])
const matchId = Number(route.params.id)

const currentPage = ref(1)
const itemsPerPage = 10

const pagedLogs = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return logs.value.slice(start, start + itemsPerPage)
})

const totalPages = computed(() =>
  Math.ceil(logs.value.length / itemsPerPage)
)

const changePage = (page) => {
  currentPage.value = page
}


const fetchLogs = async () => {
  try {
    const res = await axios.get(`/board_api/match-log/${matchId}`)
    logs.value = res.data
  } catch (e) {
    console.error('로그 조회 실패:', e)
  }
}

const deleteLog = async (index) => {
  const confirmed = window.confirm('정말로 이 로그를 삭제하시겠습니까?')
  if (!confirmed) return

  try {
    const logId = logs.value[index].log_id
    await axios.delete(`/board_api/match-log/delete/${logId}`)
    await fetchLogs()
  } catch (e) {
    console.error('삭제 실패:', e)
    alert('삭제에 실패했습니다.')
  }
}

const updateLog = async ({ index, team, member, logCode, memo }) => {
  try {
    // 점수 로그일 경우 숫자 및 범위 체크
    if (logCode === '실력 점수' || logCode === '매너 점수') {
      const score = Number(memo)
      if (isNaN(score) || score < 0 || score > 10) {
        alert('실력 점수 또는 매너 점수는 0~10 사이의 숫자여야 합니다.')
        return
      }
    }

    const log = logs.value[index]
    const payload = {
      log_id: log.log_id,
      match_id: matchId,
      club_id: team,
      user_no: member,
      log_type: logCode,
      log_memo: memo
    }

    await axios.put('/board_api/match-log/update', payload)
    await fetchLogs()
  } catch (e) {
    console.error('수정 실패:', e)
    alert('수정에 실패했습니다.')
  }
}

const validateLogInput = () => {
  const logType = selectedLogCode.value
  const requiresMember = [
    '출석', '결석', '지각', '조퇴', '골', '어시스트', '자책골', '파울',
    '옐로 카드', '레드 카드', '스마일 카드', '선방', '코너 킥', '패널티 킥',
    '실력 점수', '매너 점수', 'POM'
  ]
  const requiresTeam = ['경기 참가', '승리', '패배', '무승부']
  const forbidsBoth = ['경기 시작', '경기 종료', '경기 중단', '경기 재개']

  if (requiresMember.includes(logType) && !selectedMember.value) {
    alert(`${logType} 항목은 반드시 멤버를 선택해야 합니다.`)
    return false
  }

  if (requiresTeam.includes(logType) && !selectedTeam.value) {
    alert(`${logType} 항목은 반드시 팀을 선택해야 합니다.`)
    return false
  }

  if (forbidsBoth.includes(logType)) {
    if (selectedTeam.value || selectedMember.value) {
      alert(`${logType} 항목은 팀/멤버를 선택하면 안 됩니다.`)
      return false
    }
  }

  return true
}

const submitLog = async () => {
  // 숫자 제한 검사

  if (!validateLogInput()) return

  if (isScoreLog.value) {
    const score = Number(memo.value)
    if (isNaN(score) || score < 0 || score > 10) {
      alert('실력 점수 또는 매너 점수는 0~10 사이의 숫자여야 합니다.')
      return
    }
  }

  const payload = {
    match_id: matchId,
    club_id: selectedTeam.value,
    user_no: selectedMember.value,
    log_type: selectedLogCode.value,
    log_memo: memo.value
  }

  try {
    await axios.post('/board_api/match-log/add', payload)
    alert('로그가 등록되었습니다.')
    await fetchLogs()
  } catch (e) {
    console.error('등록 실패:', e)
    alert('등록에 실패했습니다.')
  }

  selectedTeam.value = ''
  selectedMember.value = ''
  selectedLogCode.value = ''
  memo.value = ''
}

const validateMatch = async () => {
  try {
    const res = await axios.get(`/board_api/match/${matchId}`)
    if (!res.data || Object.keys(res.data).length === 0) {
      throw new Error('해당 매치 없음')
    }
  } catch (e) {
    console.error('유효하지 않은 matchId:', e)
    alert('존재하지 않는 매치입니다.')
    window.location.href = '/match/matchlist' // 또는 router.replace(...)
  }
}

const isScoreLog = computed(() =>
  selectedLogCode.value === '실력 점수' || selectedLogCode.value === '매너 점수'
)

onMounted(async () => {
  await validateMatch()     // ← 먼저 유효성 체크
  await fetchLogs()         // ← 그 후 로그 조회
})
</script>

<style scoped>
.log-input-form {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}
.memo-input {
  flex: 1;
  padding: 6px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.submit-button {
  padding: 6px 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.submit-button:hover {
  background-color: #0056b3;
}
.pagination-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 12px;
}
</style>