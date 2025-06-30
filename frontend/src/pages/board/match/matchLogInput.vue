<template>
  <div class="log-input-form">
    <!-- 팀 선택 드롭다운 -->
    <LogTeamDropdown v-model="selectedTeam" />

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
import { ref } from 'vue'
import LogTeamDropdown from './dropdowns/LogTeamDropdown.vue'
import LogMemberDropdown from './dropdowns/LogMemberDropdown.vue'
import LogCodeDropdown from './dropdowns/LogCodeDropdown.vue'
import LogList from '@/components/board/match/matchLogList.vue'

const selectedTeam = ref('')
const selectedMember = ref('')
const selectedLogCode = ref('')
const memo = ref('')
const logs = ref([])

const deleteLog = (index) => {
  logs.value.splice(index, 1)
}

const updateLog = ({ index, team, member, logCode, memo }) => {
  logs.value[index] = {
    ...logs.value[index],
    team,
    member,
    logCode,
    memo
  }
}

const submitLog = () => {
  if (!selectedTeam.value || !selectedMember.value || !selectedLogCode.value) {
    alert('모든 항목을 선택해주세요.')
    return
  }

  const payload = {
    team: selectedTeam.value,
    member: selectedMember.value,
    logCode: selectedLogCode.value,
    memo: memo.value
  }

  console.log('로그 입력:', payload)
  // TODO: API 연동
  alert('로그가 등록되었습니다.')
  
  // 초기화
  selectedTeam.value = ''
  selectedMember.value = ''
  selectedLogCode.value = ''
  memo.value = ''
}
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
