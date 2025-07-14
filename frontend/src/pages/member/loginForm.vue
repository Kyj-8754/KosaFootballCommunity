<template>
  <div class="logincontainer">
    <form class="login-form" @submit.prevent="login">
      <h2 class="text-center mb-4 fw-bold">로그인</h2>

      <div class="mb-3">
        <label for="userid" class="form-label">아이디</label>
        <input
          v-model="userId"
          type="text"
          id="userid"
          class="form-control"
          placeholder="아이디를 입력해주세요"
        />
      </div>

      <div class="mb-3">
        <label for="passwd" class="form-label">비밀번호</label>
        <input
          v-model="userPwd"
          type="password"
          id="passwd"
          class="form-control"
          placeholder="비밀번호를 입력해주세요"
        />
      </div>

      <button type="submit" class="btn btn-primary w-100">로그인</button>
      <div>
        <router-link :to="{ name: 'Member_FindId'}" class="text-decoration-none">아이디 찾기 | </router-link>
        <router-link :to="{ name: 'Member_FindPwd'}" class="text-decoration-none">비밀번호 찾기</router-link>
      </div>
    </form>

    <hr class="my-4" />

    <!-- 소셜 로그인 -->
    <div class="social-login">
      <button class="btn btn-danger w-100" @click="loginWith('google')">Google 로그인</button>
      <button class="btn btn-success w-100" @click="loginWith('naver')">Naver 로그인</button>
      <button class="btn btn-warning w-100" @click="loginWith('kakao')">Kakao 로그인</button>
    </div>
  </div>
</template>

<script setup>
import { useLoginForm } from '@/utils/script/user'
const { userId, userPwd, login, loginWith } = useLoginForm()
</script>
