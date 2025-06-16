<template>
    <div class="card card-body">
        <h2>Home</h2>
    </div>
    <div class="container-fluid main-container">
		<div class="row h-100">
			<main class="main-area">
				<h1><Strong>환영 합니다.</Strong></h1>
				<div>
            <p>현재 시간: {{ serverTime }}</p>
        </div>
        <div class="table-responsive">
          <table class = "table table-striped table-hover table-bordered mt-4">
            <thead class="table-dark text-center">
              <tr>
                <th>글번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>등록일</th>
                <th>조회수</th>
              </tr>
            </thead>
            <tbody class="text-center">
              <tr v-for="(item, index) in pageResponse.list" :key="item.bno">
                  <td>{{ pageResponse.totalCount - index - (pageResponse.pageNo - 1) * pageResponse.size }}</td>
                  <td class="text-truncate" style="max-width: 100%;">
                    <router-link :to="{name: 'Board_DetailView', query: {bno: item.bno}}" class="d-inline-block w-100"> {{item.title}} </router-link>
                  </td>
                  <td>{{item.writer}}</td>
                  <td>{{item.reg_date}}</td>
                  <td>{{item.view_count}}</td>
              </tr>
            </tbody>
          </table>
        </div>
			</main>
		</div>
	</div>
</template>
<script setup>
  import {ref, onMounted} from 'vue'
  import axios from 'axios'

  const serverTime = ref('')
  const pageResponse = ref({ list: [] })

  onMounted(() => {
    axios.get('/api/').then(res => {
      serverTime.value = res.data.serverTime
      pageResponse.value = res.data.pageResponse
    })
  })
</script>