<template>
  <div class="match-detail">
    <!-- ✅ 매치 데이터 있을 경우 -->
    <template v-if="match">
      <!-- 제목 및 시간 -->
      <MatchTitleBar :match="match" />

      <!-- 메인 정보 카드 -->
      <MatchInfoCard :match="match" />

      <!-- 상세 설명 -->
      <MatchDescription
        :description="match.match_description"
        :matchId="match.match_id"
        :matchCode="match.match_code"
        :matchUserNo="match.user_no"
        :matchManagerNo="match.manager_no"
        :matchStatus="match.match_status"
      />

      <MatchLogButton 
        :match-id="match.match_id"
        :manager-no="match.manager_no" />
    </template>

    <!-- ❌ 매치 데이터 없을 경우 -->
    <template v-else>
      <div class="text-center text-danger py-5">
        <p>존재하지 않는 매치입니다.</p>
        <button
          class="btn btn-outline-secondary mt-3"
          @click="$router.push({ name: 'matchList' })"
        >
          매치 목록으로 이동
        </button>
      </div>
    </template>
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
import MatchLogButton from '@/components/board/match/matchLogPageButton.vue'

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
})
</script>

<style scoped>
.match-detail {
  padding: 20px;
}
</style>
