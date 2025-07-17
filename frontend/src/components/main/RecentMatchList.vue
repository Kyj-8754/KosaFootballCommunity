<template>
  <div class="recent-match-container">
    <h5>최근 매치</h5>

    <!-- 소셜/리그 탭 -->
    <div class="match-type-tabs">
      <button
        :class="{ active: selectedType === 'social' }"
        @click="selectType('social')"
      >
        소셜 매치
      </button>
      <button
        :class="{ active: selectedType === 'league' }"
        @click="selectType('league')"
      >
        리그 매치
      </button>
    </div>

    <!-- 매치 리스트 또는 결과 없음 메시지 -->
    <MatchItemList v-if="latestMatches.length > 0" :matches="latestMatches" />
    <div v-else class="no-result">검색 결과가 없습니다</div>

    <!-- 전체 매치로 이동 -->
    <div class="view-more" @click="router.push('/match/matchlist')">
      → 전체 매치 보기
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import MatchItemList from '@/components/board/match/matchItemList.vue'

const router = useRouter()
const latestMatches = ref([])
const selectedType = ref('social')

const fetchRecentMatches = async () => {
  try {
    const res = await axios.get(`/board_api/match/${selectedType.value}`)

    const today = new Date()

    const filtered = res.data
  .sort((a, b) => new Date(a.match_date) - new Date(b.match_date))

    latestMatches.value = filtered.slice(0, 5)
  } catch (err) {
    console.error('최근 매치 불러오기 실패:', err)
    latestMatches.value = []
  }
}

const selectType = (type) => {
  if (selectedType.value !== type) {
    selectedType.value = type
    fetchRecentMatches()
  }
}

onMounted(fetchRecentMatches)
</script>

<style scoped>
.recent-match-container {
  padding: 1rem;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 10px;
}

h5 {
  margin-bottom: 1rem;
  font-size: 1.2rem;
  color: #333;
}

.match-type-tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.match-type-tabs button {
  padding: 6px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: #f2f2f2;
  font-size: 0.9rem;
  cursor: pointer;
}

.match-type-tabs button.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}

.view-more {
  margin-top: 1rem;
  text-align: right;
  font-size: 0.9rem;
  color: #007bff;
  cursor: pointer;
}

.no-result {
  text-align: center;
  color: #999;
  font-size: 0.9rem;
  margin: 1rem 0;
}
</style>