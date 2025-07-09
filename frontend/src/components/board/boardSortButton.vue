<template>
  <div class="sort-bar">
    <select
      v-model="selectedColumn"
      @change="updateSort"
      :disabled="isRecruitBoard"
    >
      <option v-for="col in filteredColumns" :key="col.value" :value="col.value">
        {{ col.label }}
      </option>
    </select>

    <button @click="toggleDirection">
      {{ direction === 'asc' ? '오름차순 ▲' : '내림차순 ▼' }}
    </button>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, watch, computed, onMounted } from 'vue'

const props = defineProps({
  sort: Object, // { column: '', direction: '' }
  columns: {
    type: Array,
    default: () => [
      { label: '등록순', value: 'board_id' },
      { label: '조회순', value: 'board_viewcount' },
      { label: '추천순', value: 'board_likecount' }
    ]
  },
  category: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:sort'])

const isRecruitBoard = computed(() => props.category === '모집게시판')

const filteredColumns = computed(() => {
  return isRecruitBoard.value
    ? [{ label: '등록순', value: 'board_id' }]
    : props.columns
})

const selectedColumn = ref(props.sort.column || props.columns[0].value)
const direction = ref(props.sort.direction || 'desc')

// 마운트 시 모집게시판이면 정렬 강제 고정
onMounted(() => {
  if (isRecruitBoard.value) {
    selectedColumn.value = 'board_id'
    emit('update:sort', {
      column: 'board_id',
      direction: direction.value
    })
  }
})

const updateSort = () => {
  emit('update:sort', {
    column: selectedColumn.value,
    direction: direction.value
  })
}

const toggleDirection = () => {
  direction.value = direction.value === 'asc' ? 'desc' : 'asc'
  updateSort()
}

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
  margin: 1rem 0;
}

.sort-bar select {
  padding: 6px 10px;
  font-size: 0.9rem;
  height: 36px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

.sort-bar button {
  padding: 6px 10px;
  font-size: 0.9rem;
  height: 36px;
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