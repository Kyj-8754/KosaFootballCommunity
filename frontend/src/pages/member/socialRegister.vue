<template>
  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <div class="container d-flex justify-content-center align-items-center min-vh-100">
          <div class="card shadow p-4 rounded w-100" style="max-width: 600px;">
            <h1 class="text-center mb-4">회원가입</h1>
            <form @submit.prevent="onSubmit">
              <div class="mb-3">
                <label for="name" class="form-label">이름</label>
                <input type="text" v-model="form.userName" id="name" class="form-control" readonly>
              </div>

              <div class="mb-3">
                <label for="birth" class="form-label">생년월일</label>
                <input type="date" v-model="form.userBirth" id="birth" class="form-control" required>
              </div>

              <div class="mb-3">
                <label class="form-label">성별</label>
                <select v-model="form.userGender" class="form-select" required>
                  <option value="" disabled>성별을 선택해주세요</option>
                  <option value="M">남성</option>
                  <option value="F">여성</option>
                </select>
              </div>

              <div class="mb-3">
                <label for="phone" class="form-label">전화번호</label>
                <input type="text" v-model="form.userPhone" id="phone" class="form-control" placeholder="전화번호를 입력해주세요" maxlength="16">
              </div>

              <div class="mb-3 d-flex gap-2">
                <div class="flex-grow-1">
                  <label for="postcode" class="form-label">우편번호</label>
                  <input type="text" v-model="form.userPostcode" id="postcode" class="form-control" placeholder="우편번호를 입력해주세요" readonly>
                </div>
                <div class="mt-4">
                  <input type="button" value="우편찾기" class="btn btn-outline-secondary" @click="findZipcode">
                </div>
              </div>

              <div class="mb-3">
                <label for="address" class="form-label">주소</label>
                <input type="text" v-model="form.userAddr" id="address" class="form-control" placeholder="주소를 입력해주세요" readonly>
              </div>

              <div class="mb-4">
                <label for="detail_address" class="form-label">상세주소</label>
                <input type="text" v-model="form.userDetailAddr" id="detail_address" class="form-control" placeholder="상세주소를 입력해주세요">
              </div>

              <div class="d-flex justify-content-between">
                <input type="submit" value="회원가입" class="btn btn-primary">
                <input type="reset" value="초기화" class="btn btn-secondary" @click="onReset">
              </div>
            </form>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { useSocialRegister } from '@/utils/script/user.js'
const { form, onSubmit, onReset, findZipcode } = useSocialRegister()
</script>

<style scoped>
.container-fluid.main-container {
  padding-top: 40px;
  padding-bottom: 60px;
  font-family: 'Pretendard', sans-serif;
}

.card {
  background-color: transparent !important;
  border: none !important;
  box-shadow: none !important;
  padding: 32px;
  max-width: 600px;
  width: 100%;
}

h1 {
  font-size: 24px;
  font-weight: 700;
  color: #212529;
  margin-bottom: 28px;
  text-align: center;
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

textarea.form-control {
  resize: none;
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

.btn-secondary {
  background-color: #6c757d;
  border: none;
  color: white;
}

.btn-outline-secondary {
  background-color: transparent;
  border: none;
  color: #6c757d;
  font-weight: 500;
}

.btn-outline-secondary:hover {
  background-color: rgba(108, 117, 125, 0.1);
}

.text-danger,
.text-success {
  font-size: 13px;
  margin-top: 4px;
}
</style>
