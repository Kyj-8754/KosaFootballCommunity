<template>
  <div class="sort-bar">
    <select v-model="selectedColumn" @change="updateSort">
      <option v-for="col in columns" :key="col.value" :value="col.value">
        {{ col.label }}
      </option>
    </select>

    <button @click="toggleDirection">
      {{ direction === 'asc' ? '오름차순 ▲' : '내림차순 ▼' }}
    </button>
  </div>
</template>


<script setup>
import { defineProps, defineEmits, ref, watch } from 'vue'

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

const selectedColumn = ref(props.sort.column || props.columns[0].value)
const direction = ref(props.sort.direction || 'desc')

// 드롭다운 변경 시
const updateSort = () => {
  emit('update:sort', {
    column: selectedColumn.value,
    direction: direction.value
  })
}

// 방향 토글
const toggleDirection = () => {
  direction.value = direction.value === 'asc' ? 'desc' : 'asc'
  updateSort()
}

// 외부 sort prop이 바뀔 경우 반영
watch(() => props.sort, (newSort) => {
  selectedColumn.value = newSort.column
  direction.value = newSort.direction
})
</script>


<style scoped>
.sort-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 1rem 0; /* board-filter와 통일 */
}

.sort-bar select {
  padding: 6px 10px;
  font-size: 0.9rem;     /* 🔁 board-filter와 통일 */
  height: 36px;          /* 🔁 높이 통일 */
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

.sort-bar button {
  padding: 6px 10px;     /* 🔁 board-filter와 통일 */
  font-size: 0.9rem;     /* 🔁 폰트 크기 통일 */
  height: 36px;          /* 🔁 높이 통일 */
  background-color: #007bff;
  color: white;
  border: 1px solid #007bff;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  box-sizing: border-box;
}

.sort-bar button:hover {
  background-color: #0056b3;
}
</style>
