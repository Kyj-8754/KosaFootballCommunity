<template>
  <div class="container my-4">
    <h2 class="fw-bold mb-3">클럽 순위</h2>
    <ul class="list-group">
      <li
        v-for="(club, index) in sortedClubs"
        :key="club.club_id"
        class="list-group-item d-flex justify-content-between align-items-center"
      >
        <!-- 왼쪽: 순위 + 팀명 -->
        <div class="d-flex align-items-center">
          <span class="me-2 fw-bold">{{ index + 1 }}위</span>
          <router-link :to="`/club/${club.team_code}`" class="text-decoration-none text-dark fw-bold">
            {{ club.club_name }}
          </router-link>
        </div>

        <!-- 오른쪽: 참가 수 + 승률 -->
        <div class="text-end small text-muted">
          참가: {{ getTotalGames(club) }}회<br />
          승률: {{ calcWinRate(club) }}%
        </div>
      </li>
    </ul>
    <p v-if="clubs.length === 0" class="mt-3">클럽 데이터가 없습니다.</p>
  </div>
</template>


<script>
import axios from 'axios'

export default {
  name: 'ClubList',
  data() {
    return {
      clubs: []
    }
  },
  created() {
    this.fetchClubs()
  },
  computed: {
    sortedClubs() {
      return [...this.clubs]
        .map(club => ({
          ...club,
          win_rate: this.getWinRateRaw(club)
        }))
        .sort((a, b) => b.win_rate - a.win_rate)
    }
  },
  methods: {
    async fetchClubs() {
      try {
        const response = await axios.get('/api/clubs')
        this.clubs = response.data.data
      } catch (error) {
        console.error('클럽 목록 불러오기 실패:', error)
      }
    },
    getWinRateRaw(club) {
      const w = club.win_count || 0
      const d = club.draw_count || 0
      const l = club.loss_count || 0
      const total = w + d + l
      return total === 0 ? 0 : w / total
    },
    calcWinRate(club) {
      return Math.round(this.getWinRateRaw(club) * 100)
    },
    getTotalGames(club) {
      return (club.win_count || 0) + (club.draw_count || 0) + (club.loss_count || 0)
    }
  }
}
</script>

