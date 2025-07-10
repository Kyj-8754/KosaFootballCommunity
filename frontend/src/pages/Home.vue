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
          <button class="btn btn-outline-primary me-2" @click="goToClubCreate">클럽 생성하기</button>
        </div>

        <button @click="test">구장 리스트 불러오기</button>
        <button @click="update">구장 업데이트</button>
        <weatherWidget/>
        <BoardCategoryTabs />
        <RecentMatchList />
        <OldMatchList />
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
              <tr 
                v-if="pageResponse && Array.isArray(pageResponse.list)"  
                v-for="(item, index) in pageResponse.list" 
                :key="item.bno">
                <td>
                  {{ 
                    (pageResponse.totalCount || 0) - index 
                    - ((pageResponse.pageNo || 1) - 1) * (pageResponse.size || 10)
                  }}
                </td>
                <td class="text-truncate" style="max-width: 100%;">
                  <router-link :to="{ name: 'Board_DetailView', query: { bno: item.bno } }" class="d-inline-block w-100">
                    {{ item.title }}
                  </router-link>
                </td>
                <td>{{ item.writer }}</td>
                <td>{{ item.reg_date }}</td>
                <td>{{ item.view_count }}</td>
              </tr>
              <tr v-else>
                <td colspan="5">게시글이 없습니다.</td>
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

const token = inject('token')
const router = useRouter()

import weatherWidget from '@/components/widget/weatherWidget.vue'
import BoardCategoryTabs from '@/components/main/BoardCategoryTabs.vue'
import RecentMatchList from '@/components/main/RecentMatchList.vue'
import OldMatchList from '@/components/main/OldMatchList.vue'

const serverTime = ref('')

const pageResponse = ref({ 
  list: [],
  totalCount: 0,
  pageNo: 1,
  size: 10
}) 

onMounted(() => {
  axios.get('/api/').then(res => {
    serverTime.value = res.data.serverTime
    if (res.data.pageResponse && Array.isArray(res.data.pageResponse.list)) {
      pageResponse.value = {
        ...pageResponse.value,
        ...res.data.pageResponse
      }
    } else {
      console.warn('API 응답에 pageResponse 또는 pageResponse.list가 없습니다')
    }
  })
})

  function test() {
  axios.post('/stadium_api/stadium/test')
  }

function goToClubCreate() {
  if (token?.value) {
    router.push('/club/registForm')
  } else {
    alert('클럽 생성을 하려면 로그인해야 합니다.')
    router.push('/member/loginForm') 
  }
}

function update() {
  axios.post('/stadium_api/stadium/update')
    .then(res => {
    })
    .catch(err => {
      console.error('API 호출 실패:', err)
    })
}
</script>
