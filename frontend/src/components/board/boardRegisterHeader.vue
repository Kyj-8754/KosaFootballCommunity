<template>
  <div class="board-header-form">
    <select v-model="form.category">
      <option disabled value="">카테고리</option>
      <option>공지사항</option>
      <option>자유게시판</option>
    </select>

    <input type="text" v-model="form.title" placeholder="제목을 입력해주세요" />

    <button @click="submit">{{ mode === 'edit' ? '수정' : '등록' }}</button>
  </div>
</template>

<script setup>
const props = defineProps({
  form: Object,
  mode: {
    type: String,
    default: 'create'  // 또는 'edit'
  }
})
const emit = defineEmits(['submit'])

const submit = () => {
  emit('submit')
}
</script>