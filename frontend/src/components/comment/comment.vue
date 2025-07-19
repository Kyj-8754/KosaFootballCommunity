<template>
	<!-- 댓글 섹션 -->
	<div class="container mt-4" style="max-width: 1000px;">
		<template v-for="comment in commentDB" :key="comment.comment_no">
			<p v-if="commentDB.length > 0" class="comment-summary text-muted mb-2">
				{{ commentDB.length }}개의 리뷰, 평균 
				<span class="text-warning">★{{ averageRating.toFixed(1) }}</span>
			</p>
			<div class="card mb-2"  v-if="comment.status !== 'false'">
				<div class="card-body p-3">
					<div class="d-flex justify-content-between align-items-center mb-2 border-bottom">
						<div class="fw-bold">
							{{ comment.username }}
							<div class="rating-display-stars d-flex">
								<div v-for="n in 5" :key="n" class="display-star-wrapper">
									<span class="display-star">☆</span>
									<span class="display-star filled" :style="{ width: getStarFillForComment(comment.rating, n) }">★</span>
								</div>
							</div>
						</div>
						<div class="text-muted" style="font-size: 0.9rem;">
							{{ comment.modified_date ? comment.modified_date.substring(0, 16) + ' (수정됨)' : comment.regist_date?.substring(0, 16) }}
						</div>
					</div>
					<div class="mb-2">
						{{ comment.content }}
					</div>
					<div v-if="comment.username === userId">
						<button class="btn btn-sm btn-outline-secondary me-1" @click="editComment(comment)">수정</button>
						<button class="btn btn-sm btn-outline-danger" @click="deleteComment(comment.comment_no)">삭제</button>
					</div>	
				</div>
			</div>
			<!-- 댓글 수정 창 -->
			<form v-if="editing && editForm.comment_no === comment.comment_no" @submit.prevent="update" class="card mb-2" style="border: 2px;">
				<div class="card mb-2">
					<div class="card-body p-3">
						<div class="d-flex justify-content-between align-items-center mb-2 border-bottom">
							<div class="fw-bold mb-2">{{ comment.username }}</div>
						</div>
						<textarea ref="edittextRef" v-model="editForm.content" class="form-control mb-2" style="resize: none; overflow-y: auto; height: 120px;"required></textarea>
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
	<form id="regist" @submit.prevent="regist" v-if="userId && userName">
		<div class="container mt-4" style="max-width: 1000px;">
			<div class="border rounded p-3">
				<div class="d-flex justify-content-between align-items-center mb-2">
					<strong class="fw-bold">{{ comment.userName }}</strong>
					<div class="rating-input-stars d-flex"  
					@mousedown="dragging = true" 
					@mouseup="dragging = false" 
					@mouseleave="dragging = false" 
					@mousemove="handleDrag" 
					@click="handleClick" 
					ref="starContainer">
						<div v-for="n in 5" :key="n" class="input-star-container">
							<span class="input-star" :class="{ filled: form.rating >= n }">★</span>
							<span class="input-star half" :class="{ filled: form.rating >= n - 0.5 }">★</span>
						</div>
					</div>
				</div>
				<div class="d-flex align-items-center">
					<textarea  ref="registertextRef"  class="form-control me-2" v-model="form.content" style="resize: none; overflow-y: auto; height: 120px;" required></textarea>
					<button  type="submit" class="btn btn-secondary flex-shrink-0">등록</button>
				</div>
			</div>
		</div>
	</form>
</template>

<style>
	@import "@/utils/css/comment.css";
</style>
<script setup>
import {ref, reactive, onMounted} from 'vue'
import axios from 'axios'

// 받아줄 기본 정의
const props = defineProps({
  SVCID: {
    type: String,
    required: true
  },
  userId: {
    type: String,
    required: true
  },
  userName: {
    type: String,
    required: true
  }
});

const commentDB = ref({ list: [] })	// 댓글 

//댓글 입력 폼
	const form = reactive({
		comment_no: null,
		content : '',
		username : props.userId,
		svcid : props.SVCID,
		rating : 0
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
			editForm.username = props.userName;
			editing.value = true;
		}
	};

	//api로 댓글 수정보내기
	const editForm = reactive({
		comment_no: null,
		content: ''
	});



	// 페이지 로딩 시 
	onMounted(async() => {
		fetchComments();  // ✅ 먼저 댓글 데이터를 불러오고
	});

//게시물의 댓글 불러오기
	const fetchComments = async () => {
		const res = await axios.get('/stadium_api/comment/list', { params: { SVCID: props.SVCID } });
		commentDB.value = res.data.commentDB;
	};

// 별점 평균 계산용	
const averageRating = computed(() => {
  if (commentDB.value.length === 0) return 0;
  const sum = commentDB.value.reduce((acc, c) => acc + (c.rating || 0), 0);
  return sum / commentDB.value.length;
});

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

		axios.patch('/stadium_api/comment/update', editForm)
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
		
		axios.delete('/stadium_api/comment/delete', { 
			 data: { comment_no } 
			})
			.then(res => {
				if (!res.data.error) {
				fetchComments();
				} else {
					alert(res.data.message)
				}
			})
	}

	// 별점을 위한 스크립트

	const starContainer = ref(null)
const dragging = ref(false)

const handleClick = (e) => {
  updateRating(e)
}

const handleDrag = (e) => {
  if (!dragging.value) return
  updateRating(e)
}

const updateRating = (e) => {
  const container = starContainer.value
  if (!container) return

  const rect = container.getBoundingClientRect()
  const offsetX = e.clientX - rect.left
  const widthPerHalfStar = rect.width / 10 // 5별 * 2 (0.5점)

  let newRating = Math.ceil(offsetX / widthPerHalfStar) * 0.5
  newRating = Math.min(5, Math.max(0, newRating)) // clamp between 0 and 5
  form.rating = newRating
}


// script setup 아래 함수 추가
const getStarFillForComment = (rating, n) => {
  if (rating >= n) return '100%';
  if (rating >= n - 0.5) return '50%';
  return '0%';
};
</script>