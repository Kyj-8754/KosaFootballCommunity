<template>
  <div class="board-detail">
    <PostHeader v-if="post" :post="post" />
    <PostContent v-if="post" :post="post" />
    <PostActionButtons v-if="post" @edit="handleEdit" @delete="handleDelete" />
    <CommentForm
      v-if="post"
      :boardId="post.board_id"
      :userNo="1"
      :userName="'댓글 테스트 사용자'"
      @submit="addComment"
    />
    <CommentList
      :comments="comments"
      @edit="editComment"
      @delete="deleteComment"
    />
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

const addComment = async (replyData) => {
  try {
    await axios.post('/api/reply', replyData)
    await fetchComments()
  } catch (error) {
    console.error('댓글 등록 실패:', error)
    alert('댓글 등록에 실패했습니다.')
  }
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

const fetchComments = async () => {
  try {
    const response = await axios.get(`/api/reply/list/${postId}`)
    comments.value = response.data
  } catch (error) {
    console.error('댓글 목록 조회 실패:', error)
    alert('댓글을 불러오지 못했습니다.')
  }
}

// 댓글 수정
const editComment = async (replyId, newContent) => {
  try {
    await axios.put(`/api/reply/${replyId}`, {
      reply_content: newContent
    })
    await fetchComments()
  } catch (error) {
    console.error('댓글 수정 실패:', error)
    alert('댓글 수정에 실패했습니다.')
  }
}

// 댓글 삭제
const deleteComment = async (replyId) => {
  const confirmed = confirm('댓글을 삭제하시겠습니까?')
  if (!confirmed) return

  try {
    await axios.delete(`/api/reply/${replyId}`)
    await fetchComments()
  } catch (error) {
    console.error('댓글 삭제 실패:', error)
    alert('댓글 삭제에 실패했습니다.')
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
    await fetchComments()
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