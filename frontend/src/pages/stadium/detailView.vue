<template>
	<div class="container-fluid main-container">
		<div class="row h-100">
			<main class="main-area">
				<h2>구장 상세페이지</h2>
			<div class="container my-4">
				<div class="row g-4 align-items-start">
					<!-- 좌측: 이미지 -->
					<div class="col-md-5 d-flex justify-content-center">
						<img :src="stadiumDB.img_PATH" class="img-fluid border rounded shadow-sm" 
						style="max-width: 100%; max-height: 360px; aspect-ratio: 4/3; object-fit: cover;" />
					</div>

					<!-- 우측: 달력 + 가격 -->
					<div class="col-md-7">
						<div class="mb-3 d-flex justify-content-between align-items-center">
							<h5 class="mb-0 fw-bold">이용 요금</h5>
							<span class="fs-4 fw-bold text-primary">{{ stadiumDB.price }}원</span>
						</div>
						<div class="p-2 border rounded bg-light" style="max-width: 320px;">
							<v-calendar
							is-expanded
							:attributes="[{
								key: 'available',
								dates: availableDates,
								highlight: true,
								contentClass: 'available-date'
							},
							selectedDate?{
								key: 'selected',
								dates: selectedDate.value,
								highlight: true,
								contentClass: 'selected-date'
							}
							:null]"
							@dayclick="onDayClick"
							/>
						</div>
					</div>
				</div>
				<!-- 상단 버튼들 -->
				<div class="d-flex justify-content-end gap-2 my-3">
					<button class="btn btn-secondary btn-sm" @click="goToList">목록</button>
					<button class="btn btn-dark btn-sm" @click="goToReservation">예약</button>
				</div>
				<!-- 탭 버튼 -->
				<div class="d-flex gap-2 justify-content-center border-bottom mb-3">
					<button class="btn" :class="{ 'btn-primary': activeTab === 'overview' }" @click="activeTab = 'overview'">이용 안내</button>
					<button class="btn" :class="{ 'btn-primary': activeTab === 'detail' }" @click="activeTab = 'detail'">알림</button>
					<button class="btn" :class="{ 'btn-primary': activeTab === 'map' }" @click="activeTab = 'map'">지도</button>
					<button class="btn" :class="{ 'btn-primary': activeTab === 'review' }" @click="activeTab = 'review'">리뷰</button>
				</div>
				<!-- 탭 콘텐츠 -->
				<div class="tab-content">
					<!-- 이용 안내 -->
					<div v-show="activeTab === 'overview'">
						<p v-html="stadiumDB.dtlcont"></p>
					</div>
					<!-- 알림 -->
					<div v-show="activeTab === 'detail'">
						<p v-html="stadiumDB.notice"></p>
					</div>
					<!-- 지도 -->
					<div v-show="activeTab === 'map'">
						<div id="map" style="width:100%;height:350px;"></div>
						<div>
							주소 : {{ stadiumDB.adres }}
						</div>
						<div>
							전화 번호 : {{ stadiumDB.telno }} 
						</div>
					</div>
					<!-- 리뷰 -->
					<div v-show="activeTab === 'review'">
						<StadiumComment :SVCID ="SVCID" :userId ="userId" :userName ="userName"/>
    				</div>
  				</div>
			</div>
			</main>
		</div>
	</div>
</template>

<style scoped>
/* 예약 가능 날짜 (연한 파랑) */
.available-date {
  background-color: #e5f1ff !important;
  color: #0d6efd;
  border-radius: 50%;
}

.selected-date{
	background-color: black !important;
	color: white !important;
	border-radius: 50%;
	font-weight: bold;
	box-shadow: 0 0 0 2px white, 0 0 0 4px #0d6efd; /* 시각적 강조 */
}
</style>

<script setup>
	import DOMPurify from 'dompurify'; // notice관련 문제 해결중
	import {ref, onMounted, computed, inject, watch, nextTick } from 'vue'
	import { useRoute, useRouter } from 'vue-router'
	import axios from 'axios'
	import 'v-calendar/style.css'
	import { StadiumDataStore } from '@/stores/stadiumStore'
	import  StadiumComment  from '@/components/comment/comment.vue'


	// pinia를 이용한 저장
	const stadiumStore = StadiumDataStore();

	//아이디 관련
	const userId = inject('userId')
	const userName = inject('userName')
	//탭관련
	const activeTab = ref('overview')
	let isMapInitialized = false
	let isScriptLoaded = false
	
	// 달력관련 시작
	// 예약 가능한 날짜
	const availableDates = ref([])
	// 예약 선택 날짜
	const selectedDate = ref(null)

	const formatDate = (date) => {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}


const onDayClick = (day) => {
  const dateStr = formatDate(day.date)

  if (!availableDates.value.includes(dateStr)) {
    return // 예약 불가 날짜는 무시
  }

  selectedDate.value = dateStr;
  
}

	// 달력관련 끝

	const safeNotice = computed(() => {
  		return DOMPurify.sanitize(stadiumDB.value.dtlcont);
	});

	const router = useRouter() // 보낼 경로
	const route = useRoute()	// 현재 경로
	const SVCID = route.query.SVCID // 현재 경로의 SCVID
	const stadiumDB = ref({})	// 게시물 
	
  
	// 페이지 로딩 시 
	onMounted(async() => {
		await fetchStadiumData();	// 게시판
	});

	// 게시물 불러오기
	const fetchStadiumData = async () => {
		const res = await axios.get('/stadium_api/stadium/detailView', { params: { SVCID } });
		stadiumDB.value = res.data.stadiumDB.stadium;
		availableDates.value = res.data.stadiumDB.slot.map(item => item.slot_DATE);

		//store저장
		stadiumStore.setStadium(stadiumDB.value);
	};	

	// 구장 목록으로 넘어가기
	function goToList(){
		router.push({name: 'Stadium_List'})
	}

	// 예약 창으로 넘어가기
	function goToReservation(){
		router.push({name: 'reservation_Form', query: {date: selectedDate.value, SVCID: SVCID}})
	}

watch(activeTab, async (newTab) => {
  if (newTab === 'map' && !isMapInitialized) {
    await nextTick()           // 👉 DOM이 그려진 다음
    loadKakaoMapScript()       // 👉 스크립트 로드 + 지도 그리기
  }
})

// 스크립트를 동적으로 삽입하고 지도 초기화, 키를 숨기기 위해서 관리
const loadKakaoMapScript = () => {

	if (isScriptLoaded) {
    window.kakao.maps.load(() => {
      initKakaoMap()
    })
    return
  }

  const script = document.createElement('script')
  script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAOMAP_API_KEY}&autoload=false`
  script.async = true
  document.head.appendChild(script)

  script.onload = () => {
    window.kakao.maps.load(() => {
      initKakaoMap()
    })
  }
}

// 지도 초기화 함수
const initKakaoMap = () => {
	// X,Y받기
  const { x, y } = stadiumDB.value
  if (!x || !y) {
    console.warn('좌표 없음')
    return
  }
  	const centerPos = new kakao.maps.LatLng(Number(y), Number(x));
  	// 이미지 지도에 표시할 마커입니다
	  
	  const container = document.getElementById('map')
	  const options = {
		  center: centerPos,
		  level: 3
		}
		
		 const map = new window.kakao.maps.Map(container, options)
		
		const marker = new kakao.maps.Marker({
		  position: centerPos
		})

		
		marker.setMap(map)

  kakao.maps.event.addListener(map, 'tilesloaded', () => {
    map.setCenter(centerPos)
  })

  isMapInitialized = true
}

</script>



