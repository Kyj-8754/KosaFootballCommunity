<template>
    <div class="container-fluid main-container">
		<div class="row h-100">
			 <main class="main-area">
				<h2>구장 상세페이지</h2>

		<div class="container my-4">
  <div class="row">
    <!-- 좌측: 이미지 -->
    <div class="col-md-5">
      <img :src="stadiumDB.img_PATH" class="img-fluid border rounded" />
    </div>

    <!-- 우측: 달력 -->

  <div class="col-md-7">
    <h5>예약 가능 날짜 선택</h5>
    <Calendar
      v-model="selectedDate"
      :attributes="calendarAttributes"
      :min-date="new Date()" 
      is-inline
    />

    <div class="mt-3">
      <strong>선택된 날짜: </strong>{{ selectedDate }}
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
    <div v-show="activeTab === 'overview'">
      <p v-html="stadiumDB.dtlcont"></p>
    </div>
    <div v-show="activeTab === 'detail'">
      <p v-html="stadiumDB.notice"></p>
    </div>
    <div v-show="activeTab === 'map'">
      <!-- 지도 -->
		<div>
	  		주소 : {{ stadiumDB.adres }}
		</div>
	   <div>
	  		전화 번호 : {{ stadiumDB.telno }} 
		</div>
	   
	   
    </div>
    <div v-show="activeTab === 'review'">
      <!-- 리뷰 컴포넌트 -->
	   	<!-- 댓글 섹션 -->
				<div class="container mt-4" style="max-width: 1000px;">
					<template v-for="comment in commentDB" :key="comment.comment_no">
							<div class="card mb-2">
							<div class="card-body p-3">
								<div class="d-flex justify-content-between align-items-center mb-2 border-bottom">
									<div class="fw-bold">{{ comment.userid }}</div>
									<div class="text-muted" style="font-size: 0.9rem;">
										{{ comment.modified_date ? comment.modified_date.substring(0, 16) + ' (수정됨)' : comment.regist_date?.substring(0, 16) }}
									</div>
								</div>
								<div class="mb-2">{{ comment.content }}</div>
								<button class="btn btn-sm btn-outline-secondary me-1" @click="editComment(comment)">수정</button>
								<button class="btn btn-sm btn-outline-danger" @click="deleteComment(comment.comment_no)">삭제</button>
							</div>
						</div>
						<!-- 댓글 수정 창 -->
						<form v-if="editing && editForm.comment_no === comment.comment_no" @submit.prevent="update" class="card mb-2" style="border: 2px;">
							<div class="card mb-2">
								<div class="card-body p-3">
									<div class="d-flex justify-content-between align-items-center mb-2 border-bottom">
										<div class="fw-bold mb-2">{{ comment.userid }}</div>
									</div>
									<textarea ref="textRef" @input="adjustHeight" v-model="editForm.content" class="form-control mb-2" style="resize: none; overflow: hidden; min-height: 80px; max-height: 300px;"required></textarea>
									<div class="text-end">
										<button type="submit" class="btn btn-sm btn-outline-secondary me-1">수정</button>
										<button type="button" class="btn btn-sm btn-outline-danger" @click="editing = false">취소</button>
									</div>
								</div>
							</div>
						</form>
					</template>
				</div>

				

				<!-- 댓글 입력 섹션 -->
					<form id="regist" @submit.prevent="regist">
						<div class="container mt-4" style="max-width: 1000px;">
							<div class="border rounded p-3">
								<strong class="mb-2 d-block">{{ userId }}</strong>
								<div class="d-flex align-items-center">
									<textarea type="text" ref="textRef" @input="adjustHeight" class="form-control me-2" v-model="form.content" style="resize: none; overflow: hidden; min-height: 80px; max-height: 300px;" required></textarea>
									<button  type="submit" class="btn btn-secondary flex-shrink-0">등록</button>
								</div>
							</div>
						</div>
					</form>
    </div>
  </div>
</div>
				
			
			</main>
		</div>
	</div>
</template>
<script setup>
import DOMPurify from 'dompurify'; // notice관련 문제 해결중
  import {ref, onMounted, reactive, computed, watch, inject} from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import axios from 'axios'
  import { Calendar  } from 'v-calendar'
  import 'v-calendar/style.css'

  //아이디 관련
  const userId = inject('userId')

  //탭관련
  const activeTab = ref('overview')

