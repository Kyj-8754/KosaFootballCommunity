<template>
  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <div class="container d-flex justify-content-center align-items-center min-vh-100">
          <div class="card shadow p-4 rounded w-100" style="max-width: 600px;">
            <h1 class="text-center mb-4">회원가입</h1>
            <form @submit.prevent="onSubmit">
              <div class="mb-3 d-flex align-items-center gap-2">
                <div class="flex-grow-1">
                  <label for="userid" class="form-label">아이디</label>
                  <input type="text" v-model="form.userId" id="userid" class="form-control" placeholder="아이디를 입력해주세요" required>
                  <span v-if="useridMsg" class="text-danger">{{ useridMsg }}</span>
                </div>
                <input type="button" value="중복확인" class="btn btn-secondary mt-4" @click="checkDuplicateId">
              </div>
              <div class="mb-3">
                <label for="passwd" class="form-label">비밀번호</label>
                <input type="password" v-model="form.userPwd" id="passwd" class="form-control" placeholder="비밀번호를 입력해주세요" required>
                <span v-if="passwdMsg" class="text-danger">{{ passwdMsg }}</span>
              </div>

              <div class="mb-3">
                <label for="passwd2" class="form-label">비밀번호 확인</label>
                <input type="password" v-model="form.userPwd2" id="passwd2" class="form-control" placeholder="비밀번호를 다시 입력해주세요" required>
              </div>

              <div class="mb-3">
                <label for="name" class="form-label">이름</label>
                <input type="text" v-model="form.userName" id="name" class="form-control" placeholder="이름을 입력해주세요" required>
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
                <div class="d-flex gap-2">
                  <input type="text" v-model="form.userPhone" id="phone" class="form-control" placeholder="전화번호를 입력해주세요" maxlength="11">
                  <input type="button" value="인증 요청" class="btn btn-outline-primary" @click="sendSmsCode">
                </div>
              </div>

              <div class="mb-3" v-if="isCodeSent">
                <label for="smsCode" class="form-label">인증 코드</label>
                <div class="d-flex gap-2">
                  <input type="text" v-model="smsCode" id="smsCode" class="form-control" placeholder="인증 코드를 입력해주세요">
                  <input type="button" value="확인" class="btn btn-outline-success" @click="verifySmsCode">
                </div>
                <span v-if="smsCodeMsg" :class="smsCodeValid ? 'text-success' : 'text-danger'">{{ smsCodeMsg }}</span>
              </div>

              <div class="mb-3 d-flex gap-2">
                <div class="flex-grow-1">
                  <label for="zipcode" class="form-label">우편번호</label>
                  <input type="text" v-model="form.userPostcode" id="zipcode" class="form-control" placeholder="우편번호를 입력해주세요" readonly>
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

              <div class="d-flex justify-content-between mt-4">
                <input type="reset" value="초기화" class="btn btn-outline-secondary" @click="onReset" />
                <div class="d-flex gap-2">
                  <button type="submit" class="btn btn-primary">확인</button>
                  <router-link :to="{ name: 'Home' }" class="btn btn-outline-secondary">취소</router-link>
                </div>
              </div>
            </form>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRegisterForm } from '@/utils/script/user.js'

const {
  form,
  useridMsg,
  passwdMsg,
  smsCode,
  smsCodeMsg,
  smsCodeValid,
  isCodeSent,
  checkDuplicateId,
  sendSmsCode,
  verifySmsCode,
  onSubmit,
  onReset,
  findZipcode
} = useRegisterForm()
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

.text-danger,
.text-success {
  font-size: 13px;
  margin-top: 4px;
}
</style>