<template>
  <div class="board-filter">
    <select v-model="category">
      <option value="">카테고리</option>
      <option>게시판 1</option>
      <option>게시판 2</option>
    </select>

    <select v-model="criteria">
      <option value="">검색 기준</option>
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

const category = ref('')
const criteria = ref('')
const keyword = ref('')

const emit = defineEmits(['search'])

const search = () => {
  emit('search', {
    category: category.value,
    criteria: criteria.value,
    keyword: keyword.value,
  })
}
</script>
