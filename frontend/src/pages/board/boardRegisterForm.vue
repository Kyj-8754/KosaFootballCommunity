<template>
  <div class="board-register-form">
    <!-- 제목/카테고리 -->
    <BoardHeaderForm :form="form" @submit="submitPost" />

    <!-- 모집게시판인 경우만 탭 표시 -->
    <div v-if="form.category === '모집게시판'" class="mb-3">
      <ul class="nav nav-tabs">
        <li class="nav-item">
          <button class="nav-link" :class="{ active: activeTab === 'content' }" @click="activeTab = 'content'">
            ✍️ 내용 작성
          </button>
        </li>
        <li class="nav-item">
          <button class="nav-link" :class="{ active: activeTab === 'reservation' }" @click="activeTab = 'reservation'">
            🏟️ 예약 정보
          </button>
        </li>
      </ul>
    </div>

    <!-- 탭 전환 콘텐츠 -->
    <div v-if="form.category === '모집게시판'">
      <div v-show="activeTab === 'content'">
        <QuillEditor v-model="form.content" />
        <FileUpload ref="fileUploader" />
      </div>
      <div v-show="activeTab === 'reservation'">
        <Reservation ref="reservationRef" />
      </div>
    </div>

    <!-- 모집게시판이 아닌 경우 기본적으로 에디터만 표시 -->
    <div v-else>
      <QuillEditor v-model="form.content" />
      <FileUpload ref="fileUploader" />
    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import BoardHeaderForm from '@/components/board/boardRegisterHeader.vue'
import QuillEditor from '@/components/board/boardEditer.vue'
import FileUpload from '@/components/file/FileUpload.vue'
import Reservation from '@/components/board/match/reservation/reservation.vue'

const userNo = inject('userNo')
const userName = inject('userName')

const form = ref({
  category: '',
  title: '',
  content: ''
})

const activeTab = ref('content')
const fileUploader = ref(null)
const router = useRouter()

const reservationRef = ref(null)

const submitPost = async () => {
  const title = form.value.title.trim()
  const content = form.value.content.trim()

  if (!form.value.category || !title || !content) {
    alert('모든 항목을 입력해주세요.')
    return
  }

  if (title.length > 100) {
    alert('제목은 100자 이하로 입력해주세요.')
    return
  }

  const contentByteLength = new Blob([content]).size
  if (contentByteLength > 16_777_215) {
    alert('내용이 너무 깁니다. 최대 16MB까지 입력할 수 있습니다.')
    return
  }

  try {
    let reservationId = null

    // ✅ 1단계: 예약 수행 (모집게시판일 경우만)
    if (form.value.category === '모집게시판') {
      const result = await reservationRef.value?.requestReservation()
      if (!result || result.res_code !== '200') {
        alert(result?.res_msg || '예약에 실패했습니다.')
        return
      }
      reservationId = result.reservation_id
    }

    // ✅ 2단계: 게시글 등록
    const response = await axios.post('/board_api/board', {
      board_category: form.value.category,
      board_title: title,
      board_content: content,
      user_no: userNo?.value ?? null,
      user_name: userName?.value ?? null
    })

    const boardId = response.data.board_id

    // ✅ 3단계: 예약과 게시글 연결
    if (reservationId) {
      const updateRes = await axios.post('/reservation_api/reservation/updateBoard', {
        reservation_id: reservationId,
        board_id: boardId
      })

      if (updateRes.data.res_code !== '200') {
        alert('예약과 게시글 연결에 실패했습니다.')
        return
      }
    }

    // ✅ 4단계: 파일 업로드
    if (fileUploader.value) {
      await fileUploader.value.uploadAllFiles(boardId)
    }

    alert('게시글이 등록되었습니다.')
    router.push('/board/boardlist')
  } catch (error) {
    console.error('등록 실패:', error)
    alert('등록에 실패했습니다.')
  }
}

onMounted(() => {
  if (!userNo?.value || !userName?.value) {
    alert('로그인이 필요한 페이지입니다.')
    router.push('/board/boardlist')
  }
})
</script>
