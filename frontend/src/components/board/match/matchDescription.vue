<!-- components/MatchDescriptionCard.vue -->
<template>
  <div class="match-description-card">
    <h3>매치 설명</h3>
    <p>{{ description }}</p>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  description: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.match-description-card {
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.match-description-card h3 {
  margin-bottom: 8px;
  font-size: 18px;
}

.match-description-card p {
  white-space: pre-wrap;
  line-height: 1.5;
  font-size: 14px;
  color: #333;
}
</style>
