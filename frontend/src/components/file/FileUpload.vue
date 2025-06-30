<template>
  <div class="file-upload">
    <!-- 파일 선택 -->
    <input type="file" multiple @change="handleFileSelect" />

    <!-- 업로드 예정 파일 -->
    <div v-if="queuedFiles.length">
      <h5>업로드 예정 파일</h5>
      <p style="margin: 0.5rem 0; font-size: 0.9rem; color: #555;">
        현재 용량: {{ totalSizeMB.toFixed(1) }}MB / 50MB
      </p>
      <ul>
        <li v-for="(file, index) in queuedFiles" :key="file.name">
          {{ file.name }} ({{ formatSize(file.size) }})
          <button @click="removeQueuedFile(index)">삭제</button>
        </li>
      </ul>
    </div>

    <!-- 기존 업로드된 파일 목록 -->
    <div v-if="uploadedFiles.length">
      <h5>기존 첨부 파일</h5>
      <ul>
        <li v-for="(file, index) in uploadedFiles" :key="file.file_id">
          {{ file.file_original_name }} ({{ formatSize(file.file_size) }})
          <button @click="removeUploadedFile(file.file_id, index)">삭제</button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  initialFiles: { type: Array, default: () => [] },
})
const emit = defineEmits(['uploaded'])

const queuedFiles = ref([])
const uploadedFiles = ref([...props.initialFiles])

watch(
  () => props.initialFiles,
  (newFiles) => {
    uploadedFiles.value = [...newFiles]
  },
  { immediate: true }
)

const MAX_SINGLE_FILE_SIZE = 10 * 1024 * 1024 // 10MB
const MAX_TOTAL_SIZE = 50 * 1024 * 1024 // 50MB

const formatSize = (bytes) => {
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + 'KB'
  return (bytes / (1024 * 1024)).toFixed(1) + 'MB'
}

const totalSizeMB = computed(() => {
  const uploaded = uploadedFiles.value.reduce((sum, f) => sum + (f.file_size || 0), 0)
  const queued = queuedFiles.value.reduce((sum, f) => sum + f.size, 0)
  return (uploaded + queued) / (1024 * 1024)
})

const handleFileSelect = (e) => {
  const selected = Array.from(e.target.files)
  const validFiles = []

  let currentTotal = uploadedFiles.value.reduce((sum, f) => sum + (f.file_size || 0), 0) +
                     queuedFiles.value.reduce((sum, f) => sum + f.size, 0)

  for (const file of selected) {
    if (file.size > MAX_SINGLE_FILE_SIZE) {
      alert(`${file.name}은 10MB를 초과하여 업로드할 수 없습니다.`)
      continue
    }

    if (currentTotal + file.size > MAX_TOTAL_SIZE) {
      alert(`총 첨부파일 용량이 50MB를 초과할 수 없습니다.`)
      break
    }

    currentTotal += file.size
    validFiles.push(file)
  }

  queuedFiles.value = [...queuedFiles.value, ...validFiles]
  e.target.value = ''
}

const removeQueuedFile = (index) => {
  queuedFiles.value.splice(index, 1)
}

const removeUploadedFile = async (fileId, index) => {
  try {
    await axios.delete(`/board_api/file/delete/${fileId}`)
    uploadedFiles.value.splice(index, 1)
  } catch (err) {
    alert('파일 삭제 실패')
  }
}

const uploadAllFiles = async (boardId) => {
  const formDataList = queuedFiles.value.map(file => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('board_id', boardId)
    return formData
  })

  const resultList = []
  for (const formData of formDataList) {
    try {
      const res = await axios.post('/board_api/file/upload', formData)
      if (res.data.file_id) {
        resultList.push(res.data.file_id)
      }
    } catch (err) {
      console.error('파일 업로드 실패:', err)
    }
  }

  emit('uploaded', resultList)
  queuedFiles.value = []
}

defineExpose({ uploadAllFiles })
</script>

<style scoped>
.file-upload {
  margin-top: 1.5rem;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background-color: #fefefe;
  border: 1px solid #ccc;
  border-radius: 6px;
}

.file-upload input[type="file"] {
  display: block;
  margin-bottom: 1rem;
}

.file-upload h5 {
  font-size: 1rem;
  font-weight: 600;
  margin: 1rem 0 0.5rem;
}

ul {
  list-style: none;
  padding-left: 0;
  margin: 0;
}

li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid #eee;
  font-size: 0.95rem;
}

button {
  padding: 4px 10px;
  font-size: 0.85rem;
  border: 1px solid #dc3545;
  background-color: #dc3545;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

button:hover {
  background-color: #a71d2a;
}
</style>