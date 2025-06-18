<template>
  <div class="board-edit-form">
    <BoardHeaderForm :form="form" @submit="submitEdit" mode="edit" />
    <QuillEditor v-model="form.content" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'

import BoardHeaderForm from '@/components/board/boardRegisterHeader.vue'
import QuillEditor from '@/components/board/boardEditer.vue'
import posts from '@/pages/board/data/board'

const route = useRoute()
const postId = route.params.id
const original = posts.find(p => String(p.id) === String(postId))

const form = ref({
  category: original.category,
  title: original.title,
  content: original.content
})

const submitEdit = () => {
  if (!form.value.category || !form.value.title || !form.value.content.trim()) {
    alert('모든 항목을 입력해주세요.')
    return
  }

  console.log(`수정된 게시글(${postId}):`, form.value)
  alert('게시글이 수정되었습니다.')
}
</script>
