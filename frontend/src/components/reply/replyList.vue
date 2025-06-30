<template>
  <div class="comment-list">
    <template v-for="comment in pagedParentComments" :key="comment.reply_id">
      <!-- 부모 댓글 -->
      <CommentItem
        :comment="comment"
        @like="handleLike"
        @edit="handleEdit"
        @delete="handleDelete"
        @reply="handleReply"
      />

      <!-- 대댓글 -->
      <div
        class="child-comments"
        v-for="child in childMap[comment.reply_id] || []"
        :key="child.reply_id"
      >
        <CommentItem
          :comment="child"
          @like="handleLike"
          @edit="handleEdit"
          @delete="handleDelete"
          @reply="handleReply"
        />
      </div>
    </template>

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

const emit = defineEmits(['like', 'edit', 'delete', 'reply'])

const currentPage = ref(1)
const perPage = 5

const handleReply = (replyData) => emit('reply', replyData)
const handleLike = (id) => emit('like', id)
const handleEdit = (id, newContent) => emit('edit', id, newContent)
const handleDelete = (id) => emit('delete', id)

const parentComments = computed(() =>
  props.comments.filter(c => c.parent_reply_id === null)
)

const childMap = computed(() => {
  const map = {}
  props.comments.forEach(c => {
    if (c.parent_reply_id !== null) {
      if (!map[c.parent_reply_id]) map[c.parent_reply_id] = []
      map[c.parent_reply_id].push(c)
    }
  })
  return map
})

const totalPages = computed(() => Math.ceil(parentComments.value.length / perPage))

const pagedParentComments = computed(() => {
  const start = (currentPage.value - 1) * perPage
  return parentComments.value.slice(start, start + perPage)
})

watch(() => props.comments.length, () => {
  if (currentPage.value > totalPages.value) {
    currentPage.value = 1
  }
})

const handlePageChange = (page) => {
  currentPage.value = page
}
</script>

<style scoped>
.comment-list {
  padding: 1rem 0;
}
.child-comments {
  margin-left: 2rem;
  border-left: 2px solid #eee;
  padding-left: 1rem;
  margin-top: 0.5rem;
}
</style>
