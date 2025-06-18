<template>
  <div class="board-detail">
    <PostHeader :post="post" />
    <PostContent :post="post" />
    <PostActionButtons @edit="handleEdit" @delete="handleDelete" />
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import PostHeader from '@/components/board/boardHeader.vue'
import PostContent from '@/components/board/boardContent.vue'
import PostActionButtons from '@/components/board/boardEmitButton.vue'
import posts from '@/pages/board/data/board'

const route = useRoute()
const router = useRouter()
const postId = route.params.id

const post = posts.find(p => String(p.id) === String(postId))

const handleEdit = () => {
  router.push(`/board/boardeditform/${post.id}`)
}

const handleDelete = () => {
  const confirmed = confirm('정말 삭제하시겠습니까?')
  if (confirmed) {
    alert(`게시글 삭제: ${post.id}`)
  }
}
</script>

<style scoped>
.board-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 1.5rem;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
}
</style>