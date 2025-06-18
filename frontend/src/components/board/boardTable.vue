<template>
  <table class="board-table">
    <thead>
      <tr>
        <th>카테고리</th>
        <th>작성자</th>
        <th>제목</th>
        <th>등록일/수정일</th>
        <th>조회수</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="post in posts" :key="post.id">
        <td>{{ post.category }}</td>
        <td>{{ post.author }}</td>
        <td @click="viewPost(post.id)" style="cursor: pointer; color: blue;">
          {{ post.title }}
          <span v-if="post.attachment">📎</span>
        </td>
        <td>
          {{ formatDate(post.created_at) }}<br />
          {{ formatDate(post.modified_at) }}
        </td>
        <td>{{ post.views }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
defineProps({
  posts: Array,
})

const emit = defineEmits(['view'])

const viewPost = (id) => {
  emit('view', id)
}

const formatDate = (dateStr) => {
  // yyyy-MM-dd HH:mm:ss → yyyy-MM-dd 로만 보여줌
  return dateStr?.split(' ')[0] || ''
}
</script>