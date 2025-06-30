<template>
  <select v-model="selectedTeam" @change="$emit('update:team', selectedTeam)">
    <option disabled value="">팀 선택</option>
    <option v-for="team in teams" :key="team" :value="team">{{ team }}</option>
  </select>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: String
})

const emit = defineEmits(['update:team'])

const teams = ['팀1', '팀2', '팀3']
const selectedTeam = ref(props.modelValue || '')

watch(() => props.modelValue, (newVal) => {
  selectedTeam.value = newVal
})
</script>

<style scoped>
select {
  padding: 6px 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
}
</style>