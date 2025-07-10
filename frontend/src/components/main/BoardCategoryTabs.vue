<template>
  <div class="board-tabs-container">
    <!-- 카테고리 탭 -->
    <div class="tab-buttons">
      <button
        v-for="cat in categories"
        :key="cat"
        :class="{ active: cat === selectedCategory }"
        @click="selectCategory(cat)"
      >
        {{ cat }}
      </button>
    </div>

    <!-- 게시글 테이블 -->
    <BoardTable
      :posts="latestPosts"
      :showHeader="true"
      @view="handleViewPost"
    />

    <!-- 전체 글 보기 -->
    <div class="view-more" @click="router.push('/board/boardlist')">
      → 전체 글 보기
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import BoardTable from '@/components/board/boardTable.vue'

const router = useRouter()

// 탭으로 사용할 카테고리들
const categories = ['공지사항', '자유게시판', '모집게시판']
const selectedCategory = ref(categories[0])
const latestPosts = ref([])

const fetchLatestPosts = async (category) => {
  try {
    const res = await axios.get('/board_api/board/list', {
      params: {
        category,
        sortColumn: 'created_at',
        sortDirection: 'desc'
      }
    })
    latestPosts.value = res.data.slice(0, 5)
  } catch (e) {
    console.error('최신 게시글 불러오기 실패:', e)
    latestPosts.value = []
  }
}

const selectCategory = (category) => {
  selectedCategory.value = category
}

// 선택된 카테고리가 바뀌면 최신 글 5개 로드
watch(selectedCategory, (newCategory) => {
  fetchLatestPosts(newCategory)
})

onMounted(() => {
  fetchLatestPosts(selectedCategory.value)
})

const handleViewPost = (postId) => {
  router.push(`/board/boarddetail/${postId}`)
}
</script>

<style scoped>
.board-tabs-container {
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 1rem;
  background-color: #fff;
}

.tab-buttons {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.tab-buttons button {
  padding: 0.5rem 1rem;
  border: 1px solid #ccc;
  border-radius: 20px;
  background-color: #f9f9f9;
  cursor: pointer;
  font-size: 0.95rem;
}

.tab-buttons button.active {
  background-color: #007bff;
  color: #fff;
  border-color: #007bff;
}

.view-more {
  margin-top: 1rem;
  text-align: right;
  font-size: 0.9rem;
  color: #007bff;
  cursor: pointer;
}
</style>
