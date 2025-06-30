<template>
  <div class="container my-5" style="max-width: 900px;">
    <h2 class="fw-bold mb-3">모집 게시판</h2>

    <!-- 등록 버튼 + 정렬 버튼 + 팀명 검색창 -->
    <div class="mb-3 d-flex justify-content-between align-items-center">
      <router-link v-if="hasClub" to="/recruitBoard/new" class="btn btn-primary btn-sm"
      > 등록하기</router-link>


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
        <div class="fw-bold me-3" style="width: 30%; padding-top: 12px;">
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
import axios from 'axios'
import { inject } from 'vue'  // ✅ App.vue에서 제공한 전역값(userNo 등)을 주입받기 위해 사용

export default {
  name: 'RecruitList',

  // ✅ Composition API 방식으로 setup() 내에서 inject() 호출
  setup() {
    const userNo = inject('userNo'); // App.vue에서 provide한 userNo를 받아옴
    return { userNo }; // data나 method 등에서 사용할 수 있게 반환
  },

  data() {
    return {
      recruits: [],       // 모집글 리스트
      searchTeam: '',     // 팀명 검색 키워드
      hasClub: false      // ✅ 로그인한 사용자가 클럽을 보유하고 있는지 여부
    };
  },

  computed: {
    // ✅ 입력된 검색 키워드와 모집글 목록의 club_name을 비교해서 필터링
    filteredRecruits() {
      const keyword = this.searchTeam.toLowerCase();
      return this.recruits.filter(recruit =>
        (recruit.club_name || '').toLowerCase().includes(keyword)
      );
    }
  },

  created() {
    this.fetchRecruits();     // ✅ 컴포넌트가 생성될 때 모집글 리스트 요청
    this.checkHasClub();      // ✅ 로그인한 유저가 클럽이 있는지 여부 확인
  },

  methods: {
    // ✅ 모집글 목록 API 호출 함수
    async fetchRecruits(sortType = '') {
      try {
        // 인기순 정렬이면 쿼리 파라미터 추가, 아니면 기본
        const url = sortType === 'popular' ? '/recruits_api?sort=popular' : '/recruits_api';
        const response = await axios.get(url); // GET 요청
        this.recruits = response.data;         // 결과 저장
      } catch (e) {
        alert('모집글을 불러오는 데 실패했습니다.');
        console.error(e);
      }
    },

    // ✅ 클럽 보유 여부 확인 함수
    async checkHasClub() {
      // 로그인 정보가 없다면 종료
      if (!this.userNo) return;

      try {
        // /club_api/hasClub/{userNo} 엔드포인트로 요청
        const response = await axios.get(`/club_api/hasClub/${this.userNo}`);
        this.hasClub = response.data.result; // 응답의 result 값(true/false)을 저장
      } catch (e) {
        console.error('클럽 조회 실패', e);
        this.hasClub = false; // 오류 발생 시 안전하게 false 처리
      }
    },

    // ✅ 날짜 포맷 함수 (yyyy-MM-dd 형식으로 변환)
    formatDate(dateTime) {
      if (!dateTime || typeof dateTime !== 'string') return '';
      return dateTime.split(' ')[0].split('T')[0]; // 공백 또는 T 기준으로 앞부분만 추출
    }
  }
}
</script>
