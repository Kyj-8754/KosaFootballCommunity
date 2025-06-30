<template>
  <div>
    <label for="member-select">회원 선택</label>
    <select id="member-select" v-model="selectedMember">
      <option disabled value="">회원 선택</option>
      <option v-for="member in members" :key="member" :value="member">
        {{ member }}
      </option>
    </select>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const selectedMember = ref('')

// 추후 API 또는 props로 대체 가능
const members = ['회원1', '회원2', '회원3', '회원4', '회원5']
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
