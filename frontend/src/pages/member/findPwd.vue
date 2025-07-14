<template>
  <h1 class="text-center mb-4">비밀번호 찾기</h1>
  <form @submit.prevent="onSubmit" class="find-form">
    <div class="mb-3">
      <label for="userId" class="form-label">아이디</label>
      <input
        type="text"
        v-model="form.userId"
        id="userId"
        class="form-control"
        placeholder="아이디를 입력해주세요"
        required
      />
    </div>

    <div class="mb-3">
      <label for="name" class="form-label">이름</label>
      <input
        type="text"
        v-model="form.userName"
        id="name"
        class="form-control"
        placeholder="이름을 입력해주세요"
        required
      />
    </div>

    <div class="mb-3">
      <label for="phone" class="form-label">전화번호</label>
      <div class="d-flex gap-2">
        <input
          type="text"
          v-model="form.userPhone"
          id="phone"
          class="form-control"
          placeholder="전화번호를 입력해주세요"
          maxlength="11"/>
        <input
          type="button"
          value="인증 요청"
          class="btn btn-outline-primary"
          @click="sendSmsCode"/>
      </div>
    </div>

    <!-- 인증 코드 입력 -->
    <div class="mb-3" v-if="isCodeSent">
      <label for="smsCode" class="form-label">인증 코드</label>
      <div class="d-flex gap-2">
        <input
          type="text"
          v-model="smsCode"
          id="smsCode"
          class="form-control"
          placeholder="인증 코드를 입력해주세요"/>
        <input
          type="button"
          value="확인"
          class="btn btn-outline-success"
          @click="verifySmsCode"/>
      </div>
      <span
        v-if="smsCodeMsg"
        :class="smsCodeValid ? 'text-success' : 'text-danger'">
        {{ smsCodeMsg }}
      </span>
    </div>
    <div class="d-grid">
      <button type="submit" class="btn btn-primary">비밀번호 찾기</button>
    </div>
  </form>
</template>

<script setup>
import { useFindPwdForm } from '@/utils/script/user'

const {
  form,
  smsCode,
  smsCodeMsg,
  smsCodeValid,
  isCodeSent,
  sendSmsCode,
  verifySmsCode,
  onSubmit
} = useFindPwdForm()
</script>

<style scoped>
.find-form {
  max-width: 400px;
  margin: 3rem auto 0 auto; /* 상단 여백 확보 */
  padding: 2rem 1.5rem;
}

h1.text-center {
  font-size: 1.8rem;
  font-weight: 700;
  color: #212529;
  text-align: center;
  margin-bottom: 2rem;
  margin-top: 1rem;
}

.find-form .form-label {
  font-weight: 600;
  color: #212529;
  font-size: 0.95rem;
}

.find-form .form-control {
  padding: 10px 12px;
  font-size: 0.95rem;
  border: 1px solid #ced4da;
  border-radius: 6px;
  transition: border-color 0.3s, box-shadow 0.3s;
}
.find-form .form-control:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 2px rgba(13, 110, 253, 0.25);
}

.find-form input[type="button"],
.find-form button[type="submit"] {
  white-space: nowrap;
  font-size: 0.9rem;
  padding: 10px 14px;
  border-radius: 6px;
  font-weight: 600;
  transition: all 0.2s ease-in-out;
}

.find-form input[type="button"]:hover,
.find-form button[type="submit"]:hover {
  opacity: 0.9;
}

.find-form .btn-outline-primary {
  border-color: #0d6efd;
  color: #0d6efd;
}
.find-form .btn-outline-primary:hover {
  background-color: #0d6efd;
  color: white;
}

.find-form .btn-outline-success {
  border-color: #198754;
  color: #198754;
}
.find-form .btn-outline-success:hover {
  background-color: #198754;
  color: white;
}

.find-form span.text-success,
.find-form span.text-danger {
  display: block;
  margin-top: 6px;
  font-size: 0.9rem;
}

.find-form .btn-primary {
  width: 100%;
  font-size: 1rem;
}

@media (max-width: 480px) {
  .d-flex.gap-2 {
    flex-direction: column;
    gap: 0.5rem !important;
  }
}
</style>