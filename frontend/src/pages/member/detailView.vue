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

const fetchMemberDetail = async () => {
  try {
    const userNo = route.query.userno
		console.log(userNo);
    const res = await axios.get(`/login_api/mypage/detailView/${userNo}`)
		console.log('응답 결과:', res)
    member.value = res.data.member
  } catch (e) {
    console.error('회원 정보 로딩 실패:', e)
    member.value = null
  }
}

onMounted(fetchMemberDetail)
</script>