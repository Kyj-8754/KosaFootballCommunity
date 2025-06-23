<!-- WeatherWidget.vue -->
<template>
  <div class="weather-widget p-4 bg-gray-100 rounded-md w-[360px] mx-auto">
    <div class="flex space-x-2 mb-2">
      <WeatherImageBox :sky="current.SKY" :pty="current.PTY" />
      <div class="flex flex-col space-y-1 w-full">
        <RegionSelector v-model="region" />
        <WeatherDetail :data="current" />
      </div>
    </div>
    <ForecastTimeline :forecasts="forecastList" />
  </div>
</template>

<script setup>
import { ref, watchEffect } from 'vue'
import axios from 'axios'
import WeatherImageBox from './weatherImageBox.vue'
import RegionSelector from './regionSelector.vue'
import WeatherDetail from './weatherDetail.vue'
import ForecastTimeline from './forecastTimeline.vue'

const region = ref('강남구')
const forecastList = ref([])
const current = ref({})

const today = new Date().toISOString().slice(0, 10).replace(/-/g, '')

watchEffect(async () => {
  const res = await axios.get('/api/widget/forecast', {
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
}
</style>
