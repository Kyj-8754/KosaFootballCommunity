<template>
  <div class="board-register-form">
    <BoardHeaderForm :form="form" @submit="submitPost" />
    <QuillEditor v-model="form.content" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import BoardHeaderForm from '@/components/board/boardRegisterHeader.vue'
import QuillEditor from '@/components/board/boardEditer.vue'

const form = ref({
  category: '',
  title: '',
  content: ''
})

const submitPost = () => {
  if (!form.value.category || !form.value.title || !form.value.content.trim()) {
    alert('모든 항목을 입력해주세요.')
    return
  }

  console.log('등록된 게시글:', form.value)
  alert('게시글이 등록되었습니다.')
}
</script>
