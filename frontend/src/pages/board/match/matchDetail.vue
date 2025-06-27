<!-- views/matchDetail.vue -->
<template>
  <div class="match-detail" v-if="match">
    <!-- 제목 및 시간 -->
    <MatchTitleBar :match="match" />

    <!-- 메인 정보 카드 -->
    <MatchInfoCard :match="match" />

    <!-- 상세 설명 -->
    <MatchDescription
    :notice="match.notice"
    :detail="match.dtlcont"
    :description="match.match_description"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

// 하위 컴포넌트
import MatchTitleBar from '@/components/board/match/matchTitleBar.vue'
import MatchInfoCard from '@/components/board/match/matchInfoCard.vue'
import MatchDescription from '@/components/board/match/matchDescription.vue'

const route = useRoute()
const match = ref(null)

const fetchMatchDetail = async () => {
  try {
    const { id } = route.params
    const res = await axios.get(`/board_api/match/${id}`)
    match.value = res.data
  } catch (err) {
    console.error('매치 상세 조회 실패:', err)
  }
}

onMounted(() => {
  fetchMatchDetail()
  console.log('✅ 받은 match 데이터:', match)
})
</script>

<style scoped>
.match-detail {
  padding: 20px;
}
</style>
