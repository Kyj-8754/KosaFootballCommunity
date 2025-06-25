<template>
  <div class="category-tabs">
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
  emit('select', selected.value)
}
</script>

<style scoped>
.category-tabs {
  display: flex;
  gap: 1rem;
  border-top: 2px solid #ddd;     /* ✅ 상단 선 추가 */
  padding-top: 0.5rem;            /* ✅ 상단 패딩 (기존 bottom → top) */
  margin: 0.5rem 0 0;
  flex-wrap: wrap;
}

.category-tabs button {
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  font-size: 1rem;
  padding: 8px 4px;
  cursor: pointer;
  color: #333;
  transition: all 0.2s;
}

.category-tabs button:hover {
  color: #007bff;
}

.category-tabs button.active {
  border-color: #007bff;
  color: #007bff;
  font-weight: bold;
}
</style>