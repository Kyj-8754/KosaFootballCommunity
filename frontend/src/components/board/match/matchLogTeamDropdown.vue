<template>
  <select v-model="selectedTeam">
    <option disabled value="">팀 선택</option>
    <option :value="null">선택 안 함</option>
    <option v-for="team in teams" :key="team.club_id" :value="team.club_id">
      {{ team.club_name }}
    </option>
  </select>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({
  modelValue: [String, Number],
  matchId: Number // match_id 전달받기
})
const emit = defineEmits(['update:modelValue'])

const selectedTeam = ref(props.modelValue || '')
const teams = ref([])

watch(selectedTeam, (val) => {
  emit('update:modelValue', val)
})

watch(() => props.modelValue, (val) => {
  selectedTeam.value = val
})

const fetchTeams = async () => {
  try {
    const res = await axios.get(`/board_api/match-log/approved-teams/${props.matchId}`)
    teams.value = res.data
    console.log('✅ 받은 팀 목록:', teams.value)
  } catch (e) {
    console.error('팀 목록 로딩 실패:', e)
  }
}

onMounted(() => {
  fetchTeams()
})
</script>
