<template>
  <div class="board-detail">
    <PostHeader v-if="post" :post="post" />
    <PostContent
      v-if="post"
      :post="post"
      :liked="liked"
      :likeCount="likeCount"
      @toggle-like="toggleLike"
    />
   <LikeButton
      v-if="post"
      :liked="liked"
      :likeCount="likeCount"
      @toggle-like="toggleLike"
    />
    <FileDownload v-if="post" :board-id="post.board_id" />
    <PostActionButtons
      v-if="post"
      :userNo="userNo"
      :postUserNo="post.user_no"
      :authCode="authCode"
      @edit="handleEdit"
      @delete="handleDelete"
    />
    <CommentForm
      v-if="post"
      :boardId="post.board_id"
      :userNo="userNo"
      :userName="userName"
      @submit="addComment"
    />
    <CommentList
      :comments="comments"
      :userNo="userNo"
      @edit="editComment"
      @delete="deleteComment"
      @reply="addComment"
    />
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ref, onMounted, inject } from 'vue'
import axios from 'axios'

import PostHeader from '@/components/board/boardHeader.vue'
import PostContent from '@/components/board/boardContent.vue'
import PostActionButtons from '@/components/board/boardEmitButton.vue'
import CommentList from '@/components/reply/replyList.vue'
import CommentForm from '@/components/reply/replyRegisterForm.vue'
import LikeButton from '@/components/board/boardLikeButton.vue'
import FileDownload from '@/components/file/FileDownload.vue'

const route = useRoute()
const router = useRouter()

const postId = route.params.id
const post = ref(null)
const comments = ref([])
const liked = ref(false)
const likeCount = ref(0)
const userNo = inject('userNo')
const userName = inject('userName')
const authCode = inject('authCode')

const fetchPost = async () => {
  try {
    const response = await axios.get(`/board_api/board/${postId}`)
    post.value = response.data
  } catch (error) {
    console.error('게시글 조회 실패:', error)
    alert('게시글을 불러오지 못했습니다.')
  }
}

const fetchComments = async () => {
  try {
    const response = await axios.get(`/board_api/reply/list/${postId}`)
    comments.value = response.data
  } catch (error) {
    console.error('댓글 목록 조회 실패:', error)
    alert('댓글을 불러오지 못했습니다.')
  }
}

const fetchLikeCount = async () => {
  if (!post.value) return
  try {
    const response = await axios.get('/board_api/board/like/count', {
      params: { board_id: post.value.board_id }
    })
    likeCount.value = response.data.likeCount
  } catch (error) {
    console.error('좋아요 수 조회 실패:', error)
  }
}

const fetchLiked = async () => {
  if (!post.value) return
  try {
    const response = await axios.post('/board_api/board/like/check', {
      board_id: post.value.board_id,
      user_no: userNo?.value ?? null
    })

    liked.value = response.data.liked
  } catch (error) {
    console.error('좋아요 여부 조회 실패:', error)
  }
}


const toggleLike = async () => {
  if (!post.value) return

  try {
    if (!liked.value) {
      await axios.post('/board_api/board/like', null, {
        params: {
          board_id: post.value.board_id,
          user_no: userNo?.value ?? null
        }
      })
    } else {
      await axios.delete('/board_api/board/like', {
        params: {
          board_id: post.value.board_id,
          user_no: userNo?.value ?? null
        }
      })
    }

    // 상태 새로고침
    await fetchLikeCount()
    await fetchLiked()
  } catch (err) {
    console.error('좋아요 처리 실패:', err)
    alert('좋아요 처리 중 오류가 발생했습니다.')
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
    await axios.delete(`/board_api/board/${post.value.board_id}`)
    alert('게시글이 삭제되었습니다.')
    router.push('/board/list')
  } catch (error) {
    console.error('삭제 실패:', error)
    alert('게시글 삭제에 실패했습니다.')
  }
}

const addComment = async (replyData) => {
  try {
    await axios.post('/board_api/reply', replyData)
    await fetchComments()
  } catch (error) {
    console.error('댓글 등록 실패:', error)
    alert('댓글 등록에 실패했습니다.')
  }
}

const editComment = async (replyId, newContent) => {
  try {
    await axios.put(`/board_api/reply/${replyId}`, {
      reply_content: newContent
    })
    await fetchComments()
  } catch (error) {
    console.error('댓글 수정 실패:', error)
    alert('댓글 수정에 실패했습니다.')
  }
}

const deleteComment = async (replyId) => {
  const confirmed = confirm('댓글을 삭제하시겠습니까?')
  if (!confirmed) return

  try {
    await axios.delete(`/board_api/reply/${replyId}`)
    await fetchComments()
  } catch (error) {
    console.error('댓글 삭제 실패:', error)
    alert('댓글 삭제에 실패했습니다.')
  }
}

onMounted(async () => {
  try {
    const from = route.query.from

    if (from !== 'edit') {
      await axios.post(`/board_api/board/${postId}/increaseViewcount`)
    }

    if (from) {
      router.replace({ path: route.path })
    }

    await fetchPost()
    await fetchComments()
    await fetchLikeCount()
    await fetchLiked()  // 좋아요 상태도 함께 초기화

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
