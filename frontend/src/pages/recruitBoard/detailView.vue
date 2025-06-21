<template>
  <div class="container my-5" style="max-width: 900px;">
    <h2 class="fw-bold mb-3"></h2>

    <div v-if="recruit">
      <h4 class="mb-3">{{ recruit.title }}</h4>
      <p>{{ recruit.content }}</p>

      <ul>
        <li><strong>작성자:</strong> {{ recruit.writer }}</li>
        <li><strong>조회수:</strong> {{ recruit.view_count }}</li>
        <li><strong>등록일:</strong> {{ formatDate(recruit.reg_date) }}</li>
      </ul>

      <!-- ✅ 가입 신청/취소 버튼 (서버 없음) -->
      <button class="btn btn-outline-primary btn-sm me-2" @click="toggleApply">
        {{ isApplied ? '가입 신청 취소' : '가입 신청' }}
      </button>

      <router-link to="/recruitBoard" class="btn btn-secondary btn-sm">목록으로</router-link>
    </div>

    <div v-else class="alert alert-warning">해당 모집글이 존재하지 않습니다.</div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'RecruitDetail',
  data() {
    return {
      recruit: null,
      isApplied: false, // ✅ 현재 상태만 Vue에서 관리
    };
  },
  created() {
    this.fetchRecruit();
  },
  methods: {
    async fetchRecruit() {
      const bno = this.$route.params.bno;
      try {
        const response = await axios.get(`/api/recruits/${bno}`);
        this.recruit = response.data;
      } catch (e) {
        alert('모집글을 불러오는 데 실패했습니다.');
        console.error(e);
      }
    },
    formatDate(dateTime) {
      if (!dateTime || typeof dateTime !== 'string') return '';
      return dateTime.split(' ')[0].split('T')[0];
    },
    toggleApply() {
      this.isApplied = !this.isApplied;

      const message = this.isApplied
        ? '가입 신청이 완료되었습니다.'
        : '가입 신청이 취소되었습니다.';

      alert(message);
    }
  }
};
</script>
