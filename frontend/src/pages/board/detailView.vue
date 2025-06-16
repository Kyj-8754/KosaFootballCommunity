<template>
    <div class="container-fluid main-container">
		<div class="row h-100">
			 <main class="main-area">
				<h2>게시물 상세</h2>
				<!-- 게시글 내용 테이블 -->
				<div class="container mt-5">
					<table class="table" style="width: 100%; max-width: 1000px; margin: 0 auto;">
						<tbody>
							<tr class="title-row">
								<td colspan="4" style="font-size: 26px; font-weight: bold;">{{boardDB.title}}</td>
							</tr>
							<tr class="text-muted small">
								<td style="width: 25%;">작성자: <strong>{{boardDB.writer}}</strong></td>
								<td style="width: 25%;">작성일: {{ boardDB.modified_date ? boardDB.modified_date.substring(0, 16) + ' (수정됨)' : boardDB.reg_date?.substring(0, 16) }}</td>
								<td style="width: 25%;">조회수: {{boardDB.view_count}}</td>
								<td style="text-align: right;">
								<button @click="goToUpdateForm" class="btn btn-outline-primary btn-sm">수정하기</button>
								</td>
							</tr>
							<tr>
								<td colspan="4">
									<div class="p-3" style="min-height: 150px;">{{boardDB.content}}</div>
								</td>
							</tr>
						</tbody>
					</table>
				</div>

				
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
								<div class="text-end" v-if="memberStore.userid === comment.userid || memberStore.supervisor === 'Y'">
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
				<template v-if="memberStore.authenticated">
					<form id="regist" @submit.prevent="regist">
						<div class="container mt-4" style="max-width: 1000px;">
							<div class="border rounded p-3">
								<strong class="mb-2 d-block">{{memberStore.userid}}</strong>
								<div class="d-flex align-items-center">
									<textarea type="text" ref="textRef" @input="adjustHeight" class="form-control me-2" v-model="form.content" style="resize: none; overflow: hidden; min-height: 80px; max-height: 300px;" required></textarea>
									<button  type="submit" class="btn btn-secondary flex-shrink-0">등록</button>
								</div>
							</div>
						</div>
					</form>
				</template>
			</main>
		</div>
	</div>
</template>
<script setup>
  import {ref, onMounted, reactive} from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useMemberStore } from '@/stores/member'
  import axios from 'axios'

  const memberStore = useMemberStore(); // 로그인한 유저 정보 체크
  const router = useRouter() // 보낼 경로
  const route = useRoute()	// 현재 경로
  const bno = route.query.bno // 현재 경로의 bno
  const boardDB = ref({ list: [] })	// 게시물 
  const commentDB = ref({ list: [] })	// 댓글 

	//댓글 입력 폼
	const form = reactive({
		comment_no: null,
		bno,
		userid : memberStore.userid,
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
		fetchBoardData();	// 게시판
		fetchComments();	// 댓글
		if (textRef.value) adjustHeight();	// 댓글창 조절
	});

	// 게시물 불러오기
	const fetchBoardData = async () => {
		const res = await axios.get('/api/board/detailView', { params: { bno } });
		boardDB.value = res.data.boardDB;
	};	

	// 게시물의 댓글 불러오기
	const fetchComments = async () => {
		const res = await axios.get('/api/comment/list', { params: { bno } });
		commentDB.value = res.data.commentDB;
	};

	// 게시물 수정하기 넘어감
	function goToUpdateForm() {
		router.push({
		name: 'Board_UpdateForm',
		query: { bno: boardDB.value.bno }
		})
	}

	// 댓글 등록
	function regist() {
		const confirmRegist = confirm("댓글 등록하시겠습니까?")
		if (!confirmRegist) return

		axios.post('/api/comment/regist', form)
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

		axios.post('/api/comment/update', editForm)
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
		
		axios.post('/api/comment/delete', { comment_no })
			.then(res => {
				if (!res.data.error) {
				fetchComments();
				} else {
					alert(res.data.message)
				}
			})
	}

	// 페이지 로딩시 로그인 되어있다면 해당 정보를 form에 입력
	onMounted(() => {
		if (memberStore.authenticated) {
			form.writer = memberStore.userid
			form.passwd = '';
		}
	})
</script>