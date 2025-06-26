<!-- components/MatchSearchBar.vue -->
<template>
  <div class="search-bar">
    <input
      v-model="keyword"
      type="text"
      placeholder="검색할 내용을 입력해주세요"
      class="search-input"
    />
    <button @click="onSearch" class="search-button">검색</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['search'])
const keyword = ref('')

const onSearch = () => {
  emit('search', { match_title: keyword.value })
}
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.search-input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.search-button {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
