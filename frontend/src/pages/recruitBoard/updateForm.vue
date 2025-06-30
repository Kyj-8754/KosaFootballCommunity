<template>
  <div class="container my-5" style="max-width: 900px;">
    <h2 class="fw-bold mb-3">모집글 수정</h2>

    <form @submit.prevent="submitForm">
      <!-- 제목 -->
      <div class="mb-3">
        <label class="form-label">제목</label>
        <input v-model="form.title" class="form-control" required />
      </div>

      <!-- 내용 -->
      <div class="mb-3">
        <label class="form-label">내용</label>
        <textarea v-model="form.content" class="form-control" rows="6" required></textarea>
      </div>

      <!-- 작성자 (숨김 처리) -->
      <input type="hidden" v-model="form.writer" /> <!-- ✅ 수정 불가하게 숨김 처리 -->

      <!-- 클럽 ID (읽기 전용 처리) -->
      <input type="hidden" v-model="form.club_id" /> <!-- ✅ 수정 방지 -->

      <!-- 버튼 영역 -->
      <button class="btn btn-primary">수정</button>
      <router-link to="/recruitBoard" class="btn btn-secondary ms-2">취소</router-link>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'RecruitUpdateForm',
  data() {
    return {
      form: {
        title: '',
        content: '',
        writer: '',
        club_id: ''
      }
    };
  },
  async created() {
    const bno = this.$route.params.bno;
    try {
      const response = await axios.get(`/recruits_api/${bno}`); // ✅ 프록시 경로 적용
      this.form = response.data;
    } catch (e) {
      alert('모집글을 불러오는 데 실패했습니다.');
      console.error(e);
    }
  },
  methods: {
    async submitForm() {
      const bno = this.$route.params.bno;
      const loginUserid = sessionStorage.getItem('loginUserid');
      if (!loginUserid) {
        alert('로그인이 필요합니다.');
        return;
      }

      const user_id = loginUserid;

      try {
        await axios.put(`/recruits_api/${bno}?user_id=${user_id}`, this.form); // ✅ 프록시 경로 적용
        alert('수정이 완료되었습니다.');
        this.$router.push(`/recruitBoard/${bno}`);
      } catch (e) {
        if (e.response && e.response.status === 403) {
          alert('팀장만 수정할 수 있습니다.');
        } else {
          alert('수정에 실패했습니다.');
        }
        console.error(e);
      }
    }
  }
};
</script>
