<template>
  <div class="info-card">
    <!-- 이미지 영역 -->
    <img :src="imageUrl" alt="구장 이미지" class="venue-img" />

    <!-- 구장 정보 -->
    <div class="venue">
      <div class="placenm">{{ match.svcnm }} - {{ match.placenm }} [{{ match.subplacenm }}]</div>
      <div class="price">가격 미정</div>
      <button class="apply-button">신청하기</button>
    </div>

    <!-- 주소 및 전화번호 -->
    <div class="address">
      <div>주소: {{ match.areanm }} {{ match.adres }}</div>
      <div>대표전화: {{ match.telno || '정보없음' }}</div>
      <div>운영전화: {{ match.svcendtelno || '정보없음' }}</div>
      <div class="button-row">
        <button @click="copyAddress">주소복사</button>
        <button @click="$emit('map', match)">지도보기</button>
      </div>
    </div>

    <!-- 기타 정보 -->
    <div class="meta">
      <span>성별 제한: {{ genderLabel(match.gender_condition) }}</span>
      <span>현재 인원 수: 12 / 18</span>
      <span>매치 상태: {{ statusLabel(match.match_status) }}</span>
      <span>매치 종류: {{ codeLabel(match.match_code) }}</span>
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

const statusLabel = (code) => {
  switch (code) {
    case 'waiting': return '대기중'
    case 'active': return '진행중'
    case 'completed': return '진행 완료'
    default: return code
  }
}

const codeLabel = (code) => {
  return code === 'social' ? '소셜매치' : code === 'league' ? '리그매치' : code
}

const imageUrl = `${props.match.img_PATH}`

const copyAddress = async () => {
  const fullAddr = `${props.match.adres || ''}`
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
.venue-img {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 12px;
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
.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 14px;
  margin-top: 8px;
}
</style>