<template>
  <div class="post-content" v-html="post.board_content"></div>
  <div class="like-button-wrapper">
    <button @click="$emit('toggle-like')" class="like-btn">
      {{ liked ? '추천' : '추천' }} ({{ likeCount }})
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  post: Object,
  liked: Boolean,
  likeCount: Number
})
</script>

<style scoped>
.post-content {
  padding: 1rem;
  border: 1px solid #ccc;
  min-height: 300px;
  line-height: 1.6;
}
.post-content img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 1rem 0;
}
.like-button-wrapper {
  margin-top: 1rem;
}
.like-btn {
  padding: 0.5rem 1rem;
  border: none;
  background-color: #007bff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}
.like-btn:hover {
  background-color: #0056b3;
}
</style>