// 달력관련
const selectedDate = ref(null)



const availableDatesRaw = [new Date().toISOString().split('T')[0],'2025-06-22', '2025-06-26']
const calendarAttributes = computed(() => [
  {
    key: 'available',
    dates: availableDatesRaw,
    highlight: { class: 'bg-primary text-white' },
    popover: { label: '예약 가능' }
  }
])

watch(selectedDate, (newVal) => {
  console.log('선택한 날짜:', newVal)
})
// 달력관련 끝

  const safeNotice = computed(() => {
  return DOMPurify.sanitize(stadiumDB.value.dtlcont);
});

  const router = useRouter() // 보낼 경로
  const route = useRoute()	// 현재 경로
  const SVCID = route.query.SVCID // 현재 경로의 SCVID
  const stadiumDB = ref({ list: [] })	// 게시물 
  const commentDB = ref({ list: [] })	// 댓글 

	//댓글 입력 폼
	const form = reactive({
		comment_no: null,
		content : '',
	})

	// 댓글 수정란에 칸 넣기기
	const editing  = ref(false);
	const editComment = (comment) => {
		if (editing.value && editForm.comment_no === comment.comment_no) {
			// 이미 수정 중인 댓글이라면 → 취소로 간주
			editing.value = false;
			editForm.comment_no = null;
			editForm.content = '';
		} else {
			// 수정 시작
			editForm.comment_no = comment.comment_no;
			editForm.content = comment.content;
			editForm.userid = memberStore.userid;
			editing.value = true;
		}
	};

	//api로 댓글 수정보내기
	const editForm = reactive({
		comment_no: null,
		content: ''
	});

	// 입력창 높낮이 조절
	const textRef = ref(null);
	const adjustHeight = () => {
		const el = textRef.value;
		if (el) {
			el.style.height = 'auto';
			el.style.height = `${el.scrollHeight}px`;
		}
	};
  
	// 페이지 로딩 시 
	onMounted(() => {
		fetchStadiumData();	// 게시판
		fetchComments();	// 댓글
		if (textRef.value) adjustHeight();	// 댓글창 조절
	});

	// 게시물 불러오기
	const fetchStadiumData = async () => {
		const res = await axios.get('/stadium_api/stadium/detailView', { params: { SVCID } });
		stadiumDB.value = res.data.stadiumDB;
	};	

	//게시물의 댓글 불러오기
	const fetchComments = async () => {
		const res = await axios.get('/stadium_api/comment/list', { params: { SVCID } });
		commentDB.value = res.data.commentDB;
	};

	// 구장 목록으로 넘어가기
	function goToList(){
		router.push({name: 'Stadium_List'})
	}

	// 댓글 등록
	function regist() {
		const confirmRegist = confirm("댓글 등록하시겠습니까?")
		if (!confirmRegist) return

		axios.post('/stadium_api/comment/regist', form)
		.then(res => {
		if (!res.data.error) {
			fetchComments();
			form.content = ""
		} else {

		}
		})
		.catch(err => {
			console.error('등록 실패', err)
			alert('등록 중 오류가 발생했습니다.')
		})
	}

	// 댓글 업데이트
	function update() {
		const confirmRegist = confirm("수정하시겠습니까?")
		if (!confirmRegist) return

		axios.post('/stadium_api/comment/update', editForm)
			.then(res => {
			if (!res.data.error) {
				fetchComments();
				editing.value = false;
			} else {
			alert("수정 실패했습니다.")
			}
			})
	}

	// 삭제
	function deleteComment(comment_no) {
		const confirmRegist = confirm("삭제하시겠습니까?")
		if (!confirmRegist) return
		
		axios.post('/stadium_api/comment/delete', { comment_no })
			.then(res => {
				if (!res.data.error) {
				fetchComments();
				} else {
					alert(res.data.message)
				}
			})
	}

	// 페이지 로딩시 로그인 되어있다면 해당 정보를 form에 입력
	// onMounted(() => {
	// 	if (memberStore.authenticated) {
	// 		form.writer = memberStore.userid
	// 		form.passwd = '';
	// 	}
	// })
</script>