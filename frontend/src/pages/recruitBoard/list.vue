<template>
  <div class="container my-5" style="max-width: 900px;">
    <h2 class="fw-bold mb-3">모집 게시판</h2>

    <!-- 등록 버튼 + 정렬 버튼 + 팀명 검색창 -->
    <div class="mb-3 d-flex justify-content-between align-items-center">
      <router-link to="/recruitBoard/new" class="btn btn-primary btn-sm">등록하기</router-link>

      <div class="d-flex align-items-center">
        <input
          type="text"
          v-model="searchTeam"
          placeholder="팀명 검색"
          class="form-control form-control-sm me-2"
          style="width: 150px;"
        />
        <button class="btn btn-outline-primary btn-sm me-2" @click="fetchRecruits()">최신순</button>
        <button class="btn btn-outline-secondary btn-sm" @click="fetchRecruits('popular')">인기순</button>
      </div>
    </div>

    <!-- 모집글 목록 -->
    <div v-if="filteredRecruits.length === 0" class="alert alert-warning">등록된 모집글이 없습니다.</div>
    <ul v-else class="list-group">
      <router-link
        v-for="recruit in filteredRecruits"
        :key="recruit.bno"
        :to="`/recruitBoard/${recruit.bno}`"
        tag="li"
        class="list-group-item d-flex justify-content-between align-items-start text-dark text-decoration-none"
        style="cursor: pointer;"
      >
        <!-- 팀명 -->
        <div class="fw-bold me-3" style="width: 30%;">
          {{ recruit.club_name }}
        </div>

        <!-- 모집글 제목 및 기타 정보 -->
        <div class="flex-grow-1">
          <div class="fw-bold">{{ recruit.title }}</div>
          <div class="small text-muted mt-1">
            조회수: {{ recruit.view_count }} |
            등록일: {{ formatDate(recruit.reg_date) }}
          </div>
        </div>
      </router-link>
    </ul>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'RecruitList',
  data() {
    return {
      recruits: [],
      searchTeam: ''
    };
  },
  computed: {
    filteredRecruits() {
      const keyword = this.searchTeam.toLowerCase();
      return this.recruits.filter(recruit =>
        (recruit.club_name || '').toLowerCase().includes(keyword)
      );
    }
  },
  created() {
    this.fetchRecruits();
  },
  methods: {
  async fetchRecruits(sortType = '') {
    try {
      const url = sortType === 'popular' ? '/recruits_api?sort=popular' : '/recruits_api';  // ✅ 정상 경로
      const response = await axios.get(url);
      this.recruits = response.data;
    } catch (e) {
      alert('모집글을 불러오는 데 실패했습니다.');
      console.error(e);
    }
  },

  formatDate(dateTime) {
    if (!dateTime || typeof dateTime !== 'string') return '';

    // 공백 또는 'T' 기준으로 앞부분(날짜)만 추출
    return dateTime.split(' ')[0].split('T')[0];
  },  
}

}
</script>
