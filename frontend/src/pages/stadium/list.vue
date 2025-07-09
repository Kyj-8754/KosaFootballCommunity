<template>
    <div class="container-fluid main-container">
		<div class="row h-100">
			<main class="main-area">
			<h2>구 장</h2>
			<div class="p-3 bg-light rounded d-flex justify-content-center align-items-center gap-3 mb-4">
				<div class="d-flex align-items-center gap-3 mb-3">
					<div>
						<select name="searchType" id="searchType" v-model="searchType" class="form-select d-inline-block w-auto">
							<option v-for="type in searchTypes" :key="type.value" :value="type.value">
								{{ type.label }}
							</option>
						</select>
					<!-- 현재 페이지 번호 / 전체 페이지 번호 -->
					({{pageResponse.pageNo}}/{{pageResponse.totalPage}})
					</div>

					<form @submit.prevent="searchID" :name="searchID" :id="searchID" class="d-flex align-items-center gap-2">
						<label for="searchValue" class="me-1 mb-0">검색어:</label>
						<input type="text" :name="searchValue" :id="searchValue" v-model="pageResponse.searchValue" class="form-control form-control-sm w-auto"> 
						<button type="submit" class="btn btn-outline-secondary btn-sm">검색</button>
						<button type="button" @click="resetSearch" class="btn btn-primary btn-sm">초기화</button>
					</form>
				</div>
			</div>
			<div class="container mt-4">
				<div class="row row-cols-1 row-cols-md-3 g-4">
					<div class="col" v-for="(item, index) in pageResponse.list" :key="index">
						<router-link :to="`detailView?SVCID=${item.svcid}`">
						<div class="card h-100 shadow-sm">
							<img :src="item.img_PATH || '/default.jpg'" class="card-img-top" alt="stadium image" />
							<div class="card-body">
							<h5 class="card-title">{{ item.svcnm }}</h5>
							<p class="card-text"><strong>지역명:</strong> {{ item.areanm }}</p>
							<p class="card-text"><strong>장소명:</strong> {{ item.placenm }}</p>
							<p class="card-text"><strong>연락처:</strong> {{ item.telno }}</p>
							<p class="card-text"><strong>가격:</strong> {{ item.price }}</p>
							<p class="card-text"><strong>이용 시간:</strong> {{ item.v_MIN }} ~ {{ item.v_MAX }}</p>
							</div>
						</div>
						</router-link>
					</div>
				</div>
			</div>
			<!-- 페이지 처리 -->
				<div class="d-flex justify-content-center">
					<template v-if="pageResponse.prev">
						<router-link :to="makeUrl(pageResponse.startPage - 1)">이전</router-link>
						&nbsp;
					</template>

					<template v-for="pageNo in pageRange" :key="pageNo">
						<router-link :to="{name: 'Stadium_List', query:{pageNo, size: size,  searchValue: searchValue || undefined}}">
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
	import { computed, watch, reactive, ref, inject} from 'vue'
	import axios from 'axios'
	import { useRouter, useRoute } from 'vue-router'
	inject 
	const router = useRouter() // 보낼 경로
	const route = useRoute() // 현재 경로
	const searchTypes = [
		{ label: '전체', value: '' },
		{ label: '지역명', value: 'areanm' },
		{ label: '장소명', value: 'placenm' }
	] 

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

	// size와 searchType 변경시 감지하여 함수 발생
	const pageNo = computed(() => parseInt(route.query.pageNo) || 1)

	const searchType = ref('');
	
	// 페이징 기능, 누르면 해당 페이지 이동하도록 구현현
	const pageRange = computed(() => {
	  const range = []
	  for (let i = pageResponse.startPage; i <= pageResponse.endPage; i++) {
		range.push(i)
	  }
	  return range
	})
	
	watch([pageNo, searchType, () => route.query.searchValue], ([newPageNo, newSearchType, newSearch]) => {
		fetchData(newPageNo, newSearchType, newSearch)
	}, 
	{ immediate: true }
	)
	
	// 값 검색시 넘어가는 로직
	function searchID() {
		router.push({
			name: 'Stadium_List',
			query: {
			pageNo: 1, // 검색 시 1페이지로 이동
			searchType: searchType.value || '',
			searchValue: pageResponse.searchValue || ''
			}
		})
	}
	// 값 변경시 다시 list 가져오도록 요청
	function fetchData(pageNo, size, searchValue) {
		axios.get(`/stadium_api/stadium/list`,{
			params:{
				pageNo,
				searchType: searchType.value || '',
				searchValue: searchValue || ''}
			})
			.then(res => {
			Object.assign(pageResponse, res.data.pageResponse)
			});
	}

	// url 변경시 반응
	function makeUrl(pageNo) {
		const base = `list?pageNo=${pageNo}`
		const params = []

  if (searchType.value && searchType.value !== '전체') {
    params.push(`searchType=${encodeURIComponent(searchType.value)}`)
  }

  if (pageResponse.searchValue && pageResponse.searchValue.trim() !== '') {
    params.push(`searchValue=${encodeURIComponent(pageResponse.searchValue.trim())}`)
  }

  return params.length ? `${base}&${params.join('&')}` : base
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