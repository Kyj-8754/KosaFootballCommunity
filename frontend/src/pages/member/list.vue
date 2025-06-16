<template>
    <div class="container-fluid main-container">
		<div class="row h-100">
			<main class="main-area">
				<h2>회원 관리</h2>
				<div class="d-flex align-items-center gap-3 mb-3">
				건수 :
				<select name="size" id="size" v-model="size" class="form-select d-inline-block w-auto">
					<option v-for="size in sizes" :key="size" :value="size">
						{{ size }}
					</option>
				</select>

				<!-- 현재 페이지 번호 / 전체 페이지 번호 -->
				({{pageResponse.pageNo}}/{{pageResponse.totalPage}})
				<form @submit.prevent="searchID" :name="searchID" :id="searchID" class="d-flex align-items-center gap-2">
					<label for="searchValue" class="me-1 mb-0">검색어:</label> 
					<input type="text" :name="searchValue" :id="searchValue" v-model="pageResponse.searchValue" class="form-control form-control-sm w-auto"> 
					<input type="submit" value="검색"  class="btn btn-outline-secondary btn-sm">
				</form>
				</div>

				<table class="table table-bordered table-hover text-center align-middle">
					<thead class="table table-striped table-hover table-bordered align-middle text-center">
						<tr>
							<th style="width: 5%;">번호</th>
							<th style="width: 12%;">아이디</th>
							<th style="width: 12%;">이름</th>
							<th style="width: 14%;">생년월일</th>
							<th style="width: 10%;">관리자 여부</th>
							<th style="width: 16%;">로그인 일시</th>
							<th style="width: 12%;">로그인 차단여부</th>
							<th style="width: 10%;">탈퇴여부</th>
							<th style="width: 14%;">탈퇴 날짜</th>
						</tr>
					</thead>
					<tbody>
						<tr v-for="(item, index) in pageResponse.list" :key="item">
							<td>{{ pageResponse.totalCount - index - (pageResponse.pageNo - 1) * pageResponse.size }}</td>
							<td>
								<router-link :to="`detailView?userid=${item.userid}`">{{item.userid}}</router-link>
							</td>
							<td>{{item.name}}</td>
							<td>{{item.birthdate}}</td>
							<td>{{item.supervisor}}</td>
							<td>{{item.loginTime?.substring(0, 10)}}</td>
							<td>
								<input type="checkbox" class="form-check-input ban-toggle" @change="toggleBan(item)" :data-userid="item.userid" :checked="item.fail_login == 5" :disabled="item.supervisor === 'Y'">
							</td>
							<td>{{item.is_deleted}}</td>
							<td>{{item.deleted_at?.substring(0, 10)}}</td>
						</tr>
					</tbody>
				</table>
				
		
	<!-- 페이지 처리 -->
	<div class="d-flex justify-content-center">
					<template v-if="pageResponse.prev">
						<router-link :to="makeUrl(pageResponse.startPage - 1)">이전 </router-link>
					</template>
					&nbsp;

					<template v-for="pageNo in pageRange" :key="pageNo">
						<router-link :to="{name: 'Member_List', query:{pageNo, size: size,  searchValue: searchValue || undefined}}">
							<template v-if="pageNo === pageResponse.pageNo">
								<b>{{ pageNo }}</b>
							</template>
							<template v-else>
								{{ pageNo }}
							</template>
						</router-link>
						&nbsp;
					</template>

					<template v-if="pageResponse.next">
						<router-link :to="makeUrl(pageResponse.endPage + 1)">다음</router-link>
					</template>
				</div>
		</main>
		</div>
	</div>
</template>

<script setup>
	import { computed, watch, reactive} from 'vue'
	import axios from 'axios'
	import { useRouter, useRoute } from 'vue-router'
	import { useMemberStore } from '@/stores/member'
	const MemberStore = useMemberStore() // 멤버 정보보
	const router = useRouter() // 보낼 경로
	const route = useRoute() // 현재 경로
	const sizes = [10, 30, 90, 100] // 건수 사이즈 
	// 초기값 설정
	const pageResponse = reactive ({ 
		list: [],
		endPage: 1,
		next: false,
		prev: false,
		startPage: 1,
		totalPage: 1,
		fail_login: 1,
		searchValue: "",
		totalCount: 1,
	})

	// size와 pagNo변경시 감지하여 함수 발생
	const pageNo = computed(() => parseInt(route.query.pageNo) || 1)
	const size = computed({
		get: () => parseInt(route.query.size) || 10,
		set: (val) => {
			router.push({
				name: 'Member_List',
				query: {
					...route.query,
					pageNo: 1, // 사이즈 바꾸면 1페이지로 초기화
					size: val
				}
			})
		}
	})
	
	const pageRange = computed(() => {
	  const range = []
	  for (let i = pageResponse.startPage; i <= pageResponse.endPage; i++) {
		range.push(i)
	  }
	  return range
	})
	
	watch([pageNo, size, () => route.query.searchValue], ([newPageNo, newSize, newSearch]) => {
		fetchData(newPageNo, newSize, newSearch)
	}, 
	{ immediate: true }
	)
	
	function searchID() {
		router.push({
			name: 'Member_List',
			query: {
			pageNo: 1, // 검색 시 1페이지로 이동
			size: size.value,
			searchValue: pageResponse.searchValue || null
			}
		})
	}
	// 값 변경시 다시 list 가져오도록 요청
	function fetchData(pageNo, size, searchValue) {
		axios.get(`/api/member/list?pageNo=${pageNo}&size=${size}&searchValue=${encodeURIComponent(searchValue || '')}`)
			.then(res => {
			Object.assign(pageResponse, res.data.pageResponse)
			})
	}

	// url 변경시 반응
	function makeUrl(pageNo) {
		const base = `list?pageNo=${pageNo}&size=${pageResponse.size}`
		return pageResponse.searchValue ? `${base}&searchValue=${encodeURIComponent(searchValue)}` : base
	}

	//밴 시스템 만들기
	const toggleBan = (item) => {
		// 원래 값 저장해서 혹여나 실패시 원래값으로 반환하도록
		const originalFailLogin = item.fail_login;
		const banned = originalFailLogin === 5 ? 'N' : 'Y';

		axios.post('/api/member/ban', {
			supervisor: MemberStore.supervisor,
			userid: item.userid,
			banned: banned
		})
		.then(res => {
			const result = res.data
			if (result.success) {
				alert(result.message)
				item.fail_login = banned === 'Y' ? 5 : 0
			} else {
				alert(result.message)
				item.fail_login = originalFailLogin;
			}
		})
		.catch(err => {
			console.error('ban 처리 중 오류:', err)
			alert('처리 중 오류가 발생했습니다.')
			item.fail_login = originalFailLogin;
		})
	};

</script>