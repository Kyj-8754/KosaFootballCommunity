<template>
  <div class="title-bar">
    <h2>{{ formatTime(match.match_date) }} {{ match.match_title }}</h2>
    <div class="meta-row">
      <span class="badge" :class="'status-' + match.match_status">{{ getStatusLabel(match.match_status) }}</span>
      <span class="badge" :class="'code-' + match.match_code">{{ getMatchCodeLabel(match.match_code) }}</span>
      <span class="badge" v-if="match.match_closed === 'closed'">마감됨</span>
    </div>
    <div class="date-info">
      등록일: {{ formatDate(match.match_created_at) }} / 수정일: {{ formatDate(match.match_modified_at) }}
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  match: {
    type: Object,
    required: true
  }
})

const formatTime = (datetime) => {
  const date = new Date(datetime)
  const hour = date.getHours().toString().padStart(2, '0')
  return `${hour}:00시`
}

const formatDate = (datetime) => {
  const date = new Date(datetime)
  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const mi = String(date.getMinutes()).padStart(2, '0')
  const ss = String(date.getSeconds()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`
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

const getMatchCodeLabel = (code) => {
  switch (code) {
    case 'social': return '소셜매치'
    case 'league': return '리그매치'
    default: return code
  }
}
</script>

<style scoped>
.title-bar {
  padding: 12px;
  border-bottom: 2px solid #ddd;
  background-color: #f0f0f0;
}

.meta-row {
  display: flex;
  gap: 8px;
  margin-top: 6px;
}

.badge {
  background-color: #999;
  color: white;
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
}

.status-waiting { background-color: #ffc107; }
.status-active { background-color: #007bff; }
.status-completed { background-color: #28a745; }
.code-social { background-color: #6f42c1; }
.code-league { background-color: #17a2b8; }

.date-info {
  margin-top: 4px;
  font-size: 12px;
  color: #555;
}
</style>
