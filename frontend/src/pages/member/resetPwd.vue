<template>
  <form @submit.prevent="onSubmit" style="max-width: 400px; margin: auto;">
    <h2>비밀번호 재설정</h2>
    <p>안전한 비밀번호로 내 정보를 보호하세요</p>
    <p style="color: red;">
      · 다른 아이디/사이트에서 사용한 적 없는 비밀번호<br />
      · 이전에 사용한 적 없는 비밀번호가 안전합니다.
    </p>

    <div class="form-group mb-3">
      <label>새 비밀번호</label>
      <input type="password" v-model="form.newPassword" required class="form-control" />
      <span v-if="passwdMsg.value" class="text-danger">{{ passwdMsg.value }}</span>
    </div>

    <div class="form-group mb-3">
      <label>새 비밀번호 확인</label>
      <input type="password" v-model="form.confirmNewPassword" required class="form-control" />
      <span v-if="passwdMsg" class="text-danger">{{ passwdMsg }}</span>
    </div>

    <div class="form-group mt-4 d-flex gap-2">
      <input type="submit" value="확인" class="btn btn-success" />
      <input type="button" value="취소" class="btn btn-secondary" @click="onCancel" />
    </div>
  </form>
</template>
<script setup>
import { useResetPassword } from '@/utils/script/user.js'
const { form, passwdMsg, onSubmit, onCancel } = useResetPassword()
</script>