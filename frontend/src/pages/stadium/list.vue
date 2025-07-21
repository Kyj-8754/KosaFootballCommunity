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
						<input type="text" name="searchValue" id="searchValue" v-model="pageResponse.searchValue" class="form-control form-control-sm w-auto"> 
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
							<h5 class="card-title">
								{{ item.svcnm }}
								<span v-if="item.avgRating !== null" style="font-size: 0.9em; color: #f5a623; margin-left: 6px;">
									⭐ {{ item.avgRating.toFixed(1) }} ({{ item.reviewCount || 0 }})
								</span>
							</h5>
							<p class="card-text"><strong>지역명:</strong> {{ item.areanm }}</p>
							<p class="card-text"><strong>장소명:</strong> {{ item.placenm }}</p>
							<p class="card-text"><strong>연락처:</strong> {{ item.telno }}</p>
							<p class="card-text"><strong>가격:</strong> {{ item.price }}원</p>
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
import { useStadiumList } from '@/utils/script/stadium'

const {
  searchType,
  pageResponse,
  pageRange,
  searchTypes,  
  searchID,
  makeUrl,
  resetSearch
} = useStadiumList()
</script>




<style scoped>
	@import "@/utils/css/stadium_style.css";
</style>