<template>
  <div
    class="weather-widget"
    :class="{ collapsed: isCollapsed }"
    :style="{ opacity: isCollapsed ? 1 : opacity }"
  >
    <!-- 접기/펼치기 버튼만 항상 보이도록 -->
    <div class="collapse-toggle" @click="isCollapsed = !isCollapsed">
      <span>{{ isCollapsed ? '펼치기 ▼' : '접기 ▲' }}</span>
    </div>

    <!-- 펼쳐진 경우에만 나머지 컨텐츠 렌더링 -->
    <div v-if="!isCollapsed">
      <div class="top-section">
        <WeatherImageBox :sky="current.SKY" :pty="current.PTY" />
        <div class="info-section">
          <RegionSelector v-model="region" />
          <WeatherDetail :data="current" />
        </div>
      </div>

      <!-- weatherWidget.vue -->
      <ForecastTimeline :forecasts="forecastList" @expand="handleChildExpand" />


      <div class="opacity-slider" @mousedown.stop @mouseup.stop @mousemove.stop>
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
  </div>
</template>

<script setup>
import { ref, watchEffect, watch, nextTick } from 'vue'
import axios from 'axios'
import WeatherImageBox from './weatherImageBox.vue'
import RegionSelector from './regionSelector.vue'
import WeatherDetail from './weatherDetail.vue'
import ForecastTimeline from './forecastTimeline.vue'

const opacity = ref(1)
const isCollapsed = ref(false)

const region = ref('강남구')
const forecastList = ref([])
const current = ref({})



// 한국 시간대로 바꾸도록
const getKoreanDateYYYYMMDD = () => {
    const now = new Date(); // 현재 시각 (서버의 시간대를 따름, 예: UTC)

    // 서버의 시간대와 상관없이 'Asia/Seoul' 시간대의 날짜를 얻기 위함
    // toLocaleString()으로 특정 시간대의 날짜/시간 문자열을 얻고, 이를 다시 Date 객체로 파싱
    const dateInKST = new Date(now.toLocaleString('en-US', { timeZone: 'Asia/Seoul' }));
    
    // 연, 월, 일 값을 추출합니다.
    const year = dateInKST.getFullYear();
    const month = (dateInKST.getMonth() + 1).toString().padStart(2, '0'); // 월은 0부터 시작하므로 +1
    const day = dateInKST.getDate().toString().padStart(2, '0');

    return `${year}${month}${day}`;
};

const today = getKoreanDateYYYYMMDD();

// 시간대가 한국 시간 아님 
// const today = new Date().toISOString().slice(0, 10).replace(/-/g, '')

const emit = defineEmits(['expand'])

const handleChildExpand = () => {
  emit('expand')  // ForecastTimeline 펼침 → App.vue에 전달
}

watchEffect(async () => {
  const res = await axios.get('/widget_api/widget/forecast', {
    params: { region: region.value, date: today }
  })

  const rawList = res.data

  const grouped = {}
  for (const item of rawList) {
    const time = item.weather_fcst_time
    if (!grouped[time]) grouped[time] = { fcst_time: time }
    grouped[time][item.weather_code] = item.weather_value
  }

  const result = Object.values(grouped).sort((a, b) => a.fcst_time.localeCompare(b.fcst_time))

  forecastList.value = result
  current.value = result[0] || {}
})

watch(isCollapsed, async (newVal) => {
  if (!newVal) {
    await nextTick();   // 이제 가능!
    emit('expand');
  }
})
</script>

<style scoped>
.weather-widget {
  font-family: sans-serif;
  width: 300px;
  margin: 0 auto;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.05);
  user-select: none;
  background-color: #ffffff;
  padding: 8px;
  transition: width 0.2s ease, padding 0.2s ease;
}

/* ✅ 접힌 상태: 최소한의 폭과 패딩만 유지 */
.weather-widget.collapsed {
  width: fit-content;
  padding: 4px 8px;
}

.collapse-toggle {
  text-align: right;
  font-size: 12px;
  cursor: pointer;
  color: #555;
  white-space: nowrap;
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