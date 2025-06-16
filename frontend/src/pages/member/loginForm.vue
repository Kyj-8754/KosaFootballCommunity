<template>
    <div class="container-fluid main-container">
		<div class="row h-100">
			<main class="main-area d-flex justify-content-center">
				<div class="login-container mt-5">
					<h3 class="text-center mb-4">로그인</h3>
					<form @submit.prevent="login" id="login">
						<div class="mb-3">
							<label for="userid" class="form-label">아이디</label> 
							<input type="text" class="form-control" id="userid" v-model = "info.userid" required >
						</div>
						<div class="mb-3">
							<label for="passwd" class="form-label">비밀번호</label>
							<input type="password" class="form-control" id="passwd" v-model ="info.passwd" required>
						</div>
						<div class="d-flex justify-content-center gap-2">
						<input type="submit" class="btn btn-primary"  value="로그인">
						<router-link :to="{ name: 'Member_RegistForm' }" class="btn btn-secondary">회원가입</router-link>
						</div>
						<div class="d-flex justify-content-center gap-2">
							<button type="button" class="btn btn-link p-0 text-muted" @click="isModalOpen = true">아이디/비밀번호 찾기</button>
						</div>
					</form>
				</div>
				<FindMember v-if="isModalOpen" @close="isModalOpen = false" />
			</main>
		</div>
	</div>
</template>

<script setup>
	import { reactive, ref } from 'vue'
	import { useRoute, useRouter } from 'vue-router'
	import { Login } from '@/util/AuthUtil'

	
	const router = useRouter();
	const currentRoute = useRoute(); 
	const fromname = currentRoute.query.fromname;

	// 로그인 담아주는 form
	const info = reactive({userid : "", passwd : ""});

	// 성공시 콜백
	const successCallback = () => {
		if (fromname) router.push({ path: fromname})
		else router.push({name:'Home'})
	}
	// 실패시 콜백
	const faileCallback = () =>{
		alert('아이디 또는 비밀번호를 확인 해 주세요');
		info.passwd = "";
		document.getElementById("passwd")?.focus();
	}
	// 로그인 서비스 시작
	const login = () => {
		Login(info.userid, info.passwd, successCallback, faileCallback);
	}

	// 아이디, 비밀번호 찾는 로직 꺼내오기
	import FindMember from '@/pages/member/findMember.vue'
	const isModalOpen = ref(false)
</script>


<!-- 별도 css추가 -->
 <style>
    .login-container {
  width: 100%;
  max-width: 400px;
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
 </style>