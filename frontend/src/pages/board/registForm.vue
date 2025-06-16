<template>
    <div class="container-fluid main-container">
		<div class="row h-100">
			<main class="main-area">
				<h2>게시물 등록</h2>
				<div class="card-body">
					<form id="regist" @submit.prevent="regist">
						<div class="mb-3">
							<label for="title" class="form-label">제목</label> 
							<input type="text" class="form-control"  v-model="form.title" name="title" id="title" placeholder="제목을 입력하세요" required>
						</div>
						<template v-if="!memberStore.authenticated">
							<div class="row mb-3">
								<div class="col-md-6">
									<label for="writer" class="form-label">작성자 ID</label>
									<input type="text" class="form-control"  v-model="form.writer" name="writer" id="writer" required>
								</div>
								<div class="col-md-6">
									<label for="passwd" class="form-label">비밀번호</label> 
									<input type="password" class="form-control"  v-model="form.passwd" name="passwd" id="passwd" required>
								</div>
							</div>
						</template>

						<div class="mb-4">
							<label for="content" class="form-label">내용</label>
							<textarea class="form-control"  v-model="form.content" name="content" id="content" rows="10" style="resize: none; overflow: auto; max-height: 300px;" required></textarea>
						</div>

						<div class="d-flex justify-content-end">
							<button type="submit" class="btn btn-primary">등록</button>
						</div>
					</form>
				</div>
			</main>
		</div>
	</div>
</template>

<script setup>
  import {reactive, onMounted} from 'vue'
  import axios from 'axios'
  import { useRouter } from 'vue-router'
  import { useMemberStore } from '@/stores/member'

  const router = useRouter()
  const memberStore = useMemberStore();

  // json으로 보내기 위한 form
  const form = reactive({
	title : '',
	writer : '',
	passwd : '',
	content : '',
  })

  // 게시물 등록 이벤트
  function regist() {
	const confirmRegist = confirm("게시물 등록하시겠습니까?")
	if (!confirmRegist) return

	axios.post('/api/board/regist', form)
	.then(res => {
		if (!res.data.error) {
			alert(res.data.message)
			router.push({ name: 'Board_List'}) 
		} else {
		alert(res.data.message)
		}
	})
	.catch(err => {
		console.error('등록 실패', err)
		alert('등록 중 오류가 발생했습니다.')
	})
}

// 로그인 되어있다면 form에 해당갑 집어 넣도록
onMounted(() => {
  if (memberStore.authenticated) {
    form.writer = memberStore.userid
	form.passwd = '';
  }
})
</script>