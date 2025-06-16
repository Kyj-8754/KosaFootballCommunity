<template>
  <div class="error-container">
    <h1 class="error-code">404</h1>
    <p class="error-message">페이지를 찾을 수 없습니다.</p>
    <button @click="goHome" class="home-button">홈으로 돌아가기</button>
  </div>
</template>

<script>
export default {
  name: 'ErrorPage',
  methods: {
    goHome() {
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
.error-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f8fafc;
  color: #334155;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  text-align: center;
  padding: 2rem;
}

.error-code {
  font-size: 6rem;
  font-weight: bold;
  margin: 0;
  color: #ef4444;
}

.error-message {
  font-size: 1.5rem;
  margin: 1rem 0;
}

.home-button {
  background-color: #3b82f6;
  color: white;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.home-button:hover {
  background-color: #2563eb;
}
</style>