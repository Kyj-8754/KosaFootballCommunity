<template>
  <div class="board-detail">
    <PostHeader v-if="post" :post="post" />
    <PostContent v-if="post" :post="post" />
    <PostActionButtons v-if="post" @edit="handleEdit" @delete="handleDelete" />
    <CommentForm @submit="addComment" />
    <CommentList :comments="comments" />
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import axios from 'axios'

import PostHeader from '@/components/board/boardHeader.vue'
import PostContent from '@/components/board/boardContent.vue'
import PostActionButtons from '@/components/board/boardEmitButton.vue'
import CommentList from '@/components/reply/replyList.vue'
import CommentForm from '@/components/reply/replyRegisterForm.vue'

const route = useRoute()
const router = useRouter()

const postId = route.params.id
const post = ref(null)
const comments = ref([])

const fetchPost = async () => {
  try {
    const response = await axios.get(`/api/board/${postId}`)
    post.value = response.data
  } catch (error) {
    console.error('게시글 조회 실패:', error)
    alert('게시글을 불러오지 못했습니다.')
  }
}

const addComment = (content) => {
  alert(`애옹`)
}

const handleEdit = () => {
  if (post.value) {
    router.push(`/board/boardeditform/${post.value.board_id}`)
  }
}

const handleDelete = async () => {
  if (!post.value) return

  const confirmed = confirm('정말 삭제하시겠습니까?')
  if (!confirmed) return

  try {
    await axios.delete(`/api/board/${post.value.board_id}`)
    alert('게시글이 삭제되었습니다.')
    router.push('/board/list') // 목록 페이지로 이동
  } catch (error) {
    console.error('삭제 실패:', error)
    alert('게시글 삭제에 실패했습니다.')
  }
}

onMounted(async () => {
  try {
    const from = route.query.from

    // from=edit이 아닐 경우에만 조회수 증가
    if (from !== 'edit') {
      await axios.post(`/api/board/${postId}/increaseViewcount`)
    }

    // 쿼리 파라미터 제거 (조회수 증가 이후)
    if (from) {
      router.replace({ path: route.path })
    }

    await fetchPost()
  } catch (error) {
    console.error('초기 로딩 실패:', error)
    alert('게시글 정보를 불러오지 못했습니다.')
  }
})
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