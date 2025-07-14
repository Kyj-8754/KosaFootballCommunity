<!-- matchList.vue -->
<template>
  <div class="match-list">
    <!-- 템플릿 최상단 -->
    <div class="match-tab">
      <button
        :class="{ active: matchType === 'social' }"
        @click="changeType('social')"
      >
        소셜 매치
      </button>
      <button
        :class="{ active: matchType === 'league' }"
        @click="changeType('league')"
      >
        리그 매치
      </button>
    </div>
    <MatchQueryBar @query="fetchMatches" />
    <MatchItemList :matches="matches" />
    <Pagination
    :currentPage="currentPage"
    :totalPages="totalPages"
    @changePage="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import MatchQueryBar from '@/components/board/match/matchQueryBar.vue'
import MatchItemList from '@/components/board/match/matchItemList.vue'
import Pagination from '@/components/pagination.vue'

const matchesAll = ref([])
const matches = ref([])
const currentPage = ref(1)
const itemsPerPage = 10
const totalPages = computed(() => Math.ceil(matchesAll.value.length / itemsPerPage))
const matchType = ref('social')  // 기본값은 소셜매치

const fetchMatches = async (params = {}) => {
  const endpoint = `/board_api/match/${matchType.value}`
  const res = await axios.get(endpoint, { params })
  matchesAll.value = res.data
  currentPage.value = 1
  applyPagination()
}

const handlePageChange = (page) => {
  currentPage.value = page
  applyPagination()
}

const applyPagination = () => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  matches.value = matchesAll.value.slice(start, end)
}

const changeType = (type) => {
  if (matchType.value !== type) {
    matchType.value = type
    fetchMatches()
  }
}

onMounted(() => {
  fetchMatches()
})

// ✅ 컴포넌트가 처음 로드될 때 fetch 실행
onMounted(() => {
  fetchMatches()
})
</script>

<style scoped>
.match-list {
  padding: 16px;
}
.match-tab {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}
.match-tab button {
  padding: 8px 16px;
  border: none;
  background: #eee;
  cursor: pointer;
  border-radius: 6px;
}
.match-tab button.active {
  background: #007bff;
  color: white;
}
</style>