<template>
  <div class="post-action-buttons">
    <button @click="emit('edit')">수정</button>
    <button @click="emit('delete')">삭제</button>
  </div>
</template>

<script setup>
const emit = defineEmits(['edit', 'delete'])
</script>

<style scoped>
.post-action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 1rem;
}

button {
  padding: 6px 12px;
  border: 1px solid #ccc;
  background-color: white;
  cursor: pointer;
}

button:hover {
  background-color: #f0f0f0;
}
</style>
