import { createApp } from 'vue'
import App from './App.vue'
import 'bootstrap/dist/css/bootstrap.css'
import router from './router/index.js'
import './main.css';
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import VCalendar from 'v-calendar'
import 'v-calendar/style.css'

const app = createApp(App)
const pinia = createPinia()

pinia.use(piniaPluginPersistedstate) 
app.use(pinia);
app.use(router);
app.use(VCalendar, {})
app.mount('#app')