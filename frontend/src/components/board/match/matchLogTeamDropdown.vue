<template>
  <select v-model="selectedTeam">
    <option disabled value="">팀 선택</option>
    <option v-for="team in teams" :key="team.club_id" :value="team.club_id">
      {{ team.club_name }}
    </option>
  </select>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({ modelValue: [String, Number] })
const emit = defineEmits(['update:modelValue'])

const selectedTeam = ref(props.modelValue || '')

// 실제로는 이렇게 서버에서 받은 데이터라고 가정
const teams = [
  { club_id: 1, club_name: '팀1' },
  { club_id: 2, club_name: '팀2' },
  { club_id: 3, club_name: '팀3' }
]

watch(selectedTeam, (val) => {
  emit('update:modelValue', val)
})

watch(() => props.modelValue, (val) => {
  selectedTeam.value = val
})
</script>
