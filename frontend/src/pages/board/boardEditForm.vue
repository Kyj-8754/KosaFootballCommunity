<template>
  <div class="board-edit-form">
    <BoardHeaderForm :form="form" @submit="submitEdit" mode="edit" />
    <QuillEditor v-model="form.content" />
    <FileUpload ref="fileRef" :initial-files="initialFiles" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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

const fetchPost = async () => {
  try {
    const response = await axios.get(`/board_api/board/${postId}`)
    const data = response.data
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
