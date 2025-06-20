<template>
	<div class="text-end bg-light py-2 px-4">
        <router-link :to="{ name: 'Member_LoginForm' }">로그인</router-link> /
        <router-link :to="{ name: 'Member_RegistForm' }"> 회원 가입</router-link> 
	</div>
  <nav class="navbar navbar-expand-sm bg-dark navbar-dark">
      <span class="navbar-brand ps-2">TodoList App</span>
      <button class="navbar-toggler" type="button" @click="isNavShow = !isNavShow">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div :class="isNavShow ? 'collapse navbar-collapse show' : 'collapse navbar-collapse'">
        <ul class="navbar-nav">
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Home' }">
                Home
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Board_List' }">
                BoardList
            </router-link>
          </li>
           <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Stadium_List' }">
                구장
            </router-link>
          </li>
        </ul>
      </div>
    </nav>
	<!-- 헤더 -->
	<header class="header border-bottom"
		style="height: 250px; overflow: hidden;">
    <router-link to="/">
     <img :src="logoImage" alt="사이트 배너" style="width: 100%; height: auto; object-fit: cover;"> 
    </router-link>
	</header>
</template>

<script setup>
import logoImage from '@/assets/image/bannerlogo.jpg';
</script>