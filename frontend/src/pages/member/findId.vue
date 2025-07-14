<template>
  <h1 class="text-center mb-4">아이디 찾기</h1>
  <form @submit.prevent="onSubmit" class="find-form">
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
      <button type="submit" class="btn btn-primary">아이디 찾기</button>
    </div>
  </form>
</template>

<script setup>
import { useFindIdForm } from '@/utils/script/user'

const {
  form,
  smsCode,
  smsCodeMsg,
  smsCodeValid,
  isCodeSent,
  sendSmsCode,
  verifySmsCode,
  onSubmit
} = useFindIdForm()
</script>

<style scoped>
.find-form {
  max-width: 400px;
  margin: 0 auto;
}
</style>
