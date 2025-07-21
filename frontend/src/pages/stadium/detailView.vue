<template>
	<div class="container-fluid main-container">
		<div class="row h-100">
			<main class="main-area">
				<h2>구장 상세페이지</h2>
			<div class="container my-4">
				<div class="row g-4 align-items-start">
					<!-- 좌측: 이미지 -->
					<div class="col-md-8 d-flex justify-content-center align-items-center">
						<img :src="stadiumDB.img_PATH" class="img-fluid border rounded shadow-sm" 
						style="max-width: 100%; max-height: 500px; aspect-ratio: 4/3; object-fit: cover;" />
					</div>

					<!-- 우측: 달력 + 가격 -->
					<div class="col-md-4">
						<div class="mb-3 d-flex justify-content-between align-items-center">
							<h5 class="mb-0 fw-bold">이용 요금</h5>
							<span class="fs-4 fw-bold text-primary">{{ stadiumDB.price }}원</span>
						</div>
					</div>
				</div>
				<!-- 상단 버튼들 -->
				<div class="mt-auto d-flex justify-content-end gap-2">
					<button class="btn btn-secondary btn-sm" @click="goToList">목록</button>
					<button class="btn btn-dark btn-sm" @click="goToReservation">예약</button>
				</div>
				<!-- 탭 버튼 -->
				<div class="d-flex gap-2 justify-content-center border-bottom mb-3">
					<button class="btn" :class="{ 'btn-primary': activeTab === 'overview' }" @click="activeTab = 'overview'">이용 안내</button>
					<button class="btn" :class="{ 'btn-primary': activeTab === 'detail' }" @click="activeTab = 'detail'">알림</button>
					<button class="btn" :class="{ 'btn-primary': activeTab === 'map' }" @click="activeTab = 'map'">지도</button>
					<button class="btn" :class="{ 'btn-primary': activeTab === 'review' }" @click="activeTab = 'review'">리뷰</button>
				</div>
				<!-- 탭 콘텐츠 -->
				<div class="tab-content">
					<!-- 이용 안내 -->
					<div v-show="activeTab === 'overview'">
						<p v-html="stadiumDB.dtlcont"></p>
					</div>
					<!-- 알림 -->
					<div v-show="activeTab === 'detail'">
						<div v-if="stripHtml(stadiumDB.notice).trim() !== ''">
							<p v-html="stadiumDB.notice"></p>
						</div>
						<div v-else>
							<p class="text-muted">등록된 알림이 없습니다.</p>
						</div>
					</div>
					<!-- 지도 -->
					<div v-show="activeTab === 'map'">
						<div id="map" style="width:100%;height:350px;"></div>
						<div>
							주소 : {{ stadiumDB.adres }}
						</div>
						<div>
							전화 번호 : {{ stadiumDB.telno }} 
						</div>
					</div>
					<!-- 리뷰 -->
					<div v-show="activeTab === 'review'">
						<StadiumComment :SVCID ="SVCID" :userId ="userId" :userName ="userName"/>
    				</div>
  				</div>
			</div>
			</main>
		</div>
	</div>
</template>

<style scoped>
/* 예약 가능 날짜 (연한 파랑) */
.available-date {
  background-color: #e5f1ff !important;
  color: #0d6efd;
  border-radius: 50%;
}

.selected-date{
	background-color: black !important;
	color: white !important;
	border-radius: 50%;
	font-weight: bold;
	box-shadow: 0 0 0 2px white, 0 0 0 4px #0d6efd; /* 시각적 강조 */
}
</style>

<script setup>
	import { stdaiumDetail } from '@/utils/script/stadium'
	import  StadiumComment  from '@/components/comment/comment.vue'


	const {
		SVCID,
		activeTab,
		stadiumDB,
		userId,
		userName,
		goToList,
		goToReservation,
		stripHtml,
	} = stdaiumDetail()
</script>



