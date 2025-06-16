<template>
	<div class="container-fluid main-container">
		<div class="row h-100">
			<main class="main-area">
				<div class="container mt-5">
					<div class="card-header text-center">
						<h3 class="mb-0">회원 상세보기</h3>
					</div>
					<table class="table table-group-divider">
						<tbody>
							<tr>
								<th scope="row">아이디</th>
								<td>{{memberDB.userid}}</td>
							</tr>
							<tr>
								<th scope="row">이름</th>
								<td>{{memberDB.name}}</td>
							</tr>
							<tr>
								<th scope="row">생년월일</th>
								<td>{{memberDB.birthdate}}</td>
							</tr>
							<tr>
								<th scope="row">핸드폰</th>
								<td>{{memberDB.phone_no}}</td>
							</tr>
							<tr>
								<th scope="row">주소</th>
								<td>{{memberDB.roadaddress}}{{memberDB.jibunaddress}}</td>
							</tr>
							<tr>
								<th scope="row">상세주소</th>
								<td>{{memberDB.detail_add}}</td>
							</tr>
							<tr>
								<th scope="row">최근 로그인</th>
								<td>{{memberDB.loginTime}}</td>
							</tr>
							<template v-if="memberStore.supervisor === 'Y'">
								<tr>
									<th scope="row">탈퇴 여부</th>
									<td>{{memberDB.is_deleted}}</td>
								</tr>
								<tr>
									<th scope="row">탈퇴일</th>
									<td>{{memberDB.deleted_at}}</td>
								</tr>
							</template>
						</tbody>
					</table>
					<template v-if="memberDB.userid === memberStore.userid">
						<div class="mt-4 d-flex justify-content-center gap-3">
							<router-link :to="{name: 'Home'}"  class="btn btn-outline-primary">메인으로</router-link> 
							<router-link :to="{name: 'Member_UpdateForm',  query:  {userid: memberDB.userid}}" class="btn btn-primary">회원 수정</router-link> 
							<a id="unregist" @click="unregist" class="btn btn-danger">회원탈퇴</a>
						</div>
					</template>
				</div>
			</main>
		</div>
	</div>
</template>

<script setup>
	import {ref, onMounted} from 'vue'
	import { useRoute, useRouter } from 'vue-router'
	import axios from 'axios'
	import { useMemberStore } from '@/stores/member'
	import { logoutProcess } from '@/util/AuthUtil'

	const router = useRouter()
	const route = useRoute()
	const userid = route.query.userid
	const memberDB = ref({ list: [] })
	const memberStore = useMemberStore();

	// 페이지 구동시 자동 마운트
	onMounted(() => {
		axios.get('/api/member/detailView', { params: { userid }})
			.then(res => {
				memberDB.value = res.data.memberDB
			})
	})

	// 회원 탈퇴
	const unregist = () => {
		const confirmRegist = confirm("회원 탈퇴 하시겠습니까?")
		if (!confirmRegist) return

		axios.post('/api/member/unregister',{ userid: memberDB.value.userid, sotreUserid: memberStore.userid})
			.then(res => {
				console.log('응답 전체:', res);
			if (!res.data.error) {
				alert(res.data.message)
				// 성공하면 로그아웃하고 메인 화면으로로
				logoutProcess(() =>{
					router.push({name: 'Home'})
				})
			} else {
				alert(res.data.message)
			}
			})
			.catch(err => {
				alert('오류가 발생했습니다.')
				console.error('에러 전체:', err);
    console.error('에러 응답 본문:', err.response?.data);
			})
	}
</script>