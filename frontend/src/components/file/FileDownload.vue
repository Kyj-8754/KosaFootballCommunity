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
    const res = await axios.get(`/board_api/file/list/${props.boardId}`)
    files.value = res.data
  } catch (err) {
    console.error('파일 목록 조회 실패:', err)
  }
}

const download = (fileId) => {
  // 파일 다운로드: a 태그로 직접 트리거
  const url = `/board_api/file/download/${fileId}`  // ✅ 수정된 경로
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
  margin-top: 1.5rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.file-download h5 {
  margin-bottom: 0.75rem;
  font-size: 1rem;
  font-weight: 600;
}

ul {
  list-style: none;
  padding-left: 0;
  margin: 0;
}

li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px solid #eee;
  font-size: 0.95rem;
}

button {
  padding: 4px 10px;
  font-size: 0.85rem;
  border: 1px solid #007bff;
  background-color: #007bff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

button:hover {
  background-color: #0056b3;
}
</style>
