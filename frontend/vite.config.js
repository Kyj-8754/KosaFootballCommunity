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
        target: "http://board_api:8131",
        changeOrigin: true,
      },
      // 위젯 프록시
      "/widget_api": {
        target: "http://weather_api:8130",
        changeOrigin: true,
      },
      "/stadium_api": {
        target: "http://stadium_api:8100",
        changeOrigin: true,
      },
      "/reservation_api": {
        target: "http://reservation_api:8101",
        changeOrigin: true,
      },
      "/kakao_api": {
        target: "http://kakaopay_api:8102",
        changeOrigin: true,
      },
      "/login_api": {
        target: "http://login_api:8111",
        changeOrigin: true,
      },
      "/security_api": {
      target: "http://security_api:8110",
      changeOrigin: true,
      },
      '/club_api': {
        target: 'http://club_api:8121',
        changeOrigin: true,
      },
      '/recruits_api': {
        target: 'http://recruit_api:8122',
        changeOrigin: true,
      },
      '/alarm_api': {
        target: 'http://alarm_api:8120',
        changeOrigin: true,
      }
    }
  }
});
