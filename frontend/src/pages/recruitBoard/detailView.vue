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

      <!-- ✅ 가입 신청 버튼 -->
      <button
        class="btn btn-outline-primary btn-sm me-2"
        :disabled="isApplied"
        @click="applyToClub"
      >
        {{ isApplied ? '가입 신청 완료' : '가입 신청' }}
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
      isApplied: false,
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
        alert('❌ 모집글 불러오기 실패');
        console.error('모집글 조회 실패', e);
      }
    },
    formatDate(dateTime) {
      if (!dateTime || typeof dateTime !== 'string') return '';
      return dateTime.split(' ')[0].split('T')[0];
    },
    async applyToClub() {
      const bno = this.recruit?.bno;
      if (!bno) {
        alert('모집글 정보가 없습니다.');
        return;
      }

      try {
        const payload = {
          bno: bno
          // ✅ applicant_id는 서버 세션에서 처리하므로 제거
        };

        await axios.post('/api/club/apply', payload, {
          withCredentials: true  // ✅ 세션 사용 시 필수
        });

        this.isApplied = true;
        alert('✅ 가입 신청이 완료되었습니다.');
      } catch (e) {
        console.error('가입 신청 실패:', e);
        alert('❌ 가입 신청 중 오류가 발생했습니다.');
      }
    }
  }
};
</script>
