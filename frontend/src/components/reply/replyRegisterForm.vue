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

const props = defineProps({
  boardId: { type: Number, required: true },
  userNo: { type: Number, required: true },
  userName: { type: String, required: true },
  parentReplyId: { type: Number, default: null } // 대댓글 지원용 (옵션)
})

const newComment = ref('')

const submitComment = () => {
  if (!newComment.value.trim()) {
    alert('댓글 내용을 입력해주세요.')
    return
  }

  const replyData = {
    board_id: props.boardId,
    user_no: props.userNo,
    user_name: props.userName,
    reply_content: newComment.value.trim(),
    parent_reply_id: props.parentReplyId
  }

  emit('submit', replyData)
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
  background-color: #007bff;
  border: 1px solid #007bff;
  color: #fff;
  border-radius: 4px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

button:hover {
  background-color: #0056b3;
}

</style>
