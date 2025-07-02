<template>
  <div class="log-action-dropdown">
    <label for="action-select"></label>
    <select id="action-select" v-model="selectedAction">
      <option disabled value="">행동을 선택하세요</option>
      <option v-for="action in actions" :key="action" :value="action">
        {{ action }}
      </option>
    </select>
  </div>
</template>

<script setup>
import { ref, watch, defineEmits } from 'vue'

const emits = defineEmits(['update:modelValue'])
const selectedAction = ref('')

const actions = [
  '출석',
  '결석',
  '지각',
  '조퇴',
  '경기 참가',
  '경기 시작',
  '경기 종료',
  '경기 중단',
  '경기 재개',
  '골',
  '어시스트',
  '자책골',
  '파울',
  '옐로 카드',
  '레드 카드',
  '선방',
  '코너 킥',
  '패널티 킥',
  '승리',
  '패배',
  '무승부',
  '점수 종류 1',
  '점수 종류 2',
  '점수 종류 3'
]

watch(selectedAction, (newVal) => {
  emits('update:modelValue', newVal)
})
</script>

<style scoped>
.log-action-dropdown {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

select {
  padding: 6px;
  font-size: 14px;
  border-radius: 4px;
  border: 1px solid #ccc;
}
</style>