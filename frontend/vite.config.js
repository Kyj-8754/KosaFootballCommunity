import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      // club_api (포트 8081)
      '/api/clubs': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      // '/api/apply': {
      //   target: 'http://localhost:8082',
      //   changeOrigin: true,
      // },

      // recruit_api (포트 8082)
      '/api/recruits': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      }
    },
  },
})
