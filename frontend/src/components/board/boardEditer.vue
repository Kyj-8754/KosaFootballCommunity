<template>
  <div class="quill-editor">
    <QuillEditor
      v-model:content="content"
      contentType="html"
      theme="snow"
      ref="quillRef"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

const props = defineProps({
  modelValue: String
})
const emit = defineEmits(['update:modelValue'])
const content = defineModel('modelValue', { local: true })

const quillRef = ref(null)

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
        e.preventDefault() // 기본 이미지 붙여넣기 방지
        break
      }
    }
  })
})
</script>
