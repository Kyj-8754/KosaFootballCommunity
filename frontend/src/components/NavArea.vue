<template>
	<nav class="nav-area d-flex flex-column">
		<ul class="nav flex-column">
			<li class="nav-item"><router-link to="/board/list">게시판 목록</router-link></li>
			<li class="nav-item"><router-link to="/board/registForm">게시판 등록</router-link></li>
		</ul>
	</nav>
</template>

<style>
	.nav-area {
	background-color: #f8f9fa;
	height: 100%;
	padding: 20px;
	box-shadow: 2px 0 5px rgba(0,0,0,0.1); /* 오른쪽에 약간 그림자 */
	}
</style>