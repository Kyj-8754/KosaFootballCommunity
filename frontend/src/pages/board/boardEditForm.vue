<template>
  <div class="board-edit-form">
    <!-- 제목/카테고리 -->
    <BoardHeaderForm :form="form" @submit="submitEdit" mode="edit" />

    <!-- 모집게시판 탭 표시 -->
    <div v-if="form.category === '모집게시판'" class="mb-3">
      <ul class="nav nav-tabs">
        <li class="nav-item">
          <button class="nav-link" :class="{ active: activeTab === 'content' }" @click="activeTab = 'content'">
            ✍️ 내용 수정
          </button>
        </li>
        <li class="nav-item">
          <button class="nav-link" :class="{ active: activeTab === 'reservation' }" @click="activeTab = 'reservation'">
            🏟️ 예약 정보
          </button>
        </li>
      </ul>
    </div>

    <!-- 탭 콘텐츠 -->
    <div v-if="form.category === '모집게시판'">
      <div v-show="activeTab === 'content'">
        <QuillEditor v-model="form.content" />
        <FileUpload ref="fileRef" :initial-files="initialFiles" />
      </div>
      <div v-show="activeTab === 'reservation'">
        <Reservation />
      </div>
    </div>

    <!-- 일반 게시판일 경우 -->
    <div v-else>
      <QuillEditor v-model="form.content" />
      <FileUpload ref="fileRef" :initial-files="initialFiles" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

import BoardHeaderForm from '@/components/board/boardRegisterHeader.vue'
import QuillEditor from '@/components/board/boardEditer.vue'
import FileUpload from '@/components/file/FileUpload.vue'
import Reservation from '@/components/board/match/reservation/reservation.vue'

const route = useRoute()
const router = useRouter()
const userNo = inject('userNo')

const postId = route.params.id
const form = ref({
  category: '',
  title: '',
  content: ''
})

const initialFiles = ref([])
const fileRef = ref(null)
const activeTab = ref('content') // ← 탭 상태 추가

const fetchPost = async () => {
  try {
    if (!userNo?.value) {
      alert('로그인이 필요합니다.')
      router.replace('/member/loginForm')
      return
    }

    const response = await axios.get(`/board_api/board/${postId}`)
    const data = response.data

    // ✅ 게시글이 존재하지 않는 경우
    if (!data || Object.keys(data).length === 0) {
      alert('존재하지 않는 게시글입니다.')
      router.replace('/board/boardlist')
      return
    }

    // ✅ 작성자 확인
    if (userNo.value !== data.user_no) {
      alert('작성자만 접근할 수 있습니다.')
      router.replace('/board/boardlist')
      return
    }

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
    router.replace('/board/boardlist')
  }
}

const submitEdit = async () => {
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
    await axios.put(`/board_api/board/${postId}`, {
      board_category: form.value.category,
      board_title: title,
      board_content: content
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