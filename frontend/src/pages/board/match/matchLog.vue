<template>
  <div class="log-input-form">
    <!-- 팀 선택 드롭다운 -->
    <LogTeamDropdown v-model="selectedTeam" :match-id="matchId" />

    <!-- 회원 선택 드롭다운 -->
    <LogMemberDropdown v-model="selectedMember" :team="selectedTeam" />

    <!-- 로그 코드 드롭다운 -->
    <LogCodeDropdown v-model="selectedLogCode" />

    <!-- 메모 입력 -->
    <input v-model="memo" type="text" class="memo-input" placeholder="내용을 입력해주세요" />

    <!-- 등록 버튼 -->
    <button @click="submitLog" class="submit-button">등록</button>

    <!-- 로그 리스트 -->
    <LogList :logs="logs" @delete="deleteLog" @update="updateLog" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router' // 추가
import axios from 'axios'
import LogTeamDropdown from '@/components/board/match/matchLogTeamDropdown.vue'
import LogMemberDropdown from '@/components/board/match/matchLogMemberDropdown.vue'
import LogCodeDropdown from '@/components/board/match/matchLogActionDropdown.vue'
import LogList from '@/components/board/match/matchLogList.vue'

const route = useRoute() // 추가

const selectedTeam = ref('')
const selectedMember = ref('')
const selectedLogCode = ref('')
const memo = ref('')
const logs = ref([])
const matchId = Number(route.params.id)

const fetchLogs = async () => {
  try {
    const res = await axios.get(`/board_api/match-log/${matchId}`)
    logs.value = res.data
    console.log('logs:', logs.value)
  } catch (e) {
    console.error('로그 조회 실패:', e)
  }
}

const deleteLog = async (index) => {
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

const submitLog = async () => {
  if (!selectedMember.value || !selectedLogCode.value) {
    alert('모든 항목을 선택해주세요.')
    return
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

onMounted(() => {
  fetchLogs()
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
</style>