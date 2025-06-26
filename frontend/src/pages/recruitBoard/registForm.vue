<template>
  <div class="container my-5" style="max-width: 900px;">
    <h2 class="fw-bold mb-3">모집글 등록</h2>

    <!-- 모집글 등록 폼 -->
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
          />
        </div>
      </div>

      <!-- 등록 버튼 -->
      <button class="btn btn-primary">등록</button>
      <!-- 목록으로 이동 -->
      <router-link to="/recruitBoard" class="btn btn-secondary ms-2">취소</router-link>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import axios from 'axios'

// ✅ 로그인 사용자 정보 주입 (Pinia 또는 provide/inject 방식)
const userNoRef = inject('userNo')     // DB의 유저 기본키 (user_no)
const userIdRef = inject('userId')     // 로그인 아이디 (user_id)
const route = useRoute()
const router = useRouter()

// ✅ club_id는 현재 경로의 query 파라미터에서 받음 (예: ?club_id=3)
const club_id = ref(route.query.club_id || '')

// ✅ 폼 입력값 상태
const title = ref('')
const content = ref('')

// ✅ 컴포넌트 마운트 시 로그인 여부 확인
onMounted(() => {
  if (!userNoRef || !userNoRef.value) {
    alert('로그인 후 이용해 주세요.')
    router.push('/member/loginForm')  // 로그인 페이지로 강제 이동
  }
})

// ✅ 모집글 등록 요청
const submitForm = async () => {
  // 로그인 상태 확인
  if (!userNoRef || !userNoRef.value) {
    alert('로그인 후 이용할 수 있습니다.')
    router.push('/member/loginForm')
    return
  }

  try {
    // ✅ 게시글 등록 요청 (JWT 포함)
await axios.post(`/recruits_api/regist`, {
  title: title.value,
  content: content.value,
  user_no: parseInt(userNoRef.value),
  club_id: club_id.value
}, {
  headers: {
    Authorization: `Bearer ${localStorage.getItem("accessToken")}`
  }
})

    alert('모집글이 성공적으로 등록되었습니다.')
    router.push(`/recruitBoard`)  // 소속 클럽 모집글 목록으로 이동

  } catch (error) {
    console.error('등록 실패', error)
    alert('등록에 실패했습니다. 서버 로그를 확인해주세요.')
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
