<template>
  <div class="board-register-form">
    <BoardHeaderForm :form="form" @submit="submitPost" />
    <QuillEditor v-model="form.content" />
    <FileUpload ref="fileUploader" />
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRouter } from 'vue-router' // ✅ 추가
import axios from 'axios'

import BoardHeaderForm from '@/components/board/boardRegisterHeader.vue'
import QuillEditor from '@/components/board/boardEditer.vue'
import FileUpload from '@/components/file/FileUpload.vue'

const userNo = inject('userNo')
const userName = inject('userName')

const form = ref({
  category: '',
  title: '',
  content: ''
})

const fileUploader = ref(null)
const router = useRouter() // ✅ Router 인스턴스 생성

const submitPost = async () => {
  if (!form.value.category || !form.value.title || !form.value.content.trim()) {
    alert('모든 항목을 입력해주세요.')
    return
  }

  try {
    const response = await axios.post('/board_api/board', {
      board_category: form.value.category,
      board_title: form.value.title,
      board_content: form.value.content,
      user_no: userNo?.value ?? null,
      user_name: userName?.value ?? null
    })

    const boardId = response.data.board_id
    console.log('등록 결과:', response.data)

    if (fileUploader.value) {
      await fileUploader.value.uploadAllFiles(boardId)
    }

    alert('게시글이 등록되었습니다.')
    router.push('/board/boardlist') // ✅ 리스트 페이지로 이동
  } catch (error) {
    console.error('게시글 등록 실패:', error)
    alert('게시글 등록에 실패했습니다.')
  }
}

onMounted(() => {
  if (!userNo?.value || !userName?.value) {
    alert('로그인이 필요한 페이지입니다.')
    router.push('/board/boardlist')  // ✅ 로그인 페이지 경로에 맞게 수정
  }
})
</script>
