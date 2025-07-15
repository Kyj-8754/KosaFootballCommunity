<template>
  <div class="ranking-wrapper">
    <h5 class="fw-bold mb-3">클럽 순위</h5>

    <ul class="list-group">
      <li
        v-for="(club, index) in top5Clubs"
        :key="club.club_id"
        class="list-group-item d-flex justify-content-between align-items-center py-3 px-4 fs-5"
        style="min-height: 64px"
        @click="goToClub(club.team_code)"
      >
        <div class="d-flex align-items-center">
          <span v-if="index === 0" class="me-2">🥇</span>
          <span v-else-if="index === 1" class="me-2">🥈</span>
          <span v-else-if="index === 2" class="me-2">🥉</span>
          <span v-else class="me-2 fw-bold">{{ index + 1 }}위</span>

          <img
            :src="club.logo_path ? `http://localhost:8121${club.logo_path}` : fallbackImg"
            @error="handleImageError"
            alt="클럽 로고"
            style="width: 40px; height: 40px; object-fit: cover; border-radius: 6px; margin: 0 12px 0 8px"
          />

          <router-link
            :to="`/club/${club.team_code}`"
            class="text-decoration-none text-dark fw-bold"
          >
            {{ club.club_name }}
          </router-link>
        </div>

        <div class="d-flex align-items-center club-meta">
          <span class="badge bg-primary level">
            {{ calculateClubLevel(club.win_count || 0, club.draw_count || 0, club.loss_count || 0) }}
          </span>
          <span class="text-muted small game-count">참가: {{ getTotalGames(club) }}</span>
          <span class="text-muted small win-rate">승률: {{ calcWinRate(club) }}%</span>
        </div>
      </li>
    </ul>

    <div class="mt-2 text-end">
      <router-link :to="{ name: 'Club_List' }" class="text-primary small text-decoration-none">
        → 전체 클럽 순위 보기
      </router-link>
    </div>

    <p v-if="clubs.length === 0" class="mt-3">검색 결과가 없습니다.</p>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ClubRanking",
  data() {
    return {
      clubs: [],
      fallbackImg: "https://via.placeholder.com/40",
    };
  },
  created() {
    this.fetchClubs();
  },
  computed: {
    sortedClubs() {
      return [...this.clubs]
        .map((club) => ({
          ...club,
          win_rate: this.getWinRateRaw(club),
        }))
        .sort((a, b) => b.win_rate - a.win_rate);
    },
    top5Clubs() {
      return this.sortedClubs.slice(0, 5);
    },
  },
  methods: {
    async fetchClubs() {
      try {
        const res = await axios.get("/club_api/list");
        this.clubs = res.data.data;
      } catch (err) {
        console.error("클럽 데이터를 불러오는 중 오류 발생:", err);
      }
    },
    handleImageError(e) {
      e.target.src = this.fallbackImg;
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
      return (club.win_count || 0) + (club.draw_count || 0) + (club.loss_count || 0);
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
.ranking-wrapper {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
  background-color: white;
}

.club-meta {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  min-width: 220px;
  margin-right: -20px;
  transform: translateX(-30px);
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