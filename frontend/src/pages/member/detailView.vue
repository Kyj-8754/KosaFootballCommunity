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
							<tr><th class="text-start">이름</th><td class="text-start">{{ member.userName }}</td></tr>
							<tr><th class="text-start w-25">아이디</th><td class="text-start">{{ member.userId }}</td></tr>
						</tbody>
					</table>
					<div class="mt-4 d-flex justify-content-center gap-3">
						<router-link :to="{name: 'Home'}"  class="btn btn-outline-primary">신청내역</router-link>
						<router-link :to="{name: 'Home'}"  class="btn btn-outline-primary">사용 내역</router-link> 
						<router-link :to="{name: 'Home'}"  class="btn btn-outline-primary">친구</router-link> 
						<router-link :to="{name: 'Home'}"  class="btn btn-outline-primary">친구가 신청한 매치</router-link> 
						<router-link :to="{name: 'Member_UpdateForm',  query:  {userid: member.userid}}" class="btn btn-primary">프로필 수정</router-link> 
					</div>
				</div>
			</main>
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const member = ref(null)

	// 페이지 구동시 자동 마운트
	onMounted(() => {
		axios.get('/login_api/member/detailView', { params: { userid }})
			.then(res => {
				member.value = res.data.member
			})
	})
</script>