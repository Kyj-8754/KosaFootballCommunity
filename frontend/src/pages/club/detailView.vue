<template>
  <div>
    <h2>클럽 상세정보</h2>
    <div v-if="club">
      <p><strong>이름:</strong> {{ club.clubName }}</p>
      <p><strong>설명:</strong> {{ club.description }}</p>
      <p><strong>리더 ID:</strong> {{ club.leaderUserId }}</p>
      <p><strong>승/무/패:</strong> {{ club.winCount }} / {{ club.drawCount }} / {{ club.lossCount }}</p>
    </div>
    <p v-else>클럽 정보를 불러오는 중입니다...</p>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ClubDetail',
  data() {
    return {
      club: null,
    }
  },
  async created() {
    const id = this.$route.params.id
    try {
      const response = await axios.get(`/api/clubs/${id}`) // ← 프록시로 자동 우회
      this.club = response.data
    } catch (error) {
      console.error('클럽 정보 불러오기 실패:', error)
    }
  }
}
</script>
