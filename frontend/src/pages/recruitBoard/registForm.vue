<template>
  <div class="container my-5" style="max-width: 900px">
    <h2 class="fw-bold mb-3">모집글 등록</h2>

    <!-- ✅ 모집글 등록 폼 (submit 이벤트 막고 JS 처리) -->
    <form @submit.prevent="submitForm">
      <!-- 제목 입력 -->
      <div class="mb-3">
        <label class="form-label">제목</label>
        <input v-model="title" class="form-control" required />
      </div>

      <!-- 내용 입력 (Quill 에디터 사용) -->
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

      <!-- 목록으로 이동 버튼 -->
      <router-link to="/recruitBoard" class="btn btn-secondary ms-2"
        >취소</router-link
      >
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from "vue";
import { useRouter } from "vue-router";
import { QuillEditor } from "@vueup/vue-quill";
import "@vueup/vue-quill/dist/vue-quill.snow.css";
import axios from "axios";

// ✅ 전역 로그인 정보 (provide/inject 기반으로 받아옴)
const userNoRef = inject("userNo"); // DB에서 사용하는 유저 기본키
const router = useRouter();

// ✅ 입력 폼 데이터 상태 정의
const title = ref(""); // 제목 입력값
const content = ref(""); // 본문 내용 (Quill 사용)
const selectedClubId = ref(""); // 선택된 클럽 ID (드롭다운 v-model 바인딩)

// ✅ 클럽 목록 저장 (드롭다운 옵션으로 활용)
const myClubs = ref([]); // 현재 로그인 사용자의 클럽들 (복수 가능)

// ✅ 마운트 시 클럽 목록 조회
onMounted(async () => {
  // ✅ 로그인 상태 확인
  if (!userNoRef || !userNoRef.value) {
    alert("로그인 후 이용해주세요.");
    router.push("/member/loginForm");
    return;
  }

  try {
    // ✅ user_no를 기반으로 사용자의 클럽 목록 조회
    const res = await axios.get(`/club_api/club/myClubs/${userNoRef.value}`);

    // ✅ 서버에서 클럽 배열을 정상적으로 받아온 경우
    if (Array.isArray(res.data) && res.data.length > 0) {
      myClubs.value = res.data; // 클럽 목록 저장
      selectedClubId.value = res.data[0].club_id; // ✅ 기본으로 첫 번째 클럽 선택
    } else {
      // ❗ 클럽이 하나도 없다면 안내 후 클럽 생성 페이지로 이동
      alert("⚠️ 등록된 클럽이 없습니다. 클럽을 먼저 생성해 주세요.");
      router.push("/club/registForm");
    }
  } catch (error) {
    console.error("❌ 클럽 목록 조회 실패", error);
    alert("⚠️ 클럽 정보를 불러오는 데 실패했습니다.");
  }
});

// ✅ 모집글 등록 함수
const submitForm = async () => {
  // ↓↓↓ [클럽 선택 여부 확인 부분 삭제됨] ↓↓↓

  try {
    // ✅ 서버로 모집글 등록 요청
    await axios.post(
      `/recruit_api/recruits/regist`,
      {
        title: title.value,
        content: content.value,
        user_no: parseInt(userNoRef.value), // 로그인된 사용자 번호
         club_id: selectedClubId.value,   // ← 이 줄 삭제!
      },
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`, // JWT 토큰 포함
        },
      }
    );

    alert("모집글이 성공적으로 등록되었습니다.");
    router.push("/recruitBoard"); // 등록 성공 시 목록으로 이동
  } catch (error) {
    console.error("❌ 등록 실패", error);
    alert("등록에 실패했습니다. 서버 로그를 확인해주세요.");
  }
};

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
