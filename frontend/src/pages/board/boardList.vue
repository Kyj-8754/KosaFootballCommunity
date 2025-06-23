<template>
  <div class="board-list-container">
    <BoardFilter @search="handleSearch" />
    <BoardNoticeList @view="handleViewPost" />
    <BoardTable :posts="filteredPosts" :showHeader="false" @view="handleViewPost" />
    <Pagination :currentPage="currentPage" :totalPages="totalPages" @changePage="handlePageChange" />
    <WeatherWidget />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import BoardFilter from '@/components/board/boardFilter.vue'
import BoardNoticeList from '@/components/board/boardNoticeList.vue'
import BoardTable from '@/components/board/boardTable.vue'
import Pagination from '@/components/pagination.vue'

const router = useRouter()

const posts = ref([])
const currentPage = ref(1)
const postsPerPage = 10

const searchFilters = ref({
  category: '',
  criteria: '',
  keyword: ''
})

// 게시글 목록 조회 (백엔드에서 fetch)
const fetchPosts = async () => {
  try {
    const params = { ...searchFilters.value }
    const response = await axios.get('/api/board/list', { params })
    posts.value = response.data
  } catch (error) {
    console.error('게시글 목록 불러오기 실패:', error)
  }
}

// 검색 조건 변경 시 목록 다시 가져오기
const handleSearch = (filters) => {
  searchFilters.value = { ...filters }
  currentPage.value = 1
  fetchPosts()
}

// 페이지 변경 (프론트에서 slice 처리)
const handlePageChange = (page) => {
  currentPage.value = page
}

// 게시글 상세 이동
const handleViewPost = (postId) => {
  router.push(`/board/boarddetail/${postId}`)
}

// 실제 보여질 게시글 목록 계산
const filteredPosts = computed(() => {
  const start = (currentPage.value - 1) * postsPerPage
  return posts.value.slice(start, start + postsPerPage)
})

// 총 페이지 계산
const totalPages = computed(() => {
  return Math.ceil(posts.value.length / postsPerPage)
})

// 초기 로딩 시 게시글 목록 가져오기
onMounted(fetchPosts)
</script>
