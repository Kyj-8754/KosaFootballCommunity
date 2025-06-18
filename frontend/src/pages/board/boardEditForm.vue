<template>
  <div class="board-edit-form">
    <BoardHeaderForm :form="form" @submit="submitEdit" mode="edit" />
    <QuillEditor v-model="form.content" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

import BoardHeaderForm from '@/components/board/boardRegisterHeader.vue'
import QuillEditor from '@/components/board/boardEditer.vue'

const route = useRoute()
const router = useRouter()

const postId = route.params.id
const form = ref({
  category: '',
  title: '',
  content: ''
})

// 게시글 기존 데이터 불러오기
const fetchPost = async () => {
  try {
    const response = await axios.get(`/api/board/${postId}`)
    const data = response.data
    form.value = {
      category: data.board_category,
      title: data.board_title,
      content: data.board_content
    }
  } catch (err) {
    console.error('게시글 불러오기 실패:', err)
    alert('게시글을 불러오지 못했습니다.')
  }
}

// 게시글 수정 요청
const submitEdit = async () => {
  if (!form.value.category || !form.value.title || !form.value.content.trim()) {
    alert('모든 항목을 입력해주세요.')
    return
  }

  try {
    await axios.put(`/api/board/${postId}`, {
      board_category: form.value.category,
      board_title: form.value.title,
      board_content: form.value.content,
      board_status: 'active',   // 또는 원래 값 유지
      user_no: 1                // 임시. 실제 로그인 사용자로 교체 예정
    })

    alert('게시글이 수정되었습니다.')
    router.push(`/board/boarddetail/${postId}`)
  } catch (err) {
    console.error('게시글 수정 실패:', err)
    alert('게시글 수정에 실패했습니다.')
  }
}

onMounted(fetchPost)
</script>
