import {ref, onMounted, computed, inject, watch, nextTick, reactive } from 'vue'
import axios from 'axios'
import { useRouter, useRoute } from 'vue-router'
import 'v-calendar/style.css'
import { StadiumDataStore } from '@/stores/stadiumStore'


export function useStadiumList() {
	
	const router = useRouter() // 보낼 경로
	const route = useRoute() // 현재 경로

    	const searchTypes = [
		{ label: '전체', value: '' },
		{ label: '지역명', value: 'areanm' },
		{ label: '장소명', value: 'placenm' }
	] 

	// 초기값 설정
	const pageResponse = reactive ({ 
		list: [],
		endPage: 1,
		next: false,
		prev: false,
		startPage: 1,
		totalPage: 1,
		searchValue: "",
		totalCount: 1,
	})

	// size와 searchType 변경시 감지하여 함수 발생
	const pageNo = computed(() => parseInt(route.query.pageNo) || 1)

	const searchType = ref('');
	
	// 페이징 기능, 누르면 해당 페이지 이동하도록 구현현
	const pageRange = computed(() => {
	  const range = []
	  for (let i = pageResponse.startPage; i <= pageResponse.endPage; i++) {
		range.push(i)
	  }
	  return range
	})

	watch(
  () => route.query,
  (query) => {
    const newPageNo = parseInt(query.pageNo) || 1
    const newSearchType = query.searchType || ''
    const newSearchValue = query.searchValue || ''

    // 명확히 상태 분리해서 저장
    searchType.value = newSearchType
    pageResponse.searchValue = newSearchValue

    fetchData(newPageNo, newSearchType, newSearchValue)
  },
  { immediate: true }
)
	
	// 값 검색시 넘어가는 로직
	function searchID() {
		router.push({
			name: 'Stadium_List',
			query: {
			pageNo: 1, // 검색 시 1페이지로 이동
			searchType: searchType.value || '',
			searchValue: pageResponse.searchValue || ''
			}
		})
	}
	// 값 변경시 다시 list 가져오도록 요청
	function fetchData(pageNo, searchType, searchValue) {
		axios.get(`/stadium_api/stadium/list`,{
			params:{
				pageNo,
				searchType: searchType || '',
				searchValue: searchValue || ''}
			})
			.then(res => {
			Object.assign(pageResponse, res.data.pageResponse)
			});
	}

    // 값 초기화 변수
    function resetSearch(){
        router.push({name: 'Stadium_List'});
    }

	// url 변경시 반응
	function makeUrl(pageNo) {
		const base = `list?pageNo=${pageNo}`
		const params = []

  if (searchType.value && searchType.value !== '전체') {
    params.push(`searchType=${encodeURIComponent(searchType.value)}`)
  }

  if (pageResponse.searchValue && pageResponse.searchValue.trim() !== '') {
    params.push(`searchValue=${encodeURIComponent(pageResponse.searchValue.trim())}`)
  }

  return params.length ? `${base}&${params.join('&')}` : base
	}


     return {
    searchTypes,
    searchType,
    pageResponse,
    pageNo,
    pageRange,
    searchID,
    fetchData,
    makeUrl,
    resetSearch
  }

  
}



export function stdaiumDetail() {

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

	// 이제 안씀
// 	const formatDate = (date) => {
//   const y = date.getFullYear()
//   const m = String(date.getMonth() + 1).padStart(2, '0')
//   const d = String(date.getDate()).padStart(2, '0')
//   return `${y}-${m}-${d}`
// }

// 이제 안씀
// const onDayClick = (day) => {
//   const dateStr = formatDate(day.date)

//   if (!availableDates.value.includes(dateStr)) {
//     return // 예약 불가 날짜는 무시
//   }

//   selectedDate.value = dateStr;
  
// }

	// 달력관련 끝



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

	// 예약 창으로 넘어가기, 현재는 글 작성으로 넘어감
	function goToReservation(){
		router.push('/board/boardregisterform')
	}

	// html관련
	function stripHtml(html) {
  const tmp = document.createElement('div')
  tmp.innerHTML = html || ''
  return tmp.textContent || tmp.innerText || ''
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


     return {
		SVCID,
		activeTab,
		stadiumDB,
		userId,
		userName,
		goToList,
		goToReservation,
		stripHtml,
  }
}
