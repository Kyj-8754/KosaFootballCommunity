<template>
  <div class="board-register-form">
    <BoardHeaderForm :form="form" @submit="submitPost" />
    <QuillEditor v-model="form.content" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

import BoardHeaderForm from '@/components/board/boardRegisterHeader.vue'
import QuillEditor from '@/components/board/boardEditer.vue'

const form = ref({
  category: '',
  title: '',
  content: ''
})

const submitPost = async () => {
  if (!form.value.category || !form.value.title || !form.value.content.trim()) {
    alert('모든 항목을 입력해주세요.')
    return
  }

  try {
    const response = await axios.post('/api/board', {
      board_category: form.value.category,
      board_title: form.value.title,
      board_content: form.value.content,
      user_no: 1, // 🔸 테스트용: 실제 로그인 유저 정보로 대체 예정
      user_name: '테스트용 이름'     ,          // 🔸 테스트용: 실제 로그인 유저 정보로 대체 예정
    })

    alert('게시글이 등록되었습니다.')
    console.log('등록 결과:', response.data)

    // 이동할 경우:
    // router.push(`/board/boarddetail/${response.data.board_id}`)
  } catch (error) {
    console.error('게시글 등록 실패:', error)
    alert('게시글 등록에 실패했습니다.')
  }
}
</script>

