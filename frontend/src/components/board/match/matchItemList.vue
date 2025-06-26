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
        {{ match.match_status }} {{ match.gender_condition === 'all' ? '성별무관' : match.gender_condition + '전용' }}
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
</style>
