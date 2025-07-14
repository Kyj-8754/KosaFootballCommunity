import { createApp } from 'vue';
import App from './App.vue';
import 'bootstrap/dist/css/bootstrap.css';
import router from './router/index.js';
import './main.css';
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import VCalendar from 'v-calendar'
import 'v-calendar/style.css'

// ✅ Toastification import
import Toast from 'vue-toastification';
import 'vue-toastification/dist/index.css';  // 스타일도 반드시 import

// main.js 상단 or axios 설정 파일
import axios from 'axios'

axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken') // inject 대신 이거!
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, (error) => {
  return Promise.reject(error)
})

const app = createApp(App);
const pinia = createPinia();

pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);

// ✅ Toast 등록
app.use(Toast);

app.mount('#app');
app.use(VCalendar, {})