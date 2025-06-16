<template>
	<div class="text-end bg-light py-2 px-4">
    <template v-if="!memberStore.authenticated">
        <router-link :to="{ name: 'Member_LoginForm' }">로그인</router-link> /
        <router-link :to="{ name: 'Member_RegistForm' }"> 회원 가입</router-link> 
    </template>
		<template v-else>
      <span class="me-3">
        <router-link :to="{ name: 'Member_DetailView', query: {userid : memberStore.userid} }">{{memberStore.userid}}</router-link>님
      </span>
      <router-link :to="{}" @click.prevent="logout">로그아웃</router-link>
      남은 시간: {{ remainingMinutes }}분 {{ remainingSeconds }}초
		</template>
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
            <template v-if="memberStore.supervisor === 'Y'">
              <router-link class="nav-link":to="{ name: 'Member_List' }">
                MemberList
              </router-link>
            </template>
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
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { useMemberStore } from '@/stores/member'
import { logoutProcess } from '@/util/AuthUtil'
import logoImage from '@/assets/image/bannerlogo.jpg';
import { useRouter } from 'vue-router'

// 보내는 용도 라우터
const router = useRouter()

// pinia를 이용한 상태 관리
const memberStore = useMemberStore();

// 로그아웃 
const logout = () => {
  logoutProcess(() => {
    router.push({ name: 'Home' });
  });
}

// 남은 시간 (초 단위로 관리)
const TIMEOUT_SECONDS = 600
const remainingTime = ref(TIMEOUT_SECONDS)

// 타이머 ID 저장용
let countdownTimer = null


// 키입력, 마우스 버튼클릭, 스크린터치감지
let activityEvents = ['mousemove', 'keydown', 'mousedown', 'touchstart'] 

// 타이머 초기화 함수
const resetTimer = () => {
  remainingTime.value = TIMEOUT_SECONDS
}

// 타이머 시작 함수
const startTimer = () => {
  countdownTimer = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      clearInterval(countdownTimer)
      removeActivityListeners()
      alert("10분동안 움직임이 없어서 자동 로그아웃합니다.")
      logout()
    }
  }, 1000)
}

// 이벤트 리스너 등록 및 제거
const activityListener = () => resetTimer()

// 로그인 되면 이벤트 시작하는 함수
const addActivityListeners = () => {
  activityEvents.forEach(event =>
    window.addEventListener(event, activityListener)
  )
}

// 로그아웃 시 이벤트 제거하는 함수
const removeActivityListeners = () => {
  activityEvents.forEach(event =>
    window.removeEventListener(event, activityListener)
  )
}

// 시작
onMounted(() => {
  addActivityListeners()
  startTimer()
})

// 끝
onBeforeUnmount(() => {
  clearInterval(countdownTimer)
  removeActivityListeners()
})

// 화면 출력용 함수
const remainingMinutes = computed(() => Math.floor(remainingTime.value / 60))
const remainingSeconds = computed(() => remainingTime.value % 60)
</script>