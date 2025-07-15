<template>
  <form @submit.prevent="onSubmit" style="max-width: 400px; margin: auto;">
    <h2>비밀번호 변경</h2>
    <p>안전한 비밀번호로 내 정보를 보호하세요</p>
    <p style="color: red;">
      · 다른 아이디/사이트에서 사용한 적 없는 비밀번호<br>
      · 이전에 사용한 적 없는 비밀번호가 안전합니다.
    </p>

    <div class="form-group mb-3">
      <label>현재 비밀번호</label>
      <input type="password" v-model="form.currentPassword" required class="form-control" />
    </div>

    <div class="form-group mb-3">
      <label>새 비밀번호</label>
      <input type="password" v-model="form.newPassword" required class="form-control" />
      <span v-if="passwdMsg" class="text-danger">{{ passwdMsg }}</span>
    </div>

    <div class="form-group mb-3">
      <label>새 비밀번호 확인</label>
      <input type="password" v-model="form.confirmNewPassword" required class="form-control" />
    </div>

    <div class="form-group mt-4 d-flex gap-2">
      <input type="submit" value="변경" class="btn btn-success" />
      <input type="button" value="취소" class="btn btn-secondary" @click="onCancel" />
    </div>
  </form>
</template>

<script setup>
import { useUpdatePassword } from '@/utils/script/user.js'

const {
  form,
  passwdMsg,
  onSubmit,
  onCancel
} = useUpdatePassword()
</script>

<style>
form {
  max-width: 420px;
  margin: 3rem auto;
  padding: 2rem 1.5rem;
  border-radius: 10px;
  background-color: transparent; /* 흰 배경 제거 */
  box-shadow: none; /* 박스 그림자 제거 */
}

h2 {
  font-size: 1.6rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: #212529;
}

p {
  font-size: 0.95rem;
  margin-bottom: 0.3rem;
  color: #495057;
}

p[style*="color: red"] {
  color: #d63384;
  font-size: 0.9rem;
  margin-top: 0.5rem;
  margin-bottom: 1.5rem;
  line-height: 1.5;
}

.form-group label {
  font-weight: 600;
  color: #343a40;
  margin-bottom: 0.4rem;
  display: block;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  font-size: 1rem;
  border-radius: 6px;
  border: 1px solid #ced4da;
  background-color: transparent;
  transition: border-color 0.3s, background-color 0.3s;
}

.form-control:focus {
  outline: none;
  border-color: #86b7fe;
  background-color: #f8f9fa;
  box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.15);
}

.text-danger {
  font-size: 0.85rem;
  color: #dc3545;
  margin-top: 0.3rem;
  display: block;
}

/* 버튼 영역 정리 */
.form-group.mt-4 {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

input[type="submit"],
input[type="button"] {
  padding: 8px 18px;
  font-size: 0.95rem;
  font-weight: 600;
  border-radius: 6px;
  cursor: pointer;
  border: none;
}

input[type="submit"].btn-success {
  background-color: #198754;
  color: white;
}

input[type="submit"].btn-success:hover {
  background-color: #157347;
}

input[type="button"].btn-secondary {
  background-color: #6c757d;
  color: white;
}

input[type="button"].btn-secondary:hover {
  background-color: #5c636a;
}
</style>
