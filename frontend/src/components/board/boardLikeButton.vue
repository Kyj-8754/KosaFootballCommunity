<!-- src/components/board/LikeButton.vue -->
<template>
  <div class="like-button-wrapper">
    <button @click="$emit('toggle-like')" class="like-btn">
      {{ liked ? '추천' : '추천' }} ({{ likeCount }})
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  liked: Boolean,
  likeCount: Number
})
</script>

<style scoped>
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
