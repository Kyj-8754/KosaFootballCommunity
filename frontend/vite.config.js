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
      // 게시판 프록시
      "/api/board": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      // 게시판 프록시
      "/api/reply": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      // 첨부파일 프록시
      "/api/file": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
    
  },
})
