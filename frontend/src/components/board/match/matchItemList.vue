<!-- components/MatchItemList.vue -->
<template>
  <div class="match-item-list">
    <div
      v-for="match in matches"
      :key="match.match_id"
      class="match-item"
    >
      <div class="date-col">
        <div>{{ formatDate(match.match_date) }}</div>
        <div v-if="match.match_closed === 'closed'" class="badge">마감됨</div>
      </div>

      <div class="info-col">
        <div class="summary">
          {{ formatTime(match.match_date) }} {{ match.match_title }}
        </div>
        <div class="location">{{ match.PLACENM }} {{ match.SUBPLACENM }}</div>
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
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  matches: {
    type: Array,
    required: true
  }
})

const formatDate = (datetimeStr) => {
  const date = new Date(datetimeStr)
  return `${date.getMonth() + 1}월 ${date.getDate()}일`
}

const formatTime = (datetimeStr) => {
  const date = new Date(datetimeStr)
  return `${date.getHours().toString().padStart(2, '0')}:00시`
}

const getStatusLabel = (code) => {
  switch (code) {
    case 'waiting':
      return '대기중'
    case 'active':
      return '진행중'
    case 'completed':
      return '진행 완료'
    default:
      return code
  }
}
</script>

<style scoped>
.match-item-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.match-item {
  display: flex;
  border: 1px solid #ddd;
  padding: 12px;
  border-radius: 8px;
  align-items: center;
  background-color: #fff;
}

.date-col {
  width: 80px;
  text-align: center;
  font-weight: bold;
}

.badge {
  margin-top: 4px;
  padding: 2px 6px;
  font-size: 12px;
  background-color: #ccc;
  border-radius: 4px;
}

.info-col {
  flex: 1;
  padding: 0 12px;
}

.summary {
  font-weight: 600;
}

.location {
  color: #555;
  font-size: 14px;
}

.type-col {
  white-space: nowrap;
  font-size: 14px;
}

.badge {
  padding: 2px 6px;
  font-size: 12px;
  border-radius: 4px;
  display: inline-block;
  margin-top: 4px;
  margin-right: 4px;
  color: white;
}

.status-waiting {
  background-color: #ffc107; /* 노랑 */
}

.status-active {
  background-color: #007bff; /* 파랑 */
}

.status-completed {
  background-color: #28a745; /* 초록 */
}

.gender {
  background-color: #6c757d; /* 회색 */
}

</style>
