<script setup>
import { useRoute, useRouter } from 'vue-router'
import { inject } from 'vue'

const route = useRoute()
const router = useRouter()

// token 갱신 함수 inject
const setToken = inject('setToken')

const accessToken = route.query.accessToken
const refreshToken = route.query.refreshToken

if (accessToken && refreshToken) {
  // localStorage 저장 + App.vue의 token 상태 갱신
  setToken(accessToken)
  localStorage.setItem('refreshToken', refreshToken)

  router.push('/')
} else {
  alert('토큰이 유효하지 않습니다.')
  router.push('/member/loginForm')
}
</script>