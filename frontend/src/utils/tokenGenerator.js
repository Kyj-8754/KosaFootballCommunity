// tokenGenerator.js
import axios from 'axios'

let setTokenCallback = null
export const injectSetToken = (fn) => {
  setTokenCallback = fn
}

const instance = axios.create({
  baseURL: '/',
  withCredentials: true,
})

instance.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => Promise.reject(error))

instance.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const res = await instance.post('/refreshToken', {
          accessToken: localStorage.getItem('accessToken'),
          refreshToken: localStorage.getItem('refreshToken'),
        })

        const { accessToken, refreshToken } = res.data
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', refreshToken)

        // 외부에서 setToken 주입된 경우 동기화
        if (setTokenCallback) setTokenCallback(accessToken)

        originalRequest.headers.Authorization = `Bearer ${accessToken}`
        return instance(originalRequest)
      } catch (refreshError) {
        console.error('토큰 재발급 실패: 로그아웃 처리')
        localStorage.clear()
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  }
)

export default instance
