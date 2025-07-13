<template>
  <div class="container my-4">
    <h2 class="fw-bold mb-3">클럽 순위</h2>

    <!-- 버튼 우측 정렬 -->
    <div class="mb-3 text-end" style="margin-top: -25px">
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

    <ul class="list-group">
      <li
        v-for="(club, index) in sortedClubs"
        :key="club.club_id"
        class="list-group-item d-flex justify-content-between align-items-center py-3 px-4 fs-5"
        style="min-height: 64px"
        @click="goToClub(club.team_code)"
      >
        <!-- 왼쪽: 메달 + 순위 + 팀명 -->
        <div class="d-flex align-items-center">
          <span v-if="index === 0" class="me-2">🥇</span>
          <span v-else-if="index === 1" class="me-2">🥈</span>
          <span v-else-if="index === 2" class="me-2">🥉</span>
          <span v-else class="me-2 fw-bold">{{ index + 1 }}위</span>

          <router-link
            :to="`/club/${club.team_code}`"
            class="text-decoration-none text-dark fw-bold"
          >
            {{ club.club_name }}
          </router-link>
        </div>

        <!-- 오른쪽: 레벨 + 참가 + 승률 (가로 정렬) -->
        <div class="d-flex align-items-center gap-3">
          <span class="badge bg-primary">
            {{
              calculateClubLevel(
                club.win_count || 0,
                club.draw_count || 0,
                club.loss_count || 0
              )
            }}
          </span>
          <span class="text-muted small">참가: {{ getTotalGames(club) }}</span>
          <span class="text-muted small">승률: {{ calcWinRate(club) }}%</span>
        </div>
      </li>
    </ul>
    <p v-if="clubs.length === 0" class="mt-3">클럽 데이터가 없습니다.</p>
  </div>
</template>

<script>
import axios from "axios";
import { useRoute } from "vue-router";
const route = useRoute();

export default {
  name: "ClubList",
  data() {
    return {
      clubs: [],
    };
  },
  created() {
    this.fetchClubs();
  },
  computed: {
    // ⚠️ sortedClubs 정렬 기준에 club_level 적용 예시(아직 백엔드에서 계산값 미반영 상태)
    sortedClubs() {
      // TODO: 추후 club_level이 백엔드에서 계산되면 아래 등급 우선 정렬 적용
      // const levelOrder = { '다이아': 5, '플래티넘': 4, '골드': 3, '실버': 2, '브론즈': 1 }
      // return [...this.clubs]
      //   .map(club => ({
      //     ...club,
      //     win_rate: this.getWinRateRaw(club),
      //     club_level_order: levelOrder[club.club_level] || 0
      //   }))
      //   .sort((a, b) => {
      //     if (b.club_level_order !== a.club_level_order) {
      //       return b.club_level_order - a.club_level_order
      //     }
      //     return b.win_rate - a.win_rate
      //   })

      // 현재는 승률 기준 정렬만 적용
      return [...this.clubs]
        .map((club) => ({
          ...club,
          win_rate: this.getWinRateRaw(club),
        }))
        .sort((a, b) => b.win_rate - a.win_rate);
    },
  },
  methods: {
    isActiveTab(path) {
      return this.$route.path.startsWith(path);
    },

    async fetchClubs() {
      try {
        const response = await axios.get("/club_api/list");
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
        (club.win_count || 0) + (club.draw_count || 0) + (club.loss_count || 0)
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
</style>
