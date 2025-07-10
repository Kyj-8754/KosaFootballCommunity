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

      // 게시판 프록시
      "/board_api": {
        target: "http://localhost:8131",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/board_api/, ""),
      },
      // 위젯 프록시
      "/widget_api": {
        target: "http://localhost:8130",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/widget_api/, ""),
      },
      "/stadium_api": {
        target: "http://localhost:8100",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/stadium_api/, ""),
      },
      "/reservation_api": {
        target: "http://localhost:8101",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/reservation_api/, ""),
      },
      "/kakao_api": {
        target: "http://localhost:8102",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/kakao_api/, ""),
      },
      "/login_api": {
        target: "http://localhost:8111",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/login_api/, ""),
      },

      "/security_api": {
      target: "http://localhost:8110",
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/security_api/, ""),
    },

      
      // club_api
      '/club_api': {
        target: 'http://localhost:8121',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/club_api/, "/club"),
      },
      '/club_info': {
        target: 'http://localhost:8121',
        changeOrigin: true,
      },

      '/recruits_api': {
        target: 'http://localhost:8122',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/recruits_api/, "/recruits"),
      },

      '/alarm_api': {
        target: 'http://localhost:8120',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/alarm_api/, "/alarm"),
      },

      '/ws': {
        target: 'http://localhost:8120',
        ws: true,
        changeOrigin: true
      }
    }
  }
});
