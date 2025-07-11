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
