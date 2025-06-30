// src/stores/userStore.js
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    user_no: null,        // DB 컬럼명: 유저 고유번호(PK)
    user_name: '',        // DB 컬럼명: 유저 이름(또는 닉네임)
    jwt: ''               // 인증/인가용 JWT 토큰
  }),
  actions: {
    // 로그인 성공 시 정보 저장
    setUser(info) {
      this.user_no = info.user_no
      this.user_name = info.user_name
      this.jwt = info.jwt
    },
    // 로그아웃/만료 시 정보 초기화
    clearUser() {
      this.user_no = null
      this.user_name = ''
      this.jwt = ''
    }
  },
  persist: true  // 새로고침해도 상태 유지 (localStorage 자동 저장)
})
