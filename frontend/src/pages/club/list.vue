<template>
  <div class="container my-4">
    <h2 class="fw-bold mb-3">클럽 순위</h2>
    <ul class="list-group">
      <li
        v-for="(club, index) in sortedClubs"
        :key="club.club_id"
        class="list-group-item d-flex justify-content-between align-items-center"
      >
        <!-- 왼쪽: 순위 + 팀명 + 등급(나중에 구현) -->
        <div class="d-flex align-items-center">
          <span class="me-2 fw-bold">{{ index + 1 }}위</span>
          <router-link :to="`/club/${club.team_code}`" class="text-decoration-none text-dark fw-bold">
            {{ club.club_name }}
          </router-link>
          <!-- ⚠️ 클럽 레벨 등급(백엔드에서 계산 후 값 내려줄 때 표시) -->
          <!-- 나중에 등급 시스템 완성되면 아래 부분 활성화 -->
          <!-- <span class="badge bg-warning ms-2">{{ club.club_level }}</span> -->
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
        // 🔧 수정 전: /api/clubs
        // ✅ 수정 후: /club_api
        const response = await axios.get('/club_api/list')
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
