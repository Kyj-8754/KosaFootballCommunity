<template>
  <div class="comment-list">
    <CommentItem
      v-for="comment in pagedComments"
      :key="comment.id"
      :comment="comment"
      @like="handleLike"
      @edit="handleEdit"
      @delete="handleDelete"
    />

    <Pagination
      :currentPage="currentPage"
      :totalPages="totalPages"
      @changePage="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import CommentItem from '@/components/reply/replyItem.vue'
import Pagination from '@/components/pagination.vue'

// 더미 댓글
const comments = ref([
  { id: 1, author: '회원 1', content: '댓글 예시 1', createdAt: '2025-06-17 18:00:00', likes: 2 },
  { id: 2, author: '회원 1', content: '댓글 예시 2', createdAt: '2025-06-17 18:02:00', likes: 0 },
  { id: 3, author: '회원 1', content: '댓글 예시 3', createdAt: '2025-06-17 18:03:00', likes: 1 },
  { id: 4, author: '회원 1', content: '댓글 예시 4', createdAt: '2025-06-17 18:05:00', likes: 0 },
  { id: 5, author: '회원 1', content: '댓글 예시 5', createdAt: '2025-06-17 18:06:00', likes: 3 },
  { id: 6, author: '회원 1', content: '댓글 예시 6', createdAt: '2025-06-17 18:07:00', likes: 1 }
])

const currentPage = ref(1)
const perPage = 5

const totalPages = computed(() => Math.ceil(comments.value.length / perPage))

const pagedComments = computed(() => {
  const start = (currentPage.value - 1) * perPage
  return comments.value.slice(start, start + perPage)
})

const handlePageChange = (page) => {
  currentPage.value = page
}

const handleLike = (id) => {
  const comment = comments.value.find(c => c.id === id)
  if (comment) comment.likes += 1
}

const handleEdit = (id, newContent) => {
  const comment = comments.value.find(c => c.id === id)
  if (comment) comment.content = newContent
}

const handleDelete = (id) => {
  comments.value = comments.value.filter(c => c.id !== id)
}
</script>

<style scoped>
.comment-list {
  padding: 1rem 0;
}
</style>
