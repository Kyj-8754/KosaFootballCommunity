<template>
  <table class="board-table">
    <colgroup>
      <!--<col style="width: 8%" />-->
      <col style="width: 12%" />
      <col style="width: 12%" />
      <col style="width: 28%" />
      <col style="width: 15%" />
      <col style="width: 10%" />
      <col style="width: 10%" />
    </colgroup>
    <thead v-if="showHeader">
      <tr>
        <!--<th>글 번호</th>-->
        <th>카테고리</th>
        <th>작성자</th>
        <th>제목</th>
        <th>등록일</th>
        <th>조회수</th>
        <th>추천수</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="post in posts" :key="post.board_id">
        <!--<td>{{ post.board_id }}</td>-->
        <td>{{ post.board_category }}</td>
        <td>{{ post.user_name }}</td>
        <td @click="viewPost(post.board_id)" style="cursor: pointer; color: blue;">
          {{ post.board_title }}
        </td>
        <td>
          <span>{{ post.board_modified_at ? formatDate(post.board_modified_at) : formatDate(post.board_created_at) }}</span>
        </td>
        <td>{{ post.board_viewcount }}</td>
        <td>{{ post.board_likecount }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
defineProps({
  posts: Array,
  showHeader: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['view'])

const viewPost = (id) => {
  emit('view', id)
}

function formatDate(dateStr) {
  return dateStr ? dateStr.replace('T', ' ') : ''
}
</script>

<style scoped>
.board-table {
  width: 100%;
  border-collapse: collapse;
}

.board-table th,
.board-table td {
  padding: 10px;
  text-align: center;
  border-bottom: 1px solid #ccc; /* 회색 가로줄 */
}

.board-table tbody tr:hover {
  background-color: #f9f9f9; /* 선택 시 강조 효과 (선택사항) */
}

.board-table thead {
  background-color: #f1f1f1; /* 헤더 배경 (선택사항) */
  border-top: 2px solid #888;
}
</style>
