<!-- components/MatchFilterBar.vue -->
<template>
  <div class="filter-bar">
    <input type="date" v-model="filters.afterDate" />

    <select v-model="filters.stadiumType">
      <option value="">구장타입 선택</option>
      <option value="실내">실내</option>
      <option value="실외">실외</option>
    </select>

    <select v-model="filters.gender_condition">
      <option value="">성별 제한</option>
      <option value="all">무관</option>
      <option value="male">남성</option>
      <option value="female">여성</option>
    </select>

    <input v-model="filters.nVsN" placeholder="N vs N" />

    <select v-model="filters.match_closed">
      <option value="">마감여부</option>
      <option value="active">모집중</option>
      <option value="closed">마감</option>
    </select>

    <button @click="onFilter">적용</button>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const emit = defineEmits(['filter'])

const filters = reactive({
  afterDate: '',
  stadiumType: '',       // 실제 사용 시 stadium에서 필터링해야 할 수도 있음
  gender_condition: '',
  nVsN: '',
  match_closed: ''
})

const onFilter = () => {
  const payload = {
    afterDate: filters.afterDate,
    gender_condition: filters.gender_condition,
    match_closed: filters.match_closed,
    match_title: filters.nVsN,
    // stadiumType은 실제 필드와 매핑될 경우만 포함
  }
  emit('filter', payload)
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}
.filter-bar select,
.filter-bar input {
  padding: 6px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>
