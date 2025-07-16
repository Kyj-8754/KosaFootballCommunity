<template>
  <div class="container my-5" style="max-width: 900px">
    <h2 class="fw-bold mb-3">모집글 수정</h2>
    <form @submit.prevent="submitEdit">
      <!-- 소속 클럽(수정 불가/읽기전용) -->
      <div class="mb-3">
        <label class="form-label">소속 클럽</label>
        <input class="form-control" :value="clubName" readonly tabindex="-1" />
      </div>

      <!-- 제목 입력 -->
      <div class="mb-3">
        <label class="form-label">제목</label>
        <input
          v-model="form.title"
          class="form-control"
          required
          maxlength="100"
        />
      </div>

      <!-- 내용 입력 (boardEditer.vue 사용) -->
      <div class="mb-3">
        <label class="form-label">내용</label>
        <div class="quill-editor">
          <!-- ⚡ 팀원 컴포넌트에 맞춰 import -->
          <BoardEditer v-model="form.content" />
        </div>
      </div>

      <button class="btn btn-primary">수정</button>
      <router-link to="/recruitBoard" class="btn btn-secondary ms-2"
        >취소</router-link
      >
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from "vue";
import { useRoute, useRouter } from "vue-router";
import BoardEditer from "@/components/board/boardEditer.vue"; // 팀원 파일 경로!

import axios from "axios";

const route = useRoute();
const router = useRouter();
const bno = route.params.bno;

const userNo = inject("userNo");
const token = inject("token");

const form = ref({
  club_id: "",
  title: "",
  content: "",
  user_no: "",
});
const clubName = ref(""); // 클럽명 표시용

const fetchRecruit = async () => {
  if (!userNo?.value) {
    alert("로그인이 필요합니다.");
    router.replace("/member/loginForm");
    return;
  }
  try {
    const response = await axios.get(`/recruit_api/recruits/${bno}`);
    const data = response.data;

    if (userNo.value !== data.user_no) {
      alert("팀장만 수정할 수 있습니다.");
      router.replace("/recruitBoard");
      return;
    }
    form.value = {
      club_id: data.club_id,
      title: data.title,
      content: data.content,
      user_no: data.user_no,
    };
    clubName.value = data.club_name || "";
  } catch (err) {
    alert("모집글을 불러오는 데 실패했습니다.");
    console.error(err);
    router.replace("/recruitBoard");
  }
};

const submitEdit = async () => {
  const title = form.value.title.trim();
  const content = form.value.content?.trim() ?? "";

  if (!title || !content) {
    alert("모든 항목을 입력해주세요.");
    return;
  }
  if (title.length > 100) {
    alert("제목은 100자 이하로 입력해주세요.");
    return;
  }
  const contentByteLength = new Blob([content]).size;
  if (contentByteLength > 16_777_215) {
    alert("내용이 너무 깁니다. 최대 16MB까지 입력할 수 있습니다.");
    return;
  }
  try {
await axios.put(
  `/recruit_api/recruits/${bno}?user_no=${userNo.value}`,
  {
    club_id: form.value.club_id,
    title: title,
    content: content,
    user_no: form.value.user_no
  }
)

    alert("모집글이 수정되었습니다.");
    router.push(`/recruitBoard/${bno}?from=edit`);
  } catch (err) {
    if (err.response && err.response.status === 403) {
      alert("팀장만 수정할 수 있습니다.");
      router.replace("/recruitBoard");
    } else {
      alert("수정에 실패했습니다.");
    }
    console.error(err);
  }
};

onMounted(fetchRecruit);
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
