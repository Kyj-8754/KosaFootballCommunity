<template>
  <div class="update-container">
    <form @submit.prevent="onSubmit" class="update-form">
      <h3>회원 정보 수정</h3>

      <input type="hidden" v-model="form.userNo" />

      <div class="mb-3">
        <label for="name" class="form-label">이름</label>
        <input type="text" id="name" v-model="form.userName" required class="form-control" />
      </div>

      <div class="mb-3">
        <label for="birth" class="form-label">생년월일</label>
        <input type="date" id="birth" v-model="form.userBirth" required class="form-control" />
      </div>

      <div class="mb-3">
        <label class="form-label">성별</label>
        <select v-model="form.userGender" class="form-select" required>
          <option value="" disabled>성별을 선택해주세요</option>
          <option value="M">남성</option>
          <option value="F">여성</option>
        </select>
      </div>

      <div class="mb-3 d-flex gap-2">
        <div class="flex-grow-1">
          <label for="zipcode" class="form-label">우편번호</label>
          <input type="text" id="zipcode" v-model="form.userPostCode" class="form-control" readonly />
        </div>
        <div class="mt-4">
          <input type="button" @click="handleFindZipcode" value="우편찾기" class="btn btn-outline-secondary" />
        </div>
      </div>

      <div class="mb-3">
        <label for="address" class="form-label">주소</label>
        <input type="text" id="address" v-model="form.userAddr" class="form-control" readonly />
      </div>

      <div class="mb-3">
        <label for="detail_address" class="form-label">상세주소</label>
        <input type="text" id="detail_address" v-model="form.userDetailAddr" class="form-control" />
      </div>

      <div class="link-area">
        <input type="submit" value="변경" class="btn btn-primary" />
        <router-link :to="{ name: 'Member_MyPage' }" class="btn btn-outline-secondary">취소</router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { useUserInfoEdit } from '@/utils/script/user.js'
const { form, onSubmit, handleFindZipcode } = useUserInfoEdit()
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

.d-flex.gap-2 {
  gap: 8px;
}

.btn {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 6px;
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

.link-area {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
