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

const MAX_TITLE_LENGTH = 100
const MAX_CONTENT_BYTES = 16_777_215 // MySQL MEDIUMTEXT 최대 바이트 수

// 문자열 바이트 길이 계산 함수 (UTF-8 기준)
function getByteLength(str) {
  return new Blob([str]).size
}

const submitPost = async () => {
  const title = form.value.title.trim()
  const content = form.value.content.trim()

  // 제목/내용/카테고리 미입력 검사
  if (!form.value.category || !title || !content) {
    alert('모든 항목을 입력해주세요.')
    return
  }

  // 제목 글자 수 제한 (문자 수 기준)
  if (title.length > 100) {
    alert('제목은 100자 이하로 입력해주세요.')
    return
  }

  // 내용 바이트 수 제한 (UTF-8 기준)
  const contentByteLength = new Blob([content]).size
  if (contentByteLength > 16_777_215) {
    alert('내용이 너무 깁니다. 최대 16MB까지 입력할 수 있습니다.')
    return
  }

  try {
    const response = await axios.post('/board_api/board', {
      board_category: form.value.category,
      board_title: title,
      board_content: content,
      user_no: userNo?.value ?? null,
      user_name: userName?.value ?? null
    })

    const boardId = response.data.board_id
    console.log('등록 결과:', response.data)

    if (fileUploader.value) {
      await fileUploader.value.uploadAllFiles(boardId)
    }

    alert('게시글이 등록되었습니다.')
    router.push('/board/boardlist')
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
