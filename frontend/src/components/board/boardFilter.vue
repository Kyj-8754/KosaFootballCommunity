<template>
  <div class="board-filter">
    <select v-model="criteria">
      <option value="tc">제목+내용</option>
      <option value="title">제목</option>
      <option value="content">내용</option>
      <option value="writer">작성자</option>
    </select>

    <input v-model="keyword" placeholder="검색할 내용을 입력해주세요" />
    <button @click="search">검색</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const criteria = ref('tc')
const keyword = ref('')

const emit = defineEmits(['search'])

const search = () => {
  emit('search', {
    criteria: criteria.value,
    keyword: keyword.value,
  })
}
</script>

<style scoped>
.board-filter {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin: 1rem auto;
}

.board-filter select,
.board-filter input {
  padding: 6px 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.9rem;
}

.board-filter input {
  width: 300px;
}

.board-filter button {
  padding: 6px 14px;
  background-color: #007bff;
  border: 1px solid #007bff;
  color: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.board-filter button:hover {
  background-color: #0056b3;
}
</style>
