<template>
  <div class="container my-5" style="max-width: 900px;">
    <h2 class="fw-bold mb-3">모집글 수정</h2>

    <form @submit.prevent="submitForm">
      <div class="mb-3">
        <label class="form-label">제목</label>
        <input v-model="form.title" class="form-control" required />
      </div>

      <div class="mb-3">
        <label class="form-label">내용</label>
        <textarea v-model="form.content" class="form-control" rows="6" required></textarea>
      </div>

      <div class="mb-3">
        <label class="form-label">작성자</label>
        <input v-model="form.writer" class="form-control" required />
      </div>

      <div class="mb-3">
        <label class="form-label">클럽 ID</label>
        <input v-model="form.club_id" type="number" class="form-control" required />
      </div>

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
      const response = await axios.get(`/api/recruits/${bno}`);
      this.form = response.data;
    } catch (e) {
      alert('모집글을 불러오는 데 실패했습니다.');
      console.error(e);
    }
  },
  methods: {
    async submitForm() {
      const bno = this.$route.params.bno;
      const user_id = this.form.writer; // 실제로는 로그인된 사용자에서 가져오는 것이 적절

      try {
        await axios.put(`/api/recruits/${bno}?user_id=${user_id}`, this.form);
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
