<template>
  <div class="comment-list">
    <template v-for="item in pagedComments" :key="item.reply_id">
      <div :class="{ 'child-comments': item.parent_reply_id !== null }">
        <CommentItem
          :comment="item"
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
  comments: Array,
  userNo: Number,
  initialPage: {
    type: Number,
    default: 1
  }
})

const emit = defineEmits(['like', 'edit', 'delete', 'reply'])

const perPage = 5
const currentPage = ref(1)
let initialized = false

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

// 평탄화된 댓글 리스트
const flattenedComments = computed(() => {
  const result = []
  parentComments.value.forEach(parent => {
    result.push(parent)
    const children = childMap.value[parent.reply_id] || []
    result.push(...children)
  })
  return result
})

const totalPages = computed(() =>
  Math.ceil(flattenedComments.value.length / perPage)
)

const pagedComments = computed(() => {
  const start = (currentPage.value - 1) * perPage
  return flattenedComments.value.slice(start, start + perPage)
})

// 초기 페이지 설정
watch(
  () => props.comments,
  (newComments) => {
    if (!initialized && newComments.length > 0) {
      const totalCount = flattenedComments.value.length
      const lastPage = Math.max(1, Math.ceil(totalCount / perPage))
      currentPage.value = lastPage
      initialized = true
    }
  },
  { immediate: true, deep: true }
)

// 댓글 수 변동 시 현재 페이지 조정
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
