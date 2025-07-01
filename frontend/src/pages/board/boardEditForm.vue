<template>
  <div class="board-edit-form">
    <BoardHeaderForm :form="form" @submit="submitEdit" mode="edit" />
    <QuillEditor v-model="form.content" />
    <FileUpload ref="fileRef" :initial-files="initialFiles" />
    <div v-if="form.category === '모집게시판'" class="recruit-guide">
       여기서 예약이 가능하게끔? 할 생각 중<br>
      컴포넌트 아무거나 하나 넣어서 하는 방식으로.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

import BoardHeaderForm from '@/components/board/boardRegisterHeader.vue'
import QuillEditor from '@/components/board/boardEditer.vue'
import FileUpload from '@/components/file/FileUpload.vue'

const route = useRoute()
const router = useRouter()

const postId = route.params.id
const form = ref({
  category: '',
  title: '',
  content: ''
})
const initialFiles = ref([])
const fileRef = ref(null)

// 🔐 로그인 유저 정보
const userNo = inject('userNo')

const fetchPost = async () => {
  try {
    // ✅ 비로그인 차단
    if (!userNo?.value) {
      alert('로그인이 필요합니다.')
      router.replace('/member/loginForm') // 또는 목록 페이지 등
      return
    }

    const response = await axios.get(`/board_api/board/${postId}`)
    const data = response.data

    // ✅ 작성자와 불일치 시 차단
    if (userNo.value !== data.user_no) {
      alert('작성자만 접근할 수 있습니다.')
      router.replace('/board/boardlist')
      return
    }

    // 통과 시 폼 채우기
    form.value = {
      category: data.board_category,
      title: data.board_title,
      content: data.board_content
    }

    const fileRes = await axios.get(`/board_api/file/list/${postId}`)
    initialFiles.value = fileRes.data
  } catch (err) {
    console.error('게시글 불러오기 실패:', err)
    alert('게시글을 불러오지 못했습니다.')
    router.replace('/board/boardlist')
  }
}

const submitEdit = async () => {
  const title = form.value.title.trim()
  const content = form.value.content.trim()

  if (!form.value.category || !title || !content) {
    alert('모든 항목을 입력해주세요.')
    return
  }

  // 제목 글자 수 제한
  if (title.length > 100) {
    alert('제목은 100자 이하로 입력해주세요.')
    return
  }

  // 내용 바이트 수 제한 (UTF-8 기준 16MB)
  const contentByteLength = new Blob([content]).size
  if (contentByteLength > 16_777_215) {
    alert('내용이 너무 깁니다. 최대 16MB까지 입력할 수 있습니다.')
    return
  }

  try {
    await axios.put(`/board_api/board/${postId}`, {
      board_category: form.value.category,
      board_title: title,
      board_content: content
    })

    if (fileRef.value) {
      await fileRef.value.uploadAllFiles(postId)
    }

    alert('게시글이 수정되었습니다.')
    router.push(`/board/boarddetail/${postId}?from=edit`)
  } catch (err) {
    console.error('게시글 수정 실패:', err)
    alert('게시글 수정에 실패했습니다.')
  }
}

onMounted(fetchPost)
</script>