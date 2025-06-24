<template>
  <div class="sort-buttons">
    <button
      v-for="col in columns"
      :key="col.value"
      @click="handleClick(col.value)"
      :class="{ active: sort.column === col.value }"
    >
      {{ col.label }}
      <span v-if="sort.column === col.value">
        {{ sort.direction === 'asc' ? '▲' : '▼' }}
      </span>
    </button>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  sort: Object, // { column: '', direction: '' }
  columns: {
    type: Array,
    default: () => [
      { label: '등록순', value: 'board_id' },
      { label: '조회순', value: 'board_viewcount' },
      { label: '추천순', value: 'board_likecount' }
    ]
  }
})

const emit = defineEmits(['update:sort'])

const handleClick = (column) => {
  let direction = 'desc'
  if (props.sort.column === column) {
    direction = props.sort.direction === 'asc' ? 'desc' : 'asc'
  }
  emit('update:sort', { column, direction })
}
</script>

<style scoped>
.sort-buttons {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 1.5rem auto 1rem;
}

.sort-buttons button {
  padding: 6px 12px;
  border: 1px solid #ccc;
  background-color: white;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s, color 0.2s, border-color 0.2s;
  font-size: 0.95rem;
}

.sort-buttons button.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}
</style>
