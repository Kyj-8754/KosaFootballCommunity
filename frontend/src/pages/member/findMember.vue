<template>
  <div class="modal-backdrop">
    <div class="modal-content">
        <template v-if="mode === 'findId'">
		[아이디 찾기] 
      <label class="form-label">이름</label>
      <input type="text" class="form-control" v-model="findIdForm.name" />

      <label class="form-label">전화번호</label>
      <input type="text" class="form-control" v-model="findIdForm.phone_no" />

      <button @click="findId">아이디 찾기</button>

      <hr/>

      <label class="form-label">[비밀번호 찾기] 아이디</label>
      <input type="text" class="form-control" v-model="findPwForm.userid" />

      <label class="form-label">이름</label>
      <input type="text" class="form-control" v-model="findPwForm.name" />

      <label class="form-label">전화번호</label>
      <input type="text" class="form-control" v-model="findPwForm.phone_no" />

      <button @click="findPw">비밀번호 찾기</button>
      </template>

      <template v-else-if="mode === 'showId'">
        <p>{{ memberid }}</p>
        <button @click="goTofindMember">비밀번호 찾기</button>
      </template>

      <template v-else-if="mode === 'findPw'">
        <p>새 비밀번호 입력</p>
       <label class="form-label">새 비밀번호</label>
      <input type="password" class="form-control" v-model="findRePwForm.repasswd" />

      <label class="form-label">비밀번호 재 확인</label>
      <input type="password" class="form-control" v-model="findRePwForm.repasswd2" />

	  <button @click="RePassword">확인</button>
      </template>
      <button @click="closeModal">닫기</button>
    </div>
  </div>
</template>


<script setup>
	import { ref, reactive } from 'vue';
	import axios from 'axios'

	const mode = ref('findId')  // 기본은 '아이디 찾기'
	const emit = defineEmits(['close']); // 모달창 닫기

	const memberid = ref()

	// 아이디 찾기용 폼
	const findIdForm = reactive({
	name: '',
	phone_no: ''
	});

	// 비밀번호 찾기용 폼
	const findPwForm = reactive({
	userid: '',
	name: '',
	phone_no: ''
	});

	// 비밀번호 재설정용
	const findRePwForm = reactive({
		userid: '',
		repasswd: '',
		repasswd2: ''
	});


	// 핸드폰 입력 검사
	function validatePhoneNo(value) {
		const pwRegex = /^01[016]\d{8}$/;
		return pwRegex.test(value);
	}



	// 아이디 찾는 로직
	const findId = () =>{
		const username = findIdForm.name;
		const phone_no = findIdForm.phone_no;
		
		if(username.length == 0){
			alert("이름을 입력해주세요.");
			userid.focus();
			return;
		}

		if(!validatePhoneNo){
			alert("핸드폰 번호를 확인해주세요.");
			phone_no.focus();
			return;
		}

		axios.post('/api/member/findMemberId', { username, phone_no })
		.then(res => {
		if (res.data.existUserId) {
			memberid.value = res.data.userid
			mode.value = 'showId'
		} else {
			alert("아이디가 존재하지 않습니다.")
		}
		})
		.catch(err => {
		console.error('오류', err)
		alert('오류가 발생했습니다.')
		})
	}


	// 비밀번호 찾는 로직
	const findPw = () =>{

		const username = findPwForm.name;
		const phone_no = findPwForm.phone_no;
		const userid = findPwForm.userid;
		
		if(username.length == 0){
			alert("이름을 입력해주세요.");
			userid.focus();
			return;
		}

		if(!validatePhoneNo){
			alert("핸드폰 번호를 확인해주세요.");
			phone_no.focus();
			return;
		}
		

		axios.post('/api/member/findMemberId', { username, phone_no, userid })
		.then(res => {
		if (res.data.existUserId) {
			memberid.value = res.data.userid
			mode.value = 'findPw'
		} else {
			alert("유저가 존재하지 않습니다.")
		}
		})
		.catch(err => {
		console.error('오류', err)
		alert('오류가 발생했습니다.')
		})
	}


	// 비밀번호 검사 같은지 검사
	const checkPasswordMatch = (repasswd, repasswd2) => {
		return value.length > 0 && value2.length > 0 && value === value2;
	}


	// 비밀번호 재설정 로직
	const RePassword = () =>{
		
		const userid = findPwForm.userid;
		const repasswd = findRePwForm.repasswd;
		const repasswd2 = findRePwForm.repasswd2;
		
		if(!checkPasswordMatch) {
			alert("비밀번호를 확인해주세요."); 
			return;
		}

		axios.post('/api/member/reMemberPw', {userid, repasswd, repasswd2})
		.then(res => {
			if (res.data.existUserId) {
				alert("다시 로그인 해주세요")
				closeModal()
			} else {
				alert("재설정에 실패했습니다.")
			}
		})
		.catch(err => {
			console.error('오류', err)
			alert('오류가 발생했습니다.')
		})
	}


// 내부 전환 함수
const goTofindMember = () => mode.value = 'findId'; // 아이디 찾기
const closeModal = () => emit('close'); // 모달창 닫기
</script>

<style lang="css">
.modal-backdrop { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); }

.modal-content { background: white; padding: 2rem; margin: 10% auto; width: 400px; }
</style>