<!-- components/board/match/detail/MatchTitleBar.vue -->
<template>
  <div class="title-bar">
    <h2>{{ formatTime(match.match_date) }} {{ match.match_title }}</h2>
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
</script>

<style scoped>
.title-bar {
  padding: 12px;
  border-bottom: 2px solid #ddd;
  font-size: 20px;
  font-weight: bold;
}
</style>
