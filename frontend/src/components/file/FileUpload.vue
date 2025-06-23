<template>
  <div class="file-upload">
    <!-- 파일 선택 -->
    <input type="file" multiple @change="handleFileSelect" />

    <!-- 대기 중인 파일 목록 -->
    <div v-if="queuedFiles.length">
      <h5>업로드 예정 파일</h5>
      <ul>
        <li v-for="(file, index) in queuedFiles" :key="file.name">
          {{ file.name }}
          <button @click="removeQueuedFile(index)">삭제</button>
        </li>
      </ul>
    </div>

    <!-- 기존 업로드된 파일 목록 -->
    <div v-if="uploadedFiles.length">
      <h5>기존 첨부 파일</h5>
      <ul>
        <li v-for="(file, index) in uploadedFiles" :key="file.file_id">
          {{ file.file_original_name }}
          <button @click="removeUploadedFile(file.file_id, index)">삭제</button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'

// props: 수정 모드에서 초기 파일 목록 받기
const props = defineProps({
  initialFiles: { type: Array, default: () => [] },
})

// emit: 부모가 업로드 성공 후 file_id들을 받을 수 있도록
const emit = defineEmits(['uploaded'])

const queuedFiles = ref([]) // 업로드 대기
const uploadedFiles = ref([...props.initialFiles]) // 이미 업로드된 것들

watch(
  () => props.initialFiles,
  (newFiles) => {
    uploadedFiles.value = [...newFiles]
  },
  { immediate: true }
)

const handleFileSelect = (e) => {
  queuedFiles.value = [...queuedFiles.value, ...e.target.files]
  e.target.value = '' // 같은 파일 재선택 가능하게 초기화
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

// 게시글 등록 이후 파일 업로드 수행
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

  // 업로드 성공한 파일 id 전달
  emit('uploaded', resultList)
  queuedFiles.value = []
}

// 외부에서 uploadAllFiles를 쓸 수 있게 expose
defineExpose({ uploadAllFiles })
</script>

<style scoped>
.file-upload {
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
