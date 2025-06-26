<template>
  <div class="card card-body">
    <h2>Home</h2>
  </div>

  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <h1><strong>환영 합니다.</strong></h1>

        <!-- ✅ 이동 버튼 -->
        <div class="mb-3">
          <router-link to="/recruitBoard" class="btn btn-outline-success me-2">팀원 모집 게시판</router-link>
          <router-link to="/club" class="btn btn-outline-info me-2">클럽 순위</router-link>

          <!-- ✅ 로그인 상태에 따라 경로 달라지는 버튼 -->
          <button class="btn btn-outline-primary me-2" @click="goToClubCreate">클럽 생성하기</button>
        </div>

        <button @click="test">구장 리스트 불러오기</button>
        <button @click="update">구장 업데이트</button>

        <div>
          <p>현재 시간: {{ serverTime }}</p>
        </div>

        <div class="table-responsive">
          <table class="table table-striped table-hover table-bordered mt-4">
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
                  <router-link :to="{ name: 'Board_DetailView', query: { bno: item.bno } }" class="d-inline-block w-100">
                    {{ item.title }}
                  </router-link>
                </td>
                <td>{{ item.writer }}</td>
                <td>{{ item.reg_date }}</td>
                <td>{{ item.view_count }}</td>
              </tr>
            </tbody>
          </table>
        </div>

      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

// ✅ 전역 token 주입 및 라우터 사용
const token = inject('token')
const router = useRouter()

// ✅ 서버 시간 및 게시글 리스트 데이터
const serverTime = ref('')
const pageResponse = ref({ list: [] })

onMounted(() => {
  axios.get('/api/').then(res => {
    serverTime.value = res.data.serverTime
    pageResponse.value = res.data.pageResponse
  })
})

// ✅ 버튼 동작 함수: 로그인 여부에 따라 분기
function goToClubCreate() {
  if (token?.value) {
    // 로그인 상태 → 클럽 등록 페이지로 이동
    router.push('/club/registForm')
  } else {
    // 비로그인 상태 → 로그인 화면으로 이동
    alert('클럽 생성을 하려면 로그인해야 합니다.')
    router.push('/member/loginForm') 
  }
}

function test() {
  axios.post('/api/stadium/test')
    .then(res => {
      console.log('데이터 받음:', res.data)
    })
    .catch(err => {
      console.error('API 호출 실패:', err)
    })
}

function update() {
  axios.post('/stadium_api/stadium/update')
    .then(res => {
      console.log('업데이트 시작')
    })
    .catch(err => {
      console.error('API 호출 실패:', err)
    })
}
</script>
