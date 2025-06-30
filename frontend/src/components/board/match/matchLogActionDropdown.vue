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
  '슛',
  '패스',
  '드리블',
  '파울',
  '세이브',
  '태클',
  '실수'
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