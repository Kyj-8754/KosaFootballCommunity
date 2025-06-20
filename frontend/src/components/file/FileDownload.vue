<template>
  <div class="file-download" v-if="files.length">
    <h5>첨부 파일</h5>
    <ul>
      <li v-for="file in files" :key="file.file_id">
        {{ file.file_original_name }}
        <button @click="download(file.file_id)">다운로드</button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  boardId: {
    type: Number,
    required: true,
  },
})

const files = ref([])

const fetchFiles = async () => {
  try {
    const res = await axios.get(`/api/file/list/${props.boardId}`)
    files.value = res.data
  } catch (err) {
    console.error('파일 목록 조회 실패:', err)
  }
}

const download = (fileId) => {
  // 파일 다운로드: a 태그로 직접 트리거
  const url = `/api/file/download/${fileId}`
  const link = document.createElement('a')
  link.href = url
  link.setAttribute('download', '') // 다운로드 트리거
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

onMounted(fetchFiles)
watch(() => props.boardId, fetchFiles)
</script>

<style scoped>
.file-download {
  margin-top: 1rem;
}
ul {
  padding-left: 1rem;
}
li {
  margin-bottom: 0.25rem;
}
button {
  margin-left: 0.5rem;
}
</style>