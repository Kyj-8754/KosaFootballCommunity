<template>
  <table class="log-table">
    <thead>
      <tr>
        <th>시간</th>
        <th>팀</th>
        <th>회원</th>
        <th>행동</th>
        <th>메모/점수</th>
        <th>관리</th>
      </tr>
    </thead>
    <tbody>
      <tr v-if="logs.length === 0">
        <td colspan="6" class="empty-row">등록된 로그가 없습니다.</td>
      </tr>

      <tr v-else v-for="(log, index) in logs" :key="log.log_id">
        <template v-if="editingIndex === index">
          <td>{{ formatDateTime(log.log_created_at) }}</td>
          <td>
            <LogTeamDropdown v-model="editForm.team" :match-id="matchId" />
          </td>
          <td>
            <LogMemberDropdown v-model="editForm.member" :team="editForm.team" />
          </td>
          <td>
            <LogCodeDropdown v-model="editForm.logCode" />
          </td>
          <td>
            <input
              v-model="editForm.memo"
              :type="isScoreLog ? 'number' : 'text'"
              class="memo-input"
              :placeholder="isScoreLog ? '0~10 숫자 입력' : '내용 입력'"
              :min="isScoreLog ? 0 : null"
              :max="isScoreLog ? 10 : null"
            />
          </td>
          <td class="action-buttons">
            <button class="btn btn-save" @click="saveEdit(index)">저장</button>
            <button class="btn btn-cancel" @click="cancelEdit">취소</button>
          </td>
        </template>

        <template v-else>
          <td>{{ formatDateTime(log.log_created_at) }}</td>
          <td>{{ log.club_name }}</td>
          <td>{{ log.user_name }}</td>
          <td>{{ log.log_type }}</td>
          <td>{{ log.log_memo }}</td>
          <td class="action-buttons">
            <button class="btn btn-edit" @click="startEdit(index)">수정</button>
            <button class="btn btn-delete" @click="$emit('delete', index)">삭제</button>
          </td>
        </template>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { ref, computed } from 'vue'
import LogTeamDropdown from '@/components/board/match/matchLogTeamDropdown.vue'
import LogMemberDropdown from '@/components/board/match/matchLogMemberDropdown.vue'
import LogCodeDropdown from '@/components/board/match/matchLogActionDropdown.vue'

const props = defineProps({
  logs: Array,
  matchId: Number
})

const emit = defineEmits(['update', 'delete'])

const editingIndex = ref(null)
const editForm = ref({
  team: '',
  member: '',
  logCode: '',
  memo: ''
})

const startEdit = (index) => {
  editingIndex.value = index
  const log = props.logs[index]
  editForm.value = {
    team: log.club_id,
    member: log.user_no,
    logCode: log.log_type,
    memo: log.log_memo
  }
}

const cancelEdit = () => {
  editingIndex.value = null
}

const saveEdit = (index) => {
  if (isScoreLog.value) {
    const score = Number(editForm.value.memo)
    if (isNaN(score) || score < 0 || score > 10) {
      alert('점수는 0~10 사이 숫자여야 합니다.')
      return
    }
  }

  emit('update', {
    index,
    team: editForm.value.team,
    member: editForm.value.member,
    logCode: editForm.value.logCode,
    memo: editForm.value.memo
  })
  editingIndex.value = null
}

const formatDateTime = (isoString) => {
  if (!isoString) return ''
  return isoString.replace('T', ' ')
}

const isScoreLog = computed(() =>
  editForm.value.logCode === '실력 점수' || editForm.value.logCode === '매너 점수'
)
</script>

<style scoped>
.log-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 16px;
  font-size: 14px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
}
.log-table th,
.log-table td {
  border: 1px solid #e0e0e0;
  padding: 10px;
  text-align: center;
}
.log-table th {
  background-color: #f5f5f5;
  font-weight: 600;
}
.empty-row {
  color: #888;
  padding: 20px 0;
  text-align: center;
}
.memo-input {
  width: 100%;
  padding: 6px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 13px;
}
.action-buttons {
  display: flex;
  gap: 6px;
  justify-content: center;
}
.btn {
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.btn-edit {
  background-color: #1976d2;
  color: #fff;
}
.btn-edit:hover {
  background-color: #1565c0;
}
.btn-delete {
  background-color: #e53935;
  color: #fff;
}
.btn-delete:hover {
  background-color: #c62828;
}
.btn-save {
  background-color: #388e3c;
  color: #fff;
}
.btn-save:hover {
  background-color: #2e7d32;
}
.btn-cancel {
  background-color: #9e9e9e;
  color: #fff;
}
.btn-cancel:hover {
  background-color: #757575;
}
</style>