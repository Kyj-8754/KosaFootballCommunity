<!-- components/board/match/detail/MatchInfoCard.vue -->
<template>
  <div class="info-card">
    <!-- 구장 정보 -->
    <div class="venue">
      <div class="placenm">{{ match.PLACENM }} {{ match.SUBPLACENM }}</div>
      <div class="price">{{ match.match_price?.toLocaleString() || '가격 미정' }}원</div>
      <button class="apply-button">신청하기</button>
    </div>

    <!-- 주소 및 버튼 -->
    <div class="address">
      <div>{{ match.ADRES }}</div>
      <div>{{ match.DTLCONT }}</div>
      <div class="button-row">
        <button @click="copyAddress">주소복사</button>
        <button @click="$emit('map', match)">지도보기</button>
      </div>
    </div>

    <!-- 
    <div class="description">
      {{ match.match_description || '매치 상세 정보가 없습니다.' }}
    </div> -->

    <!-- 추가 정보 -->
    <div class="meta">
      <span>{{ match.ORGNM?.includes('실내') ? '실내' : '실외' }}</span>
      <span>{{ genderLabel(match.gender_condition) }}</span>
      <span>현재 인원 수 : {{ match.match_now_count || 12 }} / {{ match.match_max_count || 18 }}</span>
      <span>{{ match.match_title }}</span>
      <span>매니저 : ○ ○ ○</span>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const emit = defineEmits(['map'])

const props = defineProps({
  match: {
    type: Object,
    required: true
  }
})

const genderLabel = (code) => {
  if (code === 'male') return '남성 전용'
  if (code === 'female') return '여성 전용'
  return '성별 무관'
}

const copyAddress = async () => {
  const fullAddr = `${props.match.ADRES || ''} ${props.match.DTLCONT || ''}`
  await navigator.clipboard.writeText(fullAddr)
  alert('주소가 복사되었습니다.')
}
</script>

<style scoped>
.info-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 16px;
  margin-top: 12px;
  background-color: #f9f9f9;
}
.venue {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.placenm {
  font-weight: bold;
  font-size: 16px;
}
.price {
  color: #666;
}
.apply-button {
  background-color: #007bff;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.address {
  font-size: 14px;
  margin-bottom: 8px;
}
.button-row {
  margin-top: 4px;
  display: flex;
  gap: 8px;
}
.description {
  margin: 8px 0;
  padding: 8px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 14px;
  margin-top: 8px;
}
</style>
