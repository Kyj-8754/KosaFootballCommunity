<template>
	<template v-if="isAuthenticated">
    <router-link :to="{ name: 'Member_MyPage', query: { userNo: userNo } }">{{userName}} 내 정보</router-link> /
    <a href="#" @click.prevent="logout">로그아웃</a>
  </template>
  <template v-else>
    <router-link :to="{ name: 'Member_LoginForm' }">로그인</router-link> /
    <router-link :to="{ name: 'Member_RegistForm' }">회원 가입</router-link>
  </template>

    <!-- ✅ 임시 버튼 추가 -->
  <div class="my-2 px-2">
    <button class="btn btn-warning btn-sm" @click="runWeatherCollector">⛅ 날씨 수집 테스트</button>
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
            <router-link class="nav-link" :to="{ name: 'boardList' }">
                BoardList
            </router-link>
          </li>
           <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Stadium_List' }">
                구장
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Reservation_List' }">
                예약 리스트
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Payment_List' }">
                결제 리스트
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
    <img :src="logoImage" alt="사이트 배너" style="width: 100%; height: auto; object-fit: cover; cursor: not-allowed; opacity: 0.6;">
  </header>
</template>

<script setup>
import { inject, computed, ref } from 'vue'
import logoImage from '@/assets/image/bannerlogo.jpg'
import axios from 'axios'

// token과 logout 함수 inject
const token = inject('token')
const userNo = inject('userNo')
const logout = inject('logout')
const userName = inject('userName')

// 로그인 여부 계산
const isAuthenticated = computed(() => !!token?.value)

const isNavShow = ref(false)

const runWeatherCollector = async () => {
  try {
    const res = await axios.get('/widget_api/weather-collect/run')
    alert('✅ 날씨 수집 완료: ' + res.data.result)
  } catch (err) {
    console.error(err)
    alert('❌ 날씨 수집 실패')
  }
}
</script>
