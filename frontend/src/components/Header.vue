<template>
  <div>
    <div v-if="isAuthenticated" class="auth-actions">
      <span>
        <router-link :to="{ name: 'Member_MyPage' }">{{ userName }} 내 정보</router-link>
        <template v-if="isAdmin">
          | <router-link :to="{ name: 'Admin_UserList' }" class="admin-link"> 회원 목록 </router-link>
        </template> |
        <a href="#" @click.prevent="logout">로그아웃</a>
        <AlarmDropdown :userNo="userNo" class="alarm" />

        
      </span>
    </div>

    <!-- 로그인 전 -->
    <div v-else class="auth-actions">
      <router-link :to="{ name: 'Member_LoginForm' }">로그인</router-link> | 
      <router-link :to="{ name: 'Member_RegistForm' }">회원 가입</router-link>
    </div>


    <!-- 임시 버튼 -->
    <div class="my-2 px-2">
      <button class="btn btn-warning btn-sm" @click="runWeatherCollector">⛅ 날씨 수집 테스트</button>
    </div>

    <!-- 내비게이션 바 -->
    <nav class="navbar navbar-expand-sm bg-dark navbar-dark">
      <span class="navbar-brand ps-2">TodoList App</span>
      <button class="navbar-toggler" type="button" @click="isNavShow = !isNavShow">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div :class="isNavShow ? 'collapse navbar-collapse show' : 'collapse navbar-collapse'">
        <ul class="navbar-nav">
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Home' }">Home</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'boardList' }">BoardList</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Stadium_List' }">구장</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Reservation_List' }">예약 리스트</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Payment_List' }">결제 리스트</router-link>
          </li>
        </ul>
      </div>
    </nav>

    <!-- 헤더 이미지 -->
    <header class="header border-bottom" style="height: 250px; overflow: hidden;">
      <router-link to="/">
        <img :src="logoImage" alt="사이트 배너" style="width: 100%; height: auto; object-fit: cover;">
      </router-link>
      <img :src="logoImage" alt="사이트 배너" style="width: 100%; height: auto; object-fit: cover; cursor: not-allowed; opacity: 0.6;">
    </header>
  </div>
</template>


<script setup>
import { inject, computed, ref, watch } from 'vue'
import logoImage from '@/assets/image/bannerlogo.jpg'
import axios from 'axios'

// ✅ AlarmDropdown 컴포넌트 import 추가
import AlarmDropdown from '@/components/common/AlarmDropdown.vue'

// token과 logout 함수 inject
const token = inject('token')
const userNo = inject('userNo')
const logout = inject('logout')
const userName = inject('userName')
const authCode = inject('authCode')


watch(authCode, (newVal) => {
  console.log('🔐 authCode 변경됨:', newVal)
})
// 로그인 여부 계산
const isAuthenticated = computed(() => !!token?.value)
const isAdmin = computed(() => authCode?.value === 'ROLE_A1')
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
<style>
.auth-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.75rem;
  padding: 1rem 2rem 0 2rem;
  font-size: 0.95rem;
  color: #212529;
  position: relative;
  z-index: 10;
}

.auth-actions a,
.auth-actions .router-link-active {
  color: #212529;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
  display: inline-flex;
  align-items: center;
}

.auth-actions a:hover {
  color: #0d6efd;
  text-decoration: underline;
}

/* 알림 드롭다운 */
.auth-actions .alarm {
  margin-left: 0.5rem;
}

/* 관리자 전용 링크 강조 */
.auth-actions .admin-link {
  font-weight: 600;
  color: #dc3545;
}

/* 모바일 대응 */
@media (max-width: 576px) {
  .auth-actions {
    flex-direction: column;
    align-items: flex-end;
    gap: 0.4rem;
    padding: 0.5rem 1rem;
  }
}
</style>