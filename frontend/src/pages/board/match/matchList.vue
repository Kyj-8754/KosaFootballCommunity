<!-- matchList.vue -->
<template>
  <div class="match-list">
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

const fetchMatches = async (params = {}) => {
  const res = await axios.get('/board_api/match/social', { params })
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
</style>