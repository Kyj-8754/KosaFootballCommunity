<template>
  <div class="container py-5">
    <h2 class="fw-bold mb-4 text-center">클럽 정보 수정</h2>

    <div v-if="club">
      <div class="row g-4">
        <!-- 이미지 미리보기 -->
        <div class="col-md-3">
          <div class="border rounded p-3 text-center h-100">
            <img
              :src="club.logo_path || 'https://via.placeholder.com/100'"
              alt="클럽 로고"
              class="img-fluid mb-2"
              style="max-height: 100px; object-fit: contain;"
            />
            <small class="text-muted">이미지 변경은 별도 화면에서</small>
          </div>
        </div>

        <!-- 폼 4칸 -->
        <div class="col-md-9">
          <div class="row row-cols-2 g-3">
            <!-- 경기 전적 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>경기 전적</strong>
                <input type="number" :value="club.win_count" class="form-control my-1" placeholder="승" readonly />
                <input type="number" :value="club.draw_count" class="form-control my-1" placeholder="무" readonly />
                <input type="number" :value="club.loss_count" class="form-control my-1" placeholder="패" readonly />
              </div>
            </div>

            <!-- 주요 멤버 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>주요 멤버 리스트</strong>
                <ul class="list-unstyled mt-2 text-muted">
                  <li>비편집 영역입니다</li>
                </ul>
              </div>
            </div>

            <!-- 간단한 팀 정보 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>간단한 팀 정보</strong>
                <textarea
                  v-model="club.simple_info"
                  class="form-control"
                  rows="4"
                  placeholder="간단한 클럽 정보를 입력하세요"
                ></textarea>
              </div>
            </div>

            <!-- 팀 소개 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>팀 소개</strong>
                <textarea
                  v-model="club.description"
                  class="form-control"
                  rows="4"
                  placeholder="클럽 설명을 입력하세요"
                ></textarea>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 저장/취소 버튼 -->
      <div class="text-end mt-4" v-if="isLeader">
        <button @click="submitUpdate" class="btn btn-success me-2">저장하기</button>
        <button @click="cancelUpdate" class="btn btn-outline-secondary">취소하기</button>
      </div>
    </div>

    <p v-else class="text-muted mt-5 text-center">클럽 정보를 불러오는 중입니다...</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ClubUpdateForm',
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
    async submitUpdate() {
      try {
        await axios.put(`/api/clubs/${this.club.club_id}`, this.club);
        alert('수정이 완료되었습니다.');
        this.$router.push(`/club/${this.club.team_code}`);
      } catch (error) {
        console.error('수정 실패:', error);
        alert('수정 중 오류가 발생했습니다.');
      }
    },
    cancelUpdate() {
      this.$router.push(`/club/${this.club.team_code}`);
    }
  }
};
</script>
