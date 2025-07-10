<template>
  <div class="board-list-container">
    <!-- 소트와 필터를 같은 줄에 배치 -->
    <div class="sort-filter-row">
      <BoardFilter @search="handleSearch" />
      <SortButtons :sort="sortOptions" @update:sort="updateSort" />
    </div>
    <CategoryButtons @select="handleCategorySelect" />
    <BoardNoticeList @view="handleViewPost" />
    <BoardTable :posts="filteredPosts" :showHeader="false" @view="handleViewPost" />
    <div style="text-align: right; margin-top: 1.5rem;">
      <button
        @click="router.push('/board/boardregisterform')"
        style="
          background-color: #007bff;
          color: white;
          border: none;
          padding: 0.5rem 1rem;
          border-radius: 8px;
          font-size: 0.95rem;
          cursor: pointer;
        "
      >
        글쓰기
      </button>
    </div>
    <Pagination :currentPage="currentPage" :totalPages="totalPages" @changePage="handlePageChange" />
    <weatherWidget/>
  </div>
</template>

<script setup>
import CategoryButtons from '@/components/board/boardCategoryButton.vue'
import BoardFilter from '@/components/board/boardFilter.vue'
import BoardNoticeList from '@/components/board/boardNoticeList.vue'
import BoardTable from '@/components/board/boardTable.vue'
import Pagination from '@/components/pagination.vue'
import SortButtons from '@/components/board/boardSortButton.vue'
import { ref, computed, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const authCode = inject('authCode')

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

<style scoped>
.sort-filter-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0;
}
</style>