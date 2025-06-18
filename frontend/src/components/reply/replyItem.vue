<template>
  <div class="comment-item">
    <!-- 작성자 / 날짜 -->
    <div class="meta">
      <strong>{{ comment.author }}</strong>
      <span>{{ formatDate(comment.createdAt) }}</span>
    </div>

    <!-- 내용 or 수정창 -->
    <div class="content">
      <template v-if="isEditing">
        <textarea v-model="editedContent" rows="3"></textarea>
        <div class="edit-actions">
          <button @click="confirmEdit">확인</button>
          <button @click="cancelEdit">취소</button>
        </div>
      </template>
      <template v-else>
        <p>{{ comment.content }}</p>
      </template>
    </div>

    <!-- 좋아요 / 수정 / 삭제 -->
    <div class="actions">
      <button @click="$emit('like', comment.id)">👍 {{ comment.likes }}</button>
      <button @click="isEditing = true">수정</button>
      <button @click="$emit('delete', comment.id)">삭제</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  comment: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['like', 'edit', 'delete'])

const isEditing = ref(false)
const editedContent = ref(props.comment.content)

const confirmEdit = () => {
  emit('edit', props.comment.id, editedContent.value)
  isEditing.value = false
}

const cancelEdit = () => {
  editedContent.value = props.comment.content
  isEditing.value = false
}

const formatDate = (str) => str?.split(' ')[0] || ''
</script>

<style scoped>
.comment-item {
  border-bottom: 1px solid #ddd;
  padding: 0.75rem 0;
}
.meta {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 0.5rem;
}
.content {
  margin-bottom: 0.5rem;
}
.actions {
  display: flex;
  gap: 0.5rem;
}
.edit-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
}
textarea {
  width: 100%;
  resize: vertical;
}
</style>
