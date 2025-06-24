import { fileURLToPath, URL } from 'node:url';
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';
import { NodeGlobalsPolyfillPlugin } from '@esbuild-plugins/node-globals-polyfill';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)) // ✅ @ → src 폴더
    }
  },
  optimizeDeps: {
    esbuildOptions: {
      define: {
        global: 'globalThis' // ✅ WebSocket 내부의 global 참조 오류 방지
      },
      plugins: [
        NodeGlobalsPolyfillPlugin({
          buffer: true,
          process: true // ✅ WebSocket에서 process 사용 가능
        })
      ]
    }
  },
  define: {
    'process.env': {} // ✅ process.env 오류 방지
  },
  server: {
    proxy: {
      // 🔵 clubs_api (포트 8080)
      '/clubs_api': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/clubs_api/, ""),
      },
      // 게시판 프록시
      "/board_api": {
        target: "http://localhost:8082",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/board_api/, ""),
      },
      // 위젯 프록시
      "/widget_api": {
        target: "http://localhost:8083",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/widget_api/, ""),
      },
      "/stadium_api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/stadium_api/, ""),
      },
      "/login_api": {
        target: "http://localhost:8081",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/login_api/, ""),
      },
      '/api/club': {
        target: 'http://localhost:8084',
        changeOrigin: true
      },

      // 🟡 recruit_api (포트 8081)
      '/api/recruits': {
        target: 'http://localhost:8085',
        changeOrigin: true
      },

      // 🟢 alarm_api (포트 8082)
      '/api/alarm': {
        target: 'http://localhost:8086',
        changeOrigin: true
      },

      // 선택: WebSocket 서버용 경로 프록시
      '/ws': {
        target: 'http://localhost:8086',
        ws: true,
        changeOrigin: true
      }
    }
  }
});
