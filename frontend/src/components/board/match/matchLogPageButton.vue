<!-- components/board/match/MatchLogButton.vue -->
<template>
  <button
    v-if="userNo === managerNo"
    class="log-button"
    @click="goToLogPage"
  >
    매치 로그 입력
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { defineProps, inject } from 'vue'

const props = defineProps({
  matchId: {
    type: [String, Number],
    required: true
  },
  managerNo: {
    type: [String, Number],
    required: true
  }
})

const router = useRouter()
const userNo = inject('userNo')

const goToLogPage = () => {
  router.push({ name: 'matchLog', params: { id: props.matchId } })
}
</script>

<style scoped>
.log-button {
  margin-top: 12px;
  padding: 8px 16px;
  background-color: #28a745;
  color: white;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.log-button:hover {
  background-color: #218838;
}
</style>
