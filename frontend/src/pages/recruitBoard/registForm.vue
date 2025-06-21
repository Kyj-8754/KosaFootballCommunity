<template>
  <div class="container my-5" style="max-width: 900px;">
    <h2 class="fw-bold mb-3">모집글 등록</h2>

    <form @submit.prevent="submitForm">
      <div class="mb-3">
        <label class="form-label">제목</label>
        <input v-model="title" class="form-control" required />
      </div>

      <div class="mb-3">
        <label class="form-label">내용</label>
        <div class="quill-editor">
          <QuillEditor
            v-model:content="content"
            contentType="html"
            theme="snow"
            ref="quillRef"
          />
        </div>
      </div>

      <!-- 작성자 및 클럽ID는 숨김 처리 -->
      <input type="hidden" v-model="writer" />
      <input type="hidden" v-model="club_id" />

      <button class="btn btn-primary">등록</button>
      <router-link to="/recruitBoard" class="btn btn-secondary ms-2">취소</router-link>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import axios from 'axios'

const title = ref('')
const content = ref('')
const writer = ref(sessionStorage.getItem('loginUserid') || '')
const club_id = ref('') // 실제 로직에서 세션 또는 URL 기반으로 세팅

const quillRef = ref(null)
const router = useRouter()

onMounted(() => {
  const quill = quillRef.value?.getQuill()
  if (!quill) return

  quill.root.addEventListener('paste', async (e) => {
    const clipboardItems = e.clipboardData?.items
    if (!clipboardItems) return

    for (const item of clipboardItems) {
      if (item.type.indexOf('image') !== -1) {
        const file = item.getAsFile()
        const reader = new FileReader()

        reader.onload = (event) => {
          const base64 = event.target?.result
          if (typeof base64 === 'string') {
            const range = quill.getSelection(true)
            quill.insertEmbed(range.index, 'image', base64)
            quill.setSelection(range.index + 1)
          }
        }

        reader.readAsDataURL(file)
        e.preventDefault()
        break
      }
    }
  })
})

const submitForm = async () => {
  try {
    await axios.post(`/api/recruits?user_id=${writer.value}`, {
      title: title.value,
      content: content.value,
      writer: writer.value,
      club_id: club_id.value
    })
    alert('등록이 완료되었습니다.')
    router.push('/recruitBoard')
  } catch (e) {
    if (e.response && e.response.status === 403) {
      alert('팀장만 모집글을 작성할 수 있습니다.')
    } else {
      alert('등록에 실패했습니다.')
    }
    console.error(e)
  }
}
</script>

<style scoped>
.quill-editor {
  border: 1px solid #ccc;
  border-radius: 6px;
}

.quill-editor :deep(.ql-editor) {
  min-height: 400px;
  padding: 12px;
  font-size: 1rem;
}
</style>
