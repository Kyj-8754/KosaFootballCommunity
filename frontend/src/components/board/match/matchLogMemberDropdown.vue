<template>
  <select v-model="selectedMember">
    <option disabled value="">회원 선택</option>
    <option :value="null">선택 안 함</option>
    <option v-for="member in members" :key="member.user_no" :value="member.user_no">
      {{ member.user_name }}
    </option>
  </select>
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
    console.log('회원 목록 로딩중')
    const res = await axios.get(`/board_api/match-log/approved-users/${matchId}`)
    members.value = res.data
    console.log('받은 회원 목록:', res.data)
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