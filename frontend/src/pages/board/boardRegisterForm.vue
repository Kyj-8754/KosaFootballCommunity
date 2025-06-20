<template>
  <div class="board-register-form">
    <BoardHeaderForm :form="form" @submit="submitPost" />
    <QuillEditor v-model="form.content" />
    <FileUpload ref="fileUploader" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

import BoardHeaderForm from '@/components/board/boardRegisterHeader.vue'
import QuillEditor from '@/components/board/boardEditer.vue'
import FileUpload from '@/components/file/FileUpload.vue' // 👈 추가

const form = ref({
  category: '',
  title: '',
  content: ''
})

const fileUploader = ref(null) // 👈 FileUpload 참조

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
      user_no: 1, // 🔸 테스트용
      user_name: '테스트용 이름'
    })

    const boardId = response.data.board_id
    console.log('등록 결과:', response.data)

    // 🔸 게시글 등록 성공 후 파일 업로드 실행
    if (fileUploader.value) {
      await fileUploader.value.uploadAllFiles(boardId)
    }

    alert('게시글이 등록되었습니다.')
    // 이동 시:
    // router.push(`/board/boarddetail/${boardId}`)
  } catch (error) {
    console.error('게시글 등록 실패:', error)
    alert('게시글 등록에 실패했습니다.')
  }
}
</script>
