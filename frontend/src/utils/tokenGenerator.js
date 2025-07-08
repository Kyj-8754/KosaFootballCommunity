// tokenGenerator.js
import axios from 'axios'

let setTokenCallback = null

// 외부에서 token 상태 동기화를 위해 setToken 주입
export const injectSetToken = (fn) => {
  setTokenCallback = fn
}

const instance = axios.create({
  baseURL: '/',
  withCredentials: true,
})

// 요청 시 Authorization 헤더에 accessToken 추가
instance.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => Promise.reject(error))

// 응답 에러 처리
instance.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config
    console.log("응답 상태:", error.response?.status)
    console.log("응답 메시지:", error.response?.data)
    const isExpiredToken =
      error.response?.status === 403 &&
      error.response?.data?.msg === 'Expired Token'

    if (isExpiredToken && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const res = await instance.post('/refreshToken', {
          accessToken: localStorage.getItem('accessToken'),
          refreshToken: localStorage.getItem('refreshToken'),
        })

        const { accessToken, refreshToken } = res.data
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', refreshToken)

        // 전역 token 동기화
        if (setTokenCallback) setTokenCallback(accessToken)

        // 헤더에 새 accessToken 설정 후 재요청
        originalRequest.headers.Authorization = `Bearer ${accessToken}`
        return instance(originalRequest)
      } catch (refreshError) {
        console.error('🔒 토큰 재발급 실패: 로그아웃 처리')
        localStorage.clear()
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }

    // 만료 이외의 다른 에러는 그대로 전파
    return Promise.reject(error)
  }
)

export default instance
