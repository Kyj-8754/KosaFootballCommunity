<template>
  <select v-model="selectedMember">
    <option disabled value="">회원 선택</option>
    <option v-for="member in members" :key="member.user_no" :value="member.user_no">
      {{ member.user_name }}
    </option>
  </select>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: [String, Number],
  team: [String, Number]
})
const emit = defineEmits(['update:modelValue'])

const selectedMember = ref(props.modelValue || '')

// 실제 데이터 예시
const members = [
  { user_no: 101, user_name: '홍길동' },
  { user_no: 102, user_name: '김철수' },
  { user_no: 103, user_name: '이영희' }
]

watch(selectedMember, (val) => {
  emit('update:modelValue', val)
})

watch(() => props.modelValue, (val) => {
  selectedMember.value = val
})
</script>
