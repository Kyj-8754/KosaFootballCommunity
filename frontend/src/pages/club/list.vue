<template>
  <div class="container my-4">
    <h2 class="fw-bold mb-3">클럽 순위</h2>

    <!-- ✅ 검색창과 버튼을 같은 줄에 배치 -->
    <div class="d-flex justify-content-between align-items-center mb-3">
      <!-- 왼쪽: 검색창 -->
      <input
        type="text"
        v-model="searchKeyword"
        placeholder="클럽 이름 검색"
        class="form-control"
        style="max-width: 200px"
      />

      <!-- 오른쪽: 버튼들 -->
      <div>
        <router-link
          to="/recruitBoard"
          class="tab-btn me-2"
          :class="{ active: isActiveTab('/recruitBoard') }"
        >
          팀원 모집 게시판
        </router-link>
        <router-link
          to="/club/clubMatchSchedule"
          class="tab-btn me-2"
          :class="{ active: isActiveTab('/club/clubMatchSchedule') }"
        >
          리그 일정
        </router-link>
      </div>
    </div>

    <ul class="list-group">
      <li
        v-for="(club, index) in sortedClubs"
        :key="club.club_id"
        class="list-group-item d-flex justify-content-between align-items-center py-3 px-4 fs-5"
        style="min-height: 64px"
        @click="goToClub(club.team_code)"
      >
        <!-- ✅ 왼쪽: 순위 → 로고 → 클럽명 -->
        <div class="d-flex align-items-center">
          <span v-if="index === 0" class="me-2">🥇</span>
          <span v-else-if="index === 1" class="me-2">🥈</span>
          <span v-else-if="index === 2" class="me-2">🥉</span>
          <span v-else class="me-2 fw-bold">{{ index + 1 }}위</span>

          <!-- 클럽 로고 썸네일 -->
          <img
            :src="
              club.logo_path
                ? `/club_api${club.logo_path}`
                : fallbackImg
            "
            @error="handleImageError"
            alt="클럽 로고"
            style="
              width: 40px;
              height: 40px;
              object-fit: cover;
              border-radius: 6px;
              margin-left: 8px;
              margin-right: 12px;
            "
          />

          <!-- 클럽 이름 -->
          <router-link
            :to="`/club/${club.team_code}`"
            class="text-decoration-none text-dark fw-bold"
          >
            {{ club.club_name }}
          </router-link>
        </div>

        <!-- 오른쪽: 레벨 + 참가 + 승률 -->
      <div class="d-flex align-items-center club-meta">
  <span class="badge bg-primary level">
    {{
      calculateClubLevel(
        club.win_count || 0,
        club.draw_count || 0,
        club.loss_count || 0
      )
    }}
  </span>
  <span class="text-muted small game-count">참가: {{ getTotalGames(club) }}</span>
  <span class="text-muted small win-rate">승률: {{ calcWinRate(club) }}%</span>
</div>

      </li>
    </ul>

    <p v-if="clubs.length === 0" class="mt-3">클럽 데이터가 없습니다.</p>
  </div>
</template>
<script>
import axios from "axios";

export default {
  name: "ClubList",
  data() {
    return {
      clubs: [],
      searchKeyword: "",
      fallbackImg:
        'data:image/svg+xml;base64,' +
        btoa(`<svg xmlns="http://www.w3.org/2000/svg" width="40" height="40"><rect width="40" height="40" rx="8" fill="#e0e0e0"/><text x="50%" y="54%" text-anchor="middle" fill="#888" font-size="12" font-family="Arial" dy=".3em">NO LOGO</text></svg>`)
    };
  },
  created() {
    this.fetchClubs();
  },
  computed: {
    sortedClubs() {
      const keyword = this.searchKeyword.toLowerCase();
      return [...this.clubs]
        .filter((club) => club.club_name.toLowerCase().includes(keyword))
        .map((club) => ({
          ...club,
          win_rate: this.getWinRateRaw(club),
        }))
        .sort((a, b) => b.win_rate - a.win_rate);
    },
  },
  methods: {
    // ✅ 이미지 로딩 실패 시 기본 이미지로 대체
    handleImageError(event) {
      event.target.src = this.fallbackImg;
    },
    isActiveTab(path) {
      return this.$route.path.startsWith(path);
    },
    async fetchClubs() {
      try {
        const response = await axios.get("/club_api/club/list");
        this.clubs = response.data.data;
      } catch (error) {
        console.error("클럽 목록 불러오기 실패:", error);
      }
    },
    goToClub(teamCode) {
      this.$router.push(`/club/${teamCode}`);
    },
    getWinRateRaw(club) {
      const w = club.win_count || 0;
      const d = club.draw_count || 0;
      const l = club.loss_count || 0;
      const total = w + d + l;
      return total === 0 ? 0 : w / total;
    },
    calcWinRate(club) {
      return Math.round(this.getWinRateRaw(club) * 100);
    },
    getTotalGames(club) {
      return (
        (club.win_count || 0) +
        (club.draw_count || 0) +
        (club.loss_count || 0)
      );
    },
    calculateClubLevel(win, draw, loss) {
      const total = win + draw + loss;
      if (total === 0) return "브론즈";
      const rate = (win / total) * 100;
      if (rate >= 90) return "다이아";
      if (rate >= 70) return "플래티넘";
      if (rate >= 50) return "골드";
      if (rate >= 30) return "실버";
      return "브론즈";
    },
  },
};
</script>

<style scoped>
.tab-btn {
  padding: 0.375rem 0.75rem;
  border: 1px solid #ced4da; /* 연회색 테두리 */
  border-radius: 0.25rem;
  background-color: white;
  color: black;
  text-decoration: none;
  font-weight: 500;
  transition: none;
}

.tab-btn:hover {
  background-color: white !important;
  color: black !important;
  border-color: #ced4da !important;
  box-shadow: none !important;
}

.tab-btn.active {
  background-color: white !important;
  color: black !important;
  border-color: black !important;
}

.club-meta {
  display: flex;
  justify-content: flex-end; /* 기존 유지 */
  align-items: center;
  gap: 12px;
  min-width: 220px;
  margin-right: -20px; /* ✅ 오른쪽으로 너무 붙었을 경우 줄여줌 */
  transform: translateX(-30px); /* ✅ 전체 묶음을 왼쪽으로 살짝 이동 */
}

.level {
  width: 80px;
  text-align: center;
}

.game-count {
  width: 80px;
  text-align: left;
}

.win-rate {
  width: 90px;
  text-align: left;
}

</style>
