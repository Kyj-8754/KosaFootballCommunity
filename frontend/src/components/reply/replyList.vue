<template>
  <div class="comment-list">
    <CommentItem
      v-for="comment in pagedComments"
      :key="comment.reply_id"
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
import { ref, computed, watch } from 'vue'
import CommentItem from '@/components/reply/replyItem.vue'
import Pagination from '@/components/pagination.vue'

const props = defineProps({
  comments: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['like', 'edit', 'delete'])

const currentPage = ref(1)
const perPage = 5

const totalPages = computed(() => Math.ceil(props.comments.length / perPage))

const pagedComments = computed(() => {
  const start = (currentPage.value - 1) * perPage
  return props.comments.slice(start, start + perPage)
})

const handlePageChange = (page) => {
  currentPage.value = page
}

const handleLike = (id) => emit('like', id)
const handleEdit = (id, newContent) => emit('edit', id, newContent)
const handleDelete = (id) => emit('delete', id)

// 댓글 수가 바뀌면 첫 페이지로
watch(() => props.comments.length, () => {
  if (currentPage.value > totalPages.value) {
    currentPage.value = 1
  }
})
</script>

<style scoped>
.comment-list {
  padding: 1rem 0;
}
</style>