<template>
  <div>
    <h2>클럽 목록</h2>
    <ul>
      <li v-for="club in clubs" :key="club.clubId">
        {{ club.clubId }} - {{ club.clubName }} - {{ club.description }}
      </li>
    </ul>
    <p v-if="clubs.length === 0">클럽 데이터가 없습니다.</p>
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
  methods: {
    async fetchClubs() {
      try {
        const response = await axios.get('/api/clubs')
        console.log('받은 클럽 데이터:', response.data)
        this.clubs = response.data.data // ✅ 여기 수정됨
      } catch (error) {
        console.error('클럽 목록 불러오기 실패:', error)
      }
    }
  }
}
</script>
