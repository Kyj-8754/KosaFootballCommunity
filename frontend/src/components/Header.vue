<template>
  <div>
    <div v-if="isAuthenticated" class="auth-actions">
      <span>
            <!-- 알림 드롭다운 -->
      <AlarmDropdown
        :userNo="userNo"
        style="display: inline-block; margin-right: 15px"
      />
        <router-link :to="{ name: 'Member_MyPage' }"
          >{{ userName }} 내 정보</router-link
        >
        <template v-if="isAdmin">
          |
          <router-link :to="{ name: 'Admin_UserList' }" class="admin-link">
            회원 목록
          </router-link>
        </template>
        |
        <a href="#" @click.prevent="logout">로그아웃</a>
      </span>
    </div>

    <!-- 로그인 전 -->
    <div v-else class="auth-actions">
      <router-link :to="{ name: 'Member_LoginForm' }">로그인</router-link> |
      <router-link :to="{ name: 'Member_RegistForm' }">회원 가입</router-link>
    </div>

    <!-- 내비게이션 바 -->
    <nav class="navbar navbar-expand-sm bg-dark navbar-dark">
      <router-link class="navbar-brand ps-2" :to="{ name: 'Home' }">ITs FootBall</router-link>
      <button
        class="navbar-toggler"
        type="button"
        @click="isNavShow = !isNavShow"
      >
        <span class="navbar-toggler-icon"></span>
      </button>
      <div
        :class="
          isNavShow
            ? 'collapse navbar-collapse show'
            : 'collapse navbar-collapse'
        "
      >
        <ul class="navbar-nav">
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'boardList' }"
              >게시판</router-link
            >
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'matchList' }"
              >매치 목록</router-link
            >
          </li>
          <li class="nav-item dropdown" @mouseenter="showClubDropdown = true" @mouseleave="showClubDropdown = false">
            <a class="nav-link dropdown-toggle" href="#" role="button">
              클럽
            </a>
            <ul class="dropdown-menu show" v-if="showClubDropdown" style="position: absolute;">
              <li><router-link class="dropdown-item" :to="{ name: 'Club_List' }">클럽 순위</router-link></li>
              <li><router-link class="dropdown-item" :to="{ name: 'Club_MatchSchedule' }">리그 매치 일정</router-link></li>
              <li><router-link class="dropdown-item" :to="{ name: 'Recruit_List' }">클럽 모집 게시판</router-link></li>
            </ul>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{ name: 'Stadium_List' }"
              >구장</router-link
            >
          </li>
          <!-- 예약/결제 드롭다운 -->
          <li
            class="nav-item dropdown"
            @mouseenter="showReserveDropdown = true"
            @mouseleave="showReserveDropdown = false"
          >
            <a class="nav-link dropdown-toggle" href="#" role="button">
              예약/결제
            </a>
            <ul
              class="dropdown-menu show"
              v-if="showReserveDropdown"
              style="position: absolute"
            >
              <li>
                <router-link class="dropdown-item" :to="{ name: 'Reservation_List' }">
                  예약 리스트
                </router-link>
              </li>
              <li>
                <router-link class="dropdown-item" :to="{ name: 'Payment_List' }">
                  결제 리스트
                </router-link>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>

    <!-- 헤더 이미지 -->
    <header
      class="header border-bottom"
      style="height: 250px; overflow: hidden"
    >
      <router-link to="/">
        <img
          :src="logoImage"
          alt="사이트 배너"
          style="width: 100%; height: auto; object-fit: cover"
        />
      </router-link>
    </header>
  </div>
</template>

<script setup>
import { inject, computed, ref, watch } from "vue";
import logoImage from '/public/kosabanner.png'

// ✅ AlarmDropdown 컴포넌트 import 추가
import AlarmDropdown from "@/components/common/AlarmDropdown.vue";

// token과 logout 함수 inject
const token = inject("token");
const userNo = inject("userNo");
const logout = inject("logout");
const userName = inject("userName");
const authCode = inject("authCode");
const showClubDropdown = ref(false);
const showReserveDropdown = ref(false);

// 로그인 여부 계산
const isAuthenticated = computed(() => !!token?.value);
const isAdmin = computed(() => authCode?.value === "ROLE_A1");
const isNavShow = ref(false);

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
