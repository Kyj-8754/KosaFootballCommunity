<template>
  <table class="log-table">
    <thead>
      <tr>
        <th>시간</th>
        <th>팀</th>
        <th>회원</th>
        <th>행동</th>
        <th>메모</th>
        <th>관리</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(log, index) in logs" :key="log.log_id">
        <!-- 수정 모드일 경우 -->
        <template v-if="editingIndex === index">
          <td>{{ log.log_created_at }}</td>
          <td>
            <LogTeamDropdown v-model="editForm.team" />
          </td>
          <td>
            <LogMemberDropdown v-model="editForm.member" :team="editForm.team" />
          </td>
          <td>
            <LogCodeDropdown v-model="editForm.logCode" />
          </td>
          <td>
            <input v-model="editForm.memo" type="text" class="memo-input" />
          </td>
          <td>
            <button @click="saveEdit(index)">저장</button>
            <button @click="cancelEdit">취소</button>
          </td>
        </template>

        <!-- 일반 보기 모드 -->
        <template v-else>
          <td>{{ log.log_created_at }}</td>
          <td>{{ log.club_id }}</td>
          <td>{{ log.user_no }}</td>
          <td>{{ log.log_type }}</td>
          <td>{{ log.log_memo }}</td>
          <td>
            <button @click="startEdit(index)">수정</button>
            <button @click="$emit('delete', index)">삭제</button>
          </td>
        </template>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { ref } from 'vue'
import LogTeamDropdown from '@/components/board/match/matchLogTeamDropdown.vue'
import LogMemberDropdown from '@/components/board/match/matchLogMemberDropdown.vue'
import LogCodeDropdown from '@/components/board/match/matchLogActionDropdown.vue'

const props = defineProps({
  logs: {
    type: Array,
    required: true
  }
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
  emit('update', {
    index,
    team: editForm.value.team,
    member: editForm.value.member,
    logCode: editForm.value.logCode,
    memo: editForm.value.memo
  })
  editingIndex.value = null
}
</script>

<style scoped>
.log-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 16px;
  font-size: 14px;
}
.log-table th,
.log-table td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: center;
}
.log-table th {
  background-color: #f2f2f2;
}
.memo-input {
  width: 100%;
  padding: 4px;
  font-size: 13px;
}
button {
  margin: 0 2px;
  padding: 4px 8px;
  font-size: 12px;
  cursor: pointer;
}
</style>
