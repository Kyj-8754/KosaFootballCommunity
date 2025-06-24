<template>
  <div class="board-list-container">
    <CategoryButtons @select="handleCategorySelect" />
    <SortButtons :sort="sortOptions" @update:sort="updateSort" />
    <BoardFilter @search="handleSearch" />
    <BoardNoticeList @view="handleViewPost" />
    <BoardTable :posts="filteredPosts" :showHeader="false" @view="handleViewPost" />
    <Pagination :currentPage="currentPage" :totalPages="totalPages" @changePage="handlePageChange" />
  </div>
</template>

<script setup>
import CategoryButtons from '@/components/board/boardCategoryButton.vue'
import BoardFilter from '@/components/board/boardFilter.vue'
import BoardNoticeList from '@/components/board/boardNoticeList.vue'
import BoardTable from '@/components/board/boardTable.vue'
import Pagination from '@/components/pagination.vue'
import SortButtons from '@/components/board/boardSortButton.vue'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const posts = ref([])
const currentPage = ref(1)
const postsPerPage = 10

const searchFilters = ref({
  category: '',
  criteria: '',
  keyword: ''
})

const sortOptions = ref({
  column: '',
  direction: ''
})

const updateSort = (newSort) => {
  sortOptions.value = newSort
  currentPage.value = 1
  fetchPosts()
}

// ✅ 카테고리 버튼 클릭 시
const handleCategorySelect = (category) => {
  searchFilters.value.category = category
  currentPage.value = 1
  fetchPosts()
}

const fetchPosts = async () => {
  try {
    const response = await axios.get('/board_api/board/list', {
      params: {
        ...searchFilters.value,
        sortColumn: sortOptions.value.column,
        sortDirection: sortOptions.value.direction
      }
    })
    posts.value = response.data
  } catch (error) {
    console.error('게시글 목록 불러오기 실패:', error)
  }
}

const handleSearch = (filters) => {
  searchFilters.value = {
    ...searchFilters.value, // 기존 카테고리 유지
    ...filters              // 새 검색 조건만 덮어쓰기
  }
  currentPage.value = 1
  fetchPosts()
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const handleViewPost = (postId) => {
  router.push(`/board/boarddetail/${postId}`)
}

const filteredPosts = computed(() => {
  const start = (currentPage.value - 1) * postsPerPage
  return posts.value.slice(start, start + postsPerPage)
})

const totalPages = computed(() =>
  Math.ceil(posts.value.length / postsPerPage)
)

onMounted(fetchPosts)
</script>
