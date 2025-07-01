<template>
  <div class="info-card">
    <!-- 이미지 or 지도 영역 -->
    <div v-if="!showMap">
      <img :src="imageUrl" alt="구장 이미지" class="venue-img" />
    </div>
    <div v-else id="map" class="kakao-map"></div>

    <!-- 구장 정보 -->
    <div class="venue">
      <div class="placenm">{{ match.svcnm }} - {{ match.placenm }} [{{ match.subplacenm }}]</div>
      <div class="price">가격 미정</div>

      <!-- ✅ 신청취소 버튼 (이미 신청한 경우) -->
      <button 
        v-if="isApplied"
        class="apply-button"
        @click="cancelParticipation"
      >
        신청취소
      </button>

      <!-- ✅ 신청하기 버튼 -->
      <button
        v-else
        class="apply-button"
        :disabled="props.match.match_code === 'league' && !clubId"
        @click="applyToMatch"
      >
        신청하기
      </button>

      <!-- ✅ 클럽 리더가 아닌 경우 안내 -->
      <div 
        v-if="!isApplied && props.match.match_code === 'league' && !clubId" 
        class="warn-text"
      >
        ⚠ 클럽 리더만 신청할 수 있습니다.
      </div>
    </div>

    <!-- 주소 및 전화번호 -->
    <div class="address">
      <div>주소: {{ match.areanm }} {{ match.adres }}</div>
      <div>대표전화: {{ match.telno || '정보없음' }}</div>
      <div>운영전화: {{ match.svcendtelno || '정보없음' }}</div>
      <!-- 템플릿 내 버튼 부분 -->
      <div class="button-row">
        <button class="btn-blue" @click="copyAddress">주소복사</button>
        <button class="btn-blue" @click="toggleMap">
          {{ showMap ? '사진보기' : '지도보기' }}
        </button>
      </div>
    </div>

    <!-- 기타 정보 -->
    <div class="meta">
      <span>성별 제한: {{ genderLabel(match.gender_condition) }}</span>
      <span>현재 인원 수: {{ currentCount }} / 18</span>
      <span>매치 상태: {{ statusLabel(match.match_status) }}</span>
      <span>매치 종류: {{ codeLabel(match.match_code) }}</span>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, onMounted, nextTick, inject } from 'vue'
import axios from 'axios'

const userNo = inject('userNo')
const clubId = ref(null)
const isApplied = ref(false)
const showMap = ref(false)
const currentCount = ref(0)

const props = defineProps({
  match: {
    type: Object,
    required: true
  }
})

const checkClubLeader = async () => {
  if (props.match.match_code !== 'league' || !userNo?.value) return

  try {
    const res = await axios.get('/board_api/match/club', {
      params: { userNo: userNo.value }
    })

    if (res.data.club_id) {
      clubId.value = res.data.club_id
    } else {
      clubId.value = null
    }
  } catch (e) {
    console.error('클럽 리더 여부 확인 실패:', e)
    clubId.value = null
  }
}

const imageUrl = `${props.match.img_PATH}`

const toggleMap = async () => {
  showMap.value = !showMap.value
  if (showMap.value) {
    await nextTick()
    loadMap()
  }
}

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

const copyAddress = async () => {
  const fullAddr = `${props.match.areanm || ''} ${props.match.adres || ''}`
  await navigator.clipboard.writeText(fullAddr)
  alert('주소가 복사되었습니다.')
}

const loadMap = () => {
  const kakaoMapReady = () => {
    window.kakao.maps.load(() => {
      const container = document.getElementById('map')
      const options = {
        center: new window.kakao.maps.LatLng(props.match.y, props.match.x),
        level: 3
      }
      const map = new window.kakao.maps.Map(container, options)
      new window.kakao.maps.Marker({
        map,
        position: options.center
      })
    })
  }

  if (!window.kakao || !window.kakao.maps) {
    const script = document.createElement('script')
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=93d2abfcd442ea0ead3eed0dce1e66b3&autoload=false`
    script.onload = kakaoMapReady
    document.head.appendChild(script)
  } else {
    kakaoMapReady()
  }
}

// ✅ 신청 여부를 항상 DB에서 확인
const checkIsApplied = async () => {
  if (!userNo || userNo.value == null) {
    isApplied.value = false
    return
  }

  try {
    const res = await axios.get('/board_api/match/applied', {
      params: {
        matchId: props.match.match_id,
        userNo: userNo.value
      }
    })
    isApplied.value = res.data
  } catch (e) {
    console.error('참가 여부 확인 실패:', e)
    isApplied.value = false
  }
}

const applyToMatch = async () => {
  if (!userNo || userNo.value == null) {
    alert('로그인 후 이용해주세요.')
    return
  }

  if (props.match.match_code === 'league' && !clubId.value) {
    alert('⚠ 클럽 리더만 신청할 수 있습니다.')
    return
  }

  try {
    const payload = {
      match_id: props.match.match_id,
      user_no: userNo.value,
      ...(props.match.match_code === 'league' ? { club_id: clubId.value } : {})
    }

    await axios.post('/board_api/match/apply', payload)
    alert('매치 참가 신청이 완료되었습니다!')
    await checkIsApplied()
  } catch (error) {
    console.error('신청 실패:', error)
    alert('매치 참가 신청에 실패했습니다.')
  }
}

const cancelParticipation = async () => {
  if (!userNo || userNo.value == null) {
    alert('로그인 후 이용해주세요.')
    return
  }

  if (!confirm('참가 신청을 취소하시겠습니까?')) return

  try {
    await axios.delete('/board_api/match/cancel', {
      params: {
        matchId: props.match.match_id,
        userNo: userNo.value
      }
    })
    alert('참가 신청이 취소되었습니다.')
    await checkIsApplied()
    await fetchParticipantCount()
  } catch (e) {
    console.error('취소 실패:', e)
    alert('참가 신청 취소에 실패했습니다.')
  }
}

const fetchParticipantCount = async () => {
  try {
    const res = await axios.get('/board_api/match/participants/count', {
      params: {
        matchId: props.match.match_id
      }
    })
    currentCount.value = res.data
  } catch (e) {
    console.error('인원 수 조회 실패:', e)
    currentCount.value = 0
  }
}

onMounted(() => {
  checkIsApplied()
  fetchParticipantCount()
  checkClubLeader() // ✅ 클럽 리더 여부 확인
})
</script>

<style scoped>
.venue-img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 12px;
}
.kakao-map {
  width: 100%;
  height: 400px;
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
.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 14px;
  margin-top: 8px;
}
.info-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 16px;
  margin-top: 12px;
  margin-bottom: 12px; /* ✅ 다른 컴포넌트와 간격 확보 */
  background-color: #f9f9f9;
}

.btn-blue {
  background-color: #007bff;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-blue:hover {
  background-color: #0056b3;
}

.button-row {
  margin-top: 4px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
