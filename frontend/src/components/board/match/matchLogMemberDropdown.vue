<template>
  <div class="dropdown-wrapper">
    <select id="member-select" v-model="selectedMember" class="dropdown-select">
      <option disabled value="">회원 선택</option>
      <option :value="null">선택 안 함</option>
      <option v-for="member in members" :key="member.user_no" :value="member.user_no">
        {{ member.user_name }}
      </option>
    </select>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const props = defineProps({
  modelValue: [String, Number],
  team: [String, Number]
})
const emit = defineEmits(['update:modelValue'])

const route = useRoute()
const matchId = route.params.id

const selectedMember = ref(props.modelValue || '')
const members = ref([])

// 초기 로딩 시 승인된 유저 목록 불러오기
const fetchMembers = async () => {
  try {
    const res = await axios.get(`/board_api/match-log/approved-users/${matchId}`)
    members.value = res.data
  } catch (e) {
    console.error('회원 목록 로딩 실패:', e)
  }
}

watch(selectedMember, (val) => {
  emit('update:modelValue', val)
})

watch(() => props.modelValue, (val) => {
  selectedMember.value = val
})

onMounted(() => {
  fetchMembers()
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