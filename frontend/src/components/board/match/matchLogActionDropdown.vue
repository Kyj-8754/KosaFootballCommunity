<template>
  <div class="dropdown-wrapper">
    <select id="action-select" v-model="selectedAction" class="dropdown-select">
      <option disabled value="">행동을 선택하세요</option>
      <option v-for="action in actions" :key="action" :value="action">
        {{ action }}
      </option>
    </select>
  </div>
</template>

<script setup>
import { ref, watch, defineEmits, defineProps } from 'vue'

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: String
})

const selectedAction = ref(props.modelValue || '')

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
  '스마일 카드',
  '선방',
  '코너 킥',
  '패널티 킥',
  '승리',
  '패배',
  '무승부',
  '실력 점수',
  '매너 점수',
  'POM'
]

watch(selectedAction, (newVal) => {
  emits('update:modelValue', newVal)
})

watch(() => props.modelValue, (val) => {
  selectedAction.value = val
})
</script>

<style scoped>
/* 공통 드롭다운 스타일 */
.dropdown-wrapper {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin: 12px 0; /* ✅ 바깥 여백: 위아래 */
}

.dropdown-select {
  padding: 6px; /* ✅ 내부 여백 */
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: #fff;
  transition: border-color 0.2s ease;
}

.dropdown-select:focus {
  border-color: #007bff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.2);
}
</style>