<template>
  <div>
    <label for="member-select"></label>
    <select id="member-select" v-model="selectedMember">
      <option disabled value="">회원 선택</option>
      <option v-for="member in members" :key="member" :value="member">
        {{ member }}
      </option>
    </select>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: String,
  team: String // 선택된 팀 기준으로 회원 필터링 가능
})
const emit = defineEmits(['update:modelValue'])

const selectedMember = ref(props.modelValue || '')

// 추후 props.team 값으로 API 호출하거나 동적 필터링 가능
const members = ['회원1', '회원2', '회원3', '회원4', '회원5']

// 부모로 선택 값 emit
watch(selectedMember, (val) => {
  emit('update:modelValue', val)
})

// 외부에서 값 변경되었을 때 반영
watch(() => props.modelValue, (val) => {
  selectedMember.value = val
})
</script>

<style scoped>
label {
  margin-right: 8px;
  font-weight: bold;
}
select {
  padding: 4px 8px;
  font-size: 14px;
}
</style>
