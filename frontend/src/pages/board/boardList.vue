<template>
  <div class="board-list-container">
    <BoardFilter @search="handleSearch" />
    <BoardTable :posts="filteredPosts" @view="handleViewPost" />
    <Pagination :currentPage="currentPage" :totalPages="totalPages" @changePage="handlePageChange" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import BoardFilter from '@/components/board/boardFilter.vue'
import BoardTable from '@/components/board/boardTable.vue'
import Pagination from '@/components/pagination.vue'
import postsData from '@/pages/board/data/board'

const router = useRouter()

const posts = ref([...postsData])

const currentPage = ref(1)
const postsPerPage = 10

const filteredPosts = computed(() => {
  let filtered = [...posts.value]
  const { category, criteria, keyword } = searchFilters.value

  if (category) {
    filtered = filtered.filter(p => p.category === category)
  }

  if (criteria && keyword) {
    if (criteria === '제목') {
      filtered = filtered.filter(p => p.title.includes(keyword))
    } else if (criteria === '작성자') {
      filtered = filtered.filter(p => p.author.includes(keyword))
    }
  }

  const start = (currentPage.value - 1) * postsPerPage
  return filtered.slice(start, start + postsPerPage)
})

const totalPages = computed(() => {
  let filtered = [...posts.value]
  const { category, criteria, keyword } = searchFilters.value

  if (category) {
    filtered = filtered.filter(p => p.category === category)
  }

  if (criteria && keyword) {
    if (criteria === '제목') {
      filtered = filtered.filter(p => p.title.includes(keyword))
    } else if (criteria === '작성자') {
      filtered = filtered.filter(p => p.author.includes(keyword))
    }
  }

  return Math.ceil(filtered.length / postsPerPage)
})


const searchFilters = ref({
  category: '',
  criteria: '',
  keyword: ''
})

const handleSearch = (filters) => {
  searchFilters.value = { ...filters }
  currentPage.value = 1
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const handleViewPost = (postId) => {
  router.push(`/board/boarddetail/${postId}`)
}
</script>
