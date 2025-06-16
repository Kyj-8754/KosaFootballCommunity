<template>
    <div class="container-fluid main-container">
		<div class="row h-100">
			<main class="main-area d-flex justify-content-center">
				<div class="container d-flex justify-content-center align-items-center min-vh-100">
				<div class="card shadow p-4 rounded w-100" style="max-width: 600px;">
					<h1 class="text-center mb-4">회원 정보 수정</h1>
					<form class="row g-3 needs-validation"  @submit.prevent="update" name="update" id="update" novalidate>
						<div class="mb-3">
							<label for="validationuserid" class="form-label">아이디</label>
							<div class="input-group has-validation">
								<input type="text" class="form-control" v-model="form.userid" name="userid" id="userid" aria-describedby="inputGroupPrepend" readonly="readonly">
							</div>
						</div>
						<div class="mb-3">
							<label for="validationpasswd" class="form-label">비밀번호</label>
							<div class="input-group has-validation">
								<input type="password" class="form-control"  v-model="form.passwd" name="passwd" id="passwd" aria-describedby="inputGroupPrepend" required>
								<div class="invalid-feedback" id="passwdFeedback">
									기존 비밀번호를 입력해 주세요.
								</div>
							</div>
						</div>
						<div class="mb-3">
							<label for="validationNewpasswd" class="form-label">새 비밀번호</label>
							<div class="input-group has-validation">
								<input type="password" class="form-control" v-model="form.newPasswd" :class="PasswdValid ? 'is-valid' : 'is-invalid'" name="newPasswd" id="newPasswd" aria-describedby="inputGroupPrepend" required>
									<div class="invalid-feedback" id="passwdFeedback">
										비밀번호는 8자 이상이며, 영문자/숫자/특수문자를 포함해야 합니다.
									</div>
							</div>
						</div>
						<div class="mb-3">
							<label for="validationNewpasswd2" class="form-label">새 비밀번호확인</label>
							<div class="input-group has-validation">
								<input type="password" class="form-control" v-model="form.newPasswd2" :class="passwdMatch ? 'is-valid' : 'is-invalid'" name="newPasswd2" id="newPasswd2" aria-describedby="inputGroupPrepend" required>
								<div class="invalid-feedback" id="passwd2Feedback">비밀번호가 같지 않습니다.</div>
							</div>
						</div>
						<div class="mb-3">
							<label for="validationName" class="form-label">이름</label>
							<div class="input-group has-validation">
								<input type="text" class="form-control" v-model="form.name" name="name" id="name" aria-describedby="inputGroupPrepend" readonly="readonly">
							</div>
						</div>
						<div class="d-flex justify-content-center gap-2">
							<button class="btn btn-primary" type="submit">수정</button>
						</div>
					</form>
				</div>
			</div>
			</main>
		</div>
	</div>
</template>

<script setup>
	import {ref, onMounted, computed, reactive} from 'vue'
	import { useRoute, useRouter } from 'vue-router'
	import axios from 'axios'
	import { logoutProcess } from '@/util/AuthUtil'

	const router = useRouter()
	const route = useRoute()
	const userid = route.query.userid // 경로의 매개변수 받아오기

	const memberDB = ref({ })
	const form = reactive({
		userid: '',
		passwd: '',
		newPasswd: '',
		newPasswd2: '',
		name: '',
	})	// 보내는용도로 만든 객체

	onMounted(() => {
		axios.get('/api/member/detailView', { params: { userid }})
		.then(res => {
			memberDB.value = res.data.memberDB
			form.userid = memberDB.value.userid
			form.name = memberDB.value.name
		})
	}) // api에서 DB에 값 불러오기


	//비밀번호 검사 로직 (영문자, 숫자, 특수문자는 1개 이상, 최소 길이 8자)
	const validatePassword = (value) => {
		const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+=-]).{8,}$/;
		return pwRegex.test(value);
	}
 
	// 비밀번호 검사 같은지 검사 로직
	const checkPasswordMatch = (value, value2) => {
		return value.length > 0 && value2.length > 0 && value === value2;
	}
	
	// 실시간 검사하도록 computed
	const PasswdValid = computed(() => validatePassword(form.newPasswd))
	const passwdMatch = computed(() => checkPasswordMatch(form.newPasswd, form.newPasswd2))



const update = () => {
  const confirmRegist = confirm("회원 수정 하시겠습니까?")
  if (!confirmRegist) return

  if (!passwdMatch.value) {
		document.getElementById("newPasswd")?.focus();
		alert("새 비밀번호를 다시 확인해주세요.");
		return;
	}

	if (!PasswdValid.value) {
		document.getElementById("newPasswd2")?.focus();
		alert("새 비밀번호확인을 다시 확인해주세요.");
		return;
	}

  axios.post('/api/member/update', form)
    .then(res => {
      if (!res.data.error) {
		  alert(res.data.message)
		  // 성공하면 로그아웃하고 로그인화면으로 
		  logoutProcess(() =>{
			router.push({name: 'Member_LoginForm'})
		})
      } else {
        alert(res.data.message)
      }
    })
    .catch(err => {
      console.error('수정 실패', err)
      alert('오류가 발생했습니다.')
    })
	}
</script>