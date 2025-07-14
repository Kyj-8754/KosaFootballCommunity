<template>
  <div class="text-sm text-gray-800 space-y-1">
    <div>강수확률 : {{ data.POP }}%</div>
    <!--<div>강수형태: {{ ptyLabel }}</div>-->
    <div v-if="precipLabel">
      {{ precipLabel }}
    </div>
    <div>하늘상태: {{ skyLabel }}</div>
    <div>기온: {{ data.TMP }}℃</div>
    <!--
    <div>습도: {{ data.REH }}%</div>
    <div>풍속: {{ data.WSD }} m/s</div>-->
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: Object
})

const skyLabel = computed(() => {
  const map = { '1': '맑음', '3': '구름많음', '4': '흐림' }
  return map[props.data?.SKY] || '-'
})

const ptyLabel = computed(() => {
  const map = { '0': '없음', '1': '비', '2': '비/눈', '3': '눈', '4': '소나기' }
  return map[props.data?.PTY] || '-'
})

// ❄️ 눈(PTY=3) → SNO, ☔ 비(PTY=1) → PCP, 그 외는 null
const precipLabel = computed(() => {
  const pty = props.data?.PTY
  if (pty === '3' && props.data?.SNO) return `신적설: ${props.data.SNO}`
  if (pty === '1' && props.data?.PCP) return `강수량: ${props.data.PCP}`
  return null
})
</script>
