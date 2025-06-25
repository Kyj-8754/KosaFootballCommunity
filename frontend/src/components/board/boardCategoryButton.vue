<template>
  <div class="category-buttons">
    <button
      v-for="cat in categories"
      :key="cat"
      :class="{ active: selected === cat }"
      @click="toggleCategory(cat)"
    >
      {{ cat }}
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['select'])

const categories = [
    '공지사항', 
    '자유게시판', 
    '임시 게시판1', 
    '임시 게시판2', 
    '임시 게시판3', 
    '임시 게시판4', 
    '임시 게시판5'
]
const selected = ref('')

const toggleCategory = (value) => {
  selected.value = selected.value === value ? '' : value
  emit('select', selected.value) // ''일 경우 전체
}
</script>

<style scoped>
.category-buttons {
  display: flex;
  justify-content: left;
  gap: 0.5rem;
  margin: 1.5rem auto 1rem; /* ⬅ 상단 여백 추가됨 (기존 margin-bottom → margin 전체) */
}

.category-buttons button {
  padding: 6px 12px;
  border: 1px solid #ccc;
  background-color: white;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.category-buttons button.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}
</style>