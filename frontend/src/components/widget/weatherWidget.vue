<template>
  <div class="weather-widget" :style="{ opacity: opacity }">
    <!-- 상단 영역 -->
    <div class="top-section">
      <WeatherImageBox :sky="current.SKY" :pty="current.PTY" />
      <div class="info-section">
        <RegionSelector v-model="region" />
        <WeatherDetail :data="current" />
      </div>
    </div>

    <!-- 하단 시간별 예보 -->
    <ForecastTimeline :forecasts="forecastList" />

    <!-- ✅ 투명도 조절 슬라이더 -->
    <div class="opacity-slider">
      <label>투명도: {{ (opacity * 100).toFixed(0) }}%</label>
      <input
        type="range"
        min="0.1"
        max="1"
        step="0.01"
        v-model="opacity"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watchEffect } from 'vue'
import axios from 'axios'
import WeatherImageBox from './weatherImageBox.vue'
import RegionSelector from './regionSelector.vue'
import WeatherDetail from './weatherDetail.vue'
import ForecastTimeline from './forecastTimeline.vue'

const opacity = ref(1)

const region = ref('강남구')
const forecastList = ref([])
const current = ref({})

const today = new Date().toISOString().slice(0, 10).replace(/-/g, '')

watchEffect(async () => {
  const res = await axios.get('/widget_api/widget/forecast', {
    params: { region: region.value, date: today }
  })

  const rawList = res.data // 서버에서 받은 row 데이터들

  // 🧠 그룹핑 로직: 시간별로 묶어서 { TMP, SKY, POP, ... } 구조로 변경
  const grouped = {}
  for (const item of rawList) {
    const time = item.weather_fcst_time
    if (!grouped[time]) {
      grouped[time] = { fcst_time: time }
    }
    grouped[time][item.weather_code] = item.weather_value
  }

  const result = Object.values(grouped).sort((a, b) => a.fcst_time.localeCompare(b.fcst_time))

  forecastList.value = result
  current.value = result[0] || {}
})
</script>

<style scoped>
.weather-widget {
  font-family: sans-serif;
  width: 300px;
  padding: 16px;
  margin: 0 auto;
  background-color: #ffffff;        /* ✅ 흰색 배경 */
  border: 1px solid #d1d5db;        /* ✅ 연한 회색 테두리 (Tailwind 기준 gray-300) */
  border-radius: 8px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.05); /* ✅ 그림자 약하게 */
  user-select: none;
}

.top-section {
  display: flex;
  margin-bottom: 12px;
}

.top-section > *:first-child {
  flex: 0 0 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
}

.info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.opacity-slider {
  margin-top: 12px;
  font-size: 12px;
  color: #555;
  opacity: 1 !important;
}

.opacity-slider input[type='range'] {
  width: 100%;
  margin-top: 4px;
}
</style>