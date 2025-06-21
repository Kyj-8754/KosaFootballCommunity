<template>
  <div class="container py-5">
    <h2 class="fw-bold mb-4 text-center">클럽 상세 정보</h2>

    <div v-if="club">
      <div class="row mb-4">
        <!-- 좌측 상단: 이미지 등록 (고정 크기) -->
        <div class="col-md-3">
          <div class="border rounded p-3 text-center" style="height: 250px;">
            <img
              :src="club.logo_path || 'https://via.placeholder.com/100'"
              alt="클럽 로고"
              class="img-fluid mb-3"
              style="max-height: 100px; object-fit: contain;"
            />
            <button class="btn btn-outline-secondary btn-sm w-100">이미지 등록</button>
          </div>
        </div>

        <!-- 우측 4개의 동일 박스 -->
        <div class="col-md-9">
          <div class="row row-cols-2 g-3">
            <!-- 박스 1: 경기 전적 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>경기 전적</strong>
                <p class="mb-1">참가수: {{ getTotalGames(club) }}회</p>
                <p class="mb-1">승 / 무 / 패: {{ club.win_count }} / {{ club.draw_count }} / {{ club.loss_count }}</p>
                <p class="mb-0">승률: {{ calcWinRate(club) }}%</p>
              </div>
            </div>

            <!-- 박스 2: 주요 멤버 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>주요 멤버 리스트</strong>
                <ul class="list-unstyled mt-2 mb-0">
                  <li>멤버 A</li>
                  <li>멤버 B</li>
                  <li>멤버 C</li>
                </ul>
              </div>
            </div>

            <!-- 박스 3: 간단한 팀 정보 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>간단한 팀 정보</strong>
                <p class="mb-1"><strong>팀명:</strong> {{ club.club_name }}</p>
                <p class="mb-0"><strong>팀 코드:</strong> {{ club.team_code }}</p>
              </div>
            </div>

            <!-- 박스 4: 팀 소개 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>팀 소개</strong>
                <p class="mb-0">{{ club.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 팀장만 수정 버튼 -->
      <div v-if="isLeader" class="text-end mt-3">
        <button @click="goToEdit" class="btn btn-primary">수정하기</button>
      </div>
    </div>

    <p v-else class="text-muted mt-5 text-center">클럽 정보를 불러오는 중입니다...</p>
  </div>
</template>


<script>
import axios from 'axios';

export default {
  name: 'ClubDetailView',
  data() {
    return {
      club: null,
      isLeader: false,
    };
  },
  async created() {
    const teamCode = this.$route.params.teamCode;
    try {
      const response = await axios.get(`/api/clubs/code/${teamCode}`);
      this.club = response.data;

      const loginUserid = sessionStorage.getItem('loginUserid');
      this.isLeader = loginUserid === this.club.leader_user_id;
    } catch (error) {
      console.error('클럽 정보 불러오기 실패:', error);
      alert('클럽 정보를 불러오는 데 실패했습니다.');
    }
  },
  methods: {
    getTotalGames(club) {
      return (club.win_count || 0) + (club.draw_count || 0) + (club.loss_count || 0);
    },
    calcWinRate(club) {
      const total = this.getTotalGames(club);
      return total === 0 ? 0 : Math.round((club.win_count / total) * 100);
    },
    goToEdit() {
      this.$router.push(`/club/${this.club.team_code}/edit`);
    },
  },
};
</script>
