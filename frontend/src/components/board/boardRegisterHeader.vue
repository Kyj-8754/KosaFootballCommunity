<template>
  <div class="board-header-form">
  <select v-model="form.category">
    <option disabled value="">카테고리</option>
    <option
      v-for="cat in categories"
      :key="cat"
      :disabled="cat === '공지사항' && authCode !== 'ROLE_A1'"
    >
      {{ cat }}
    </option>
  </select>

    <input type="text" v-model="form.title" placeholder="제목을 입력해주세요" />

    <button @click="submit">{{ mode === 'edit' ? '수정' : '등록' }}</button>
  </div>
</template>


<script setup>
import { inject } from 'vue'
import { categories } from '@/assets/board/boardCategory.js'

const authCode = inject('authCode')

const props = defineProps({
  form: Object,
  mode: {
    type: String,
    default: 'create'
  }
})

const emit = defineEmits(['submit'])

const submit = () => {
  emit('submit')
}

</script>

<style scoped>
.board-header-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.75rem;
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.board-header-form select,
.board-header-form input {
  padding: 8px 12px;
  font-size: 0.95rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  flex: 1;
  min-width: 150px;
}

.board-header-form input {
  flex: 3;
}

.board-header-form button {
  padding: 8px 16px;
  font-size: 0.95rem;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: #fff;
  cursor: pointer;
  transition: background-color 0.2s;
}

.board-header-form button:hover {
  background-color: #0056b3;
}
</style>
