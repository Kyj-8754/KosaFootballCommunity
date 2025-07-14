<template>
  <form @submit.prevent="onSubmit" style="max-width: 400px; margin: auto;">
    <h2>회원 탈퇴</h2>
    <div class="form-group mb-3">
      <label for="password">비밀번호를 입력하세요</label>
      <input v-model="password" id="password" type="password" required class="form-control" />
    </div>
    <button type="submit" class="btn btn-danger w-100">회원 탈퇴</button>
  </form>
</template>

<script setup>
import { useDeleteMember } from '@/utils/script/user'
const { password, onSubmit } = useDeleteMember()
</script>
<style>
form {
  max-width: 400px;
  margin: 3rem auto 0 auto;
  padding: 2rem 1.5rem;
  background-color: transparent;
  border: none;
  box-shadow: none;
}


form h2 {
  text-align: center;
  font-size: 1.8rem;
  font-weight: 700;
  color: #212529;
  margin-bottom: 2rem;
}

.form-group label {
  font-weight: 600;
  color: #212529;
  font-size: 0.95rem;
  margin-bottom: 6px;
  display: block;
}

.form-control {
  padding: 10px 12px;
  font-size: 0.95rem;
  border: 1px solid #ced4da;
  border-radius: 6px;
  transition: border-color 0.3s, box-shadow 0.3s;
}
.form-control:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 2px rgba(13, 110, 253, 0.25);
}

.btn-danger {
  font-size: 1rem;
  font-weight: 600;
  padding: 10px 0;
  border-radius: 6px;
  transition: background-color 0.2s ease-in-out;
}

.btn-danger:hover {
  background-color: #bb2d3b;
}
</style>