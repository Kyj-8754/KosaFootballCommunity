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
<style>
.logincontainer {
  max-width: 400px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}

.login-form h2 {
  font-size: 1.8rem;
  color: #212529;
  text-align: center;
  margin-bottom: 2rem;
}

.login-form .form-control {
  padding: 10px 12px;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: box-shadow 0.2s ease-in-out;
}
.login-form .form-control:focus {
  box-shadow: 0 0 0 2px rgba(13, 110, 253, 0.2);
}

.login-form > div:last-child {
  margin-top: 0.8rem;
  text-align: center;
  font-size: 0.9rem;
}
.login-form a {
  color: #6c757d;
  font-weight: 500;
}
.login-form a:hover {
  text-decoration: underline;
}

.social-login .btn {
  margin-bottom: 0.5rem;
  padding: 10px 14px;
  font-weight: 600;
  border: none;
  font-size: 0.95rem;
}

.social-login .btn-danger {
  background-color: #db4437;
  color: white;
}
.social-login .btn-danger:hover {
  background-color: #c23321;
}

.social-login .btn-success {
  background-color: #03c75a;
  color: white;
}
.social-login .btn-success:hover {
  background-color: #02b24f;
}

.social-login .btn-warning {
  background-color: #fee500;
  color: #3c1e1e;
}
.social-login .btn-warning:hover {
  background-color: #ffeb3b;
}

</style>