<template>
  <div class="comment-item">
    <!-- 작성자 / 날짜 -->
    <div class="meta">
      <strong>{{ comment.user_name }}</strong> |
      <span>{{ formatDate(comment.reply_created_at) }}</span>
      <span v-if="comment.reply_modified_at">/{{ formatDate(comment.reply_modified_at) }}</span>
    </div>

    <!-- 내용 or 수정창 -->
    <div class="content">
      <template v-if="isEditing">
          <textarea
            v-model="editedContent"
            rows="3"
            maxlength="1000"
            style="height: 80px; resize: none; overflow: auto;"
          ></textarea>
        <div class="edit-actions">
          <button @click="confirmEdit">확인</button>
          <button @click="cancelEdit">취소</button>
        </div>
      </template>
      <template v-else>
        <p>{{ comment.reply_content }}</p>
      </template>
    </div>


    <!-- 수정 / 삭제 / 대댓글 -->
    <div class="actions" v-if="!isEditing">
      <!-- 수정: 작성자만 -->
      <button v-if="userNo === comment.user_no" @click="isEditing = true">수정</button>

      <!-- 삭제: 작성자 또는 관리자 -->
      <button
        v-if="userNo === comment.user_no || authCode === 'ROLE_A1'"
        @click="$emit('delete', comment.reply_id)"
      >
        삭제
      </button>

      <!-- 답글: 부모 댓글일 때만 -->
      <button
        v-if="!comment.parent_reply_id"
        @click="toggleReplying"
      >
        {{ isReplying ? '취소' : '답글' }}
      </button>
    </div>
        
    <!-- 대댓글 입력창 -->
    <CommentForm
      v-if="isReplying"
      :boardId="comment.board_id"
      :userNo="userNo"
      :userName="userName"
      :parentReplyId="comment.reply_id"
      @submit="handleReplySubmit"
    />

  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import CommentForm from '@/components/reply/replyRegisterForm.vue'

const userNo = inject('userNo')
const userName = inject('userName')
const authCode = inject('authCode')

const isReplying = ref(false)

const toggleReplying = () => {
  isReplying.value = !isReplying.value
}

const handleReplySubmit = (replyData) => {
  emit('reply', replyData)
  isReplying.value = false
}


const props = defineProps({
  comment: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['edit', 'delete', 'reply'])

const isEditing = ref(false)
const editedContent = ref(props.comment.reply_content)

const confirmEdit = () => {
  emit('edit', props.comment.reply_id, editedContent.value)
  isEditing.value = false
}

const cancelEdit = () => {
  editedContent.value = props.comment.reply_content
  isEditing.value = false
}

function formatDate(dateStr) {
  return dateStr ? dateStr.replace('T', ' ') : ''
}
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
.actions button,
.edit-actions button {
  padding: 6px 12px;
  background-color: #f4f4f4;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.actions button:hover,
.edit-actions button:hover {
  background-color: #e0e0e0;
}

.actions button:disabled,
.edit-actions button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

</style>
