<template>
    <div class="container-fluid main-container">
		<div class="row h-100">
			<main class="main-area">
			<h2>게시물 목록</h2>
			<div class="d-flex align-items-center gap-3 mb-3">
				<div>
				건수 : 
				<select name="size" id="size" v-model="size" class="form-select d-inline-block w-auto">
					<option v-for="size in sizes" :key="size" :value="size">
						{{ size }}
					</option>
				</select>
				<!-- 현재 페이지 번호 / 전체 페이지 번호 -->
				({{pageResponse.pageNo}}/{{pageResponse.totalPage}})
				</div>

				<form @submit.prevent="searchID" :name="searchID" :id="searchID" class="d-flex align-items-center gap-2">
					<label for="searchValue" class="me-1 mb-0">검색어:</label>
					<input type="text" :name="searchValue" :id="searchValue" v-model="pageResponse.searchValue" class="form-control form-control-sm w-auto"> 
					<input type="submit" value="검색" class="btn btn-outline-secondary btn-sm">
				</form>
			</div>

			<table class="table table-striped table-hover table-bordered mt-4 table-fixed">
				<thead class="table-dark text-center">
					<tr>
						<th>글번호</th>
						<th style="width: 40%;">제목</th>
						<th >작성자</th>
						<th>등록일</th>
						<th>조회수</th>
						<template v-if="memberStore.supervisor === 'Y'">
						<th>삭제여부</th>
						<th>삭제일</th>
						</template>
					</tr>
				</thead>
				<tbody class="text-center">
						<tr v-for="(item,index) in pageResponse.list" :key="item">
							<td>{{ pageResponse.totalCount - index - (pageResponse.pageNo - 1) * pageResponse.size }}</td>
							<td class="cell">
								<router-link :to="`detailView?bno=${item.bno}`" :title="item.title">{{item.title}}</router-link>
							</td>
							<td class="cell">{{item.writer}}</td>
							<td>{{item.reg_date?.substring(0, 10)}}</td>
							<td>{{item.view_count}}</td>
							<template v-if="memberStore.supervisor === 'Y'">
								<td>{{item.is_deleted}}</td>
								<td>{{item.deleted_at?.substring(0, 10)}}</td>
							</template>
						</tr>
				</tbody>
			</table>
			<!-- 페이지 처리 -->
				<div class="d-flex justify-content-center">
					<template v-if="pageResponse.prev">
						<router-link :to="makeUrl(pageResponse.startPage - 1)">이전</router-link>
						&nbsp;
					</template>

					<template v-for="pageNo in pageRange" :key="pageNo">
						<router-link :to="{name: 'Board_List', query:{pageNo, size: size,  searchValue: searchValue || undefined}}">
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

	const memberStore = useMemberStore(); // 로그인한 유저 정보 체크
	const supervisor = memberStore.supervisor === 'Y' ? 'Y' : 'N'; // 관리자인지 체크
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
		searchValue: "",
		totalCount: 1,
	})

	// size와 pagNo변경시 감지하여 함수 발생
	const pageNo = computed(() => parseInt(route.query.pageNo) || 1)
	const size = computed({
		get: () => parseInt(route.query.size) || 10,
		set: (val) => {
			router.push({
				name: 'Board_List',
				query: {
					...route.query,
					pageNo: 1, // 사이즈 바꾸면 1페이지로 초기화
					size: val
				}
			})
		}
	})
	
	// 페이징 기능, 누르면 해당 페이지 이동하도록 구현현
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
	
	// 값 검색시 넘어가는 로직
	function searchID() {
		router.push({
			name: 'Board_List',
			query: {
			pageNo: 1, // 검색 시 1페이지로 이동
			size: size.value,
			searchValue: pageResponse.searchValue || null
			}
		})
	}
	// 값 변경시 다시 list 가져오도록 요청
	function fetchData(pageNo, size, searchValue) {
		axios.get(`/api/board/list`,{
			params:{pageNo,size,searchValue: searchValue || ''},
			headers: {
				'supervisor': supervisor
			}
			})
			.then(res => {
			Object.assign(pageResponse, res.data.pageResponse)
			});
	}

	// url 변경시 반응
	function makeUrl(pageNo) {
		const base = `list?pageNo=${pageNo}&size=${pageResponse.size}`
		return pageResponse.searchValue ? `${base}&searchValue=${encodeURIComponent(pageResponse.searchValue)}` : base
	}
</script>


<style scoped>
	.table-fixed {
		table-layout: fixed;
		word-break: break-word;
	}

	.cell {
		max-width: 200px;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
	}

	.cell a {
		display: inline-block;
		width: 100%;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
	}
</style>