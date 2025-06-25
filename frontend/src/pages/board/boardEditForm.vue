<template>
  <div class="board-edit-form">
    <BoardHeaderForm :form="form" @submit="submitEdit" mode="edit" />
    <QuillEditor v-model="form.content" />
    <FileUpload ref="fileRef" :initial-files="initialFiles" />
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
  if (!form.value.category || !form.value.title || !form.value.content.trim()) {
    alert('모든 항목을 입력해주세요.')
    return
  }

  try {
    await axios.put(`/board_api/board/${postId}`, {
      board_category: form.value.category,
      board_title: form.value.title,
      board_content: form.value.content
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