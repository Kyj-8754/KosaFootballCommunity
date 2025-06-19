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
							<tr>
								<th scope="row">탈퇴 여부</th>
								<td>{{memberDB.is_deleted}}</td>
							</tr>
							<tr>
								<th scope="row">탈퇴일</th>
								<td>{{memberDB.deleted_at}}</td>
							</tr>
						</tbody>
					</table>
					<div class="mt-4 d-flex justify-content-center gap-3">
						<router-link :to="{name: 'Home'}"  class="btn btn-outline-primary">메인으로</router-link> 
						<router-link :to="{name: 'Member_UpdateForm',  query:  {userid: memberDB.userid}}" class="btn btn-primary">회원 수정</router-link> 
						<a id="unregist" @click="unregist" class="btn btn-danger">회원탈퇴</a>
					</div>
				</div>
			</main>
		</div>
	</div>
</template>

<script setup>
	import {ref, onMounted} from 'vue'
	import { useRoute, useRouter } from 'vue-router'
	import axios from 'axios'

	const router = useRouter()
	const route = useRoute()
	const userid = route.query.userid
	const memberDB = ref({ list: [] })

	// 페이지 구동시 자동 마운트
	onMounted(() => {
		axios.get('/api/member/detailView', { params: { userid }})
			.then(res => {
				memberDB.value = res.data.memberDB
			})
	})
</script>