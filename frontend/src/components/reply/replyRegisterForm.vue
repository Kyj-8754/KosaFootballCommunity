<template>
  <div class="comment-form">
    <textarea
      v-model="newComment"
      placeholder="내용을 입력해주세요"
      rows="3"
    ></textarea>
    <div class="form-actions">
      <button @click="submitComment">등록</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['submit'])
const newComment = ref('')

const submitComment = () => {
  if (!newComment.value.trim()) {
    alert('댓글 내용을 입력해주세요.')
    return
  }

  emit('submit', newComment.value.trim())
  newComment.value = ''
}
</script>

<style scoped>
.comment-form {
  margin-top: 1rem;
  border-top: 1px solid #ccc;
  padding-top: 1rem;
}
textarea {
  width: 100%;
  resize: vertical;
  padding: 8px;
  font-size: 0.9rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.5rem;
}
button {
  padding: 6px 12px;
}
</style>
