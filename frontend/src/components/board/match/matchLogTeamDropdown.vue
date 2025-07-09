<template>
  <div class="dropdown-wrapper">
    <select id="team-select" v-model="selectedTeam" class="dropdown-select">
      <option disabled value="">팀 선택</option>
      <option :value="null">선택 안 함</option>
      <option v-for="team in teams" :key="team.club_id" :value="team.club_id">
        {{ team.club_name }}
      </option>
    </select>
  </div>
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
  } catch (e) {
    console.error('팀 목록 로딩 실패:', e)
  }
}

onMounted(() => {
  fetchTeams()
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