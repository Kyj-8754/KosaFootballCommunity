<template>
  <div class="post-header">
    <!-- 제목 + 조회수 -->
    <div class="title-row">
      <h2>{{ post.board_title }}</h2>
      <span class="views">조회수: {{ post.board_viewcount }}</span>
    </div>

    <!-- 작성자 / 카테고리 -->
    <div class="meta-row">
      <span>작성자: {{ post.user_name }}</span>
      <span>카테고리: {{ post.board_category }}</span>
    </div>

    <!-- 작성일 / 수정일 -->
    <div class="date-row">
      <span>작성일: {{ formatDate(post.board_created_at) }}</span>
      <span v-if="post.board_modified_at">수정일: {{ formatDate(post.board_modified_at) }}</span>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  post: {
    type: Object,
    required: true
  }
})

function formatDate(dateStr) {
  return dateStr ? dateStr.replace('T', ' ') : ''
}
</script>

<style scoped>
.post-header {
  border-bottom: 1px solid #ddd;
  padding-bottom: 1rem;
  margin-bottom: 1rem;
}
.title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.meta-row,
.date-row {
  display: flex;
  justify-content: flex-start;
  gap: 1rem;
  font-size: 0.9rem;
  color: #555;
}
.attachment {
  margin-top: 8px;
  font-size: 0.9rem;
}
a {
  color: #007acc;
  text-decoration: underline;
}
</style>
