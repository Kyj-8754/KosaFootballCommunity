<template>
  <div class="update-container">
    <form @submit.prevent="onSubmit" class="update-form">
      <h3>회원 프로필 수정</h3>

      <input type="hidden" v-model="form.userNo" />

      <div class="mb-3">
        <label for="name" class="form-label">이름</label>
        <input type="text" id="name" v-model="form.userName" required class="form-control" />
      </div>

      <div class="mb-3">
        <label for="comment" class="form-label">소개</label>
        <textarea id="comment" v-model="form.userComment" class="form-control" rows="3" />
      </div>

      <div class="mb-3">
        <label for="style" class="form-label">좋아하는 스타일</label>
        <select id="style" v-model="form.styleCode" class="form-select">
          <option value="">스타일을 선택해주세요</option>
          <option value="1">공격</option>
          <option value="2">밸런스</option>
          <option value="3">수비</option>
        </select>
      </div>

      <div class="mb-3">
        <label for="stat" class="form-label">자신있는 능력</label>
        <select id="stat" v-model="form.statCode" class="form-select">
          <option value="">능력을 선택해주세요</option>
          <option value="1">슛</option>
          <option value="2">패스</option>
          <option value="3">드리블</option>
          <option value="4">체력</option>
          <option value="5">스피드</option>
          <option value="6">피지컬</option>
        </select>
      </div>

      <div class="link-area">
        <input type="submit" value="변경" class="btn btn-primary" />
        <router-link
          v-if="form.userNo"
          :to="{ name: 'Member_Profile', query: { userNo: form.userNo } }" class="btn btn-outline-secondary">
          취소
        </router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { useProfileUpdate } from '@/utils/script/user.js'
const { form, onSubmit } = useProfileUpdate()
</script>

<style scoped>
.update-container {
  display: flex;
  justify-content: center;
  margin-top: 60px;
  font-family: 'Pretendard', sans-serif;
}

.update-form {
  background-color: transparent;
  border: none;
  box-shadow: none;
  padding: 32px;
  width: 100%;
  max-width: 600px;
}

.update-form h3 {
  font-size: 24px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 30px;
  color: #212529;
}

.form-label {
  font-weight: 600;
  font-size: 15px;
  color: #333;
  margin-bottom: 6px;
  display: block;
}

.form-control,
.form-select {
  font-size: 14px;
  padding: 10px 12px;
  border: none;
  border-bottom: 1px solid #ccc;
  border-radius: 0;
  background-color: transparent;
  box-shadow: none;
  transition: border-color 0.3s ease;
  width: 100%;
  margin-top: 4px;
}
textarea.form-control {
  resize: none;
  height: 120px;
}
.form-control:focus,
.form-select:focus {
  border-color: #007bff;
  outline: none;
  box-shadow: none;
}

.mb-3 {
  margin-bottom: 1rem !important;
}

.link-area {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  text-align: center;
}

.btn-primary {
  background-color: #007bff;
  border: none;
  color: white;
}

.btn-outline-secondary {
  background-color: #f8f9fa;
  border: none;
  color: #6c757d;
  font-weight: 500;
}

.btn-outline-secondary:hover {
  background-color: #e2e6ea;
  color: #212529;
}
</style>
