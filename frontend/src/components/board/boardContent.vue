<template>
  <div class="post-content" v-html="post.board_content"></div>
</template>

<script setup>
import { onMounted, watch } from 'vue'

const props = defineProps({
  post: Object
})

// 게시글 내용이 바뀔 때마다 이미지 크기 조정
const resizeImages = () => {
  const container = document.querySelector('.post-content')
  if (!container) return

  const images = container.querySelectorAll('img')
  images.forEach(img => {
    // 조건부로 너비 조정 (예: 원본 너비가 600px 이상인 경우)
    if (img.naturalWidth > 600 || img.width > 600) {
      img.style.width = '100%'
      img.style.height = 'auto'
    }
  })
}

onMounted(() => {
  // 최초 마운트 시 이미지 크기 조정
  setTimeout(resizeImages, 100)
})

watch(() => props.post?.board_content, () => {
  // 게시글 내용이 바뀔 때 이미지 조정
  setTimeout(resizeImages, 100)
})
</script>

<style scoped>
.post-content {
  padding: 1rem;
  border: 1px solid #ccc;
  min-height: 300px;
  line-height: 1.6;
  overflow-x: auto;
}
.post-content img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 1rem 0;
}
</style>
