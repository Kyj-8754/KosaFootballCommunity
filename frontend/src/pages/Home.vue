<template>
  <div class="card card-body">
    <h2>Home</h2>
  </div>

  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <!--
        <h1><strong>환영 합니다.</strong></h1>


        <div class="mb-3">
          <button class="btn btn-outline-info me-2" @click="goToClubMatchList">
            클럽 매치 참가 리스트
          </button>
        </div>
        <button @click="test">구장 리스트 불러오기</button>
        <button @click="update">구장 업데이트</button>
        -->
        <!--
        <weatherWidget/>
        -->
        <BoardCategoryTabs />
        <RecentMatchList />
        <OldMatchList />
       
        <!-- <clubMatchList :clubId="4"/> -->
       
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const token = inject("token");
const router = useRouter();

import weatherWidget from "@/components/widget/weatherWidget.vue";
import BoardCategoryTabs from "@/components/main/BoardCategoryTabs.vue";
import RecentMatchList from "@/components/main/RecentMatchList.vue";
import OldMatchList from "@/components/main/OldMatchList.vue";
//import clubMatchList from '@/components/board/club/clubMatchList.vue'

const serverTime = ref("");

const pageResponse = ref({
  list: [],
  totalCount: 0,
  pageNo: 1,
  size: 10,
});

onMounted(() => {
  axios.get("/api/").then((res) => {
    serverTime.value = res.data.serverTime;
    if (res.data.pageResponse && Array.isArray(res.data.pageResponse.list)) {
      pageResponse.value = {
        ...pageResponse.value,
        ...res.data.pageResponse,
      };
    } else {
      console.warn("API 응답에 pageResponse 또는 pageResponse.list가 없습니다");
    }
  });
});

function test() {
  axios.post("/stadium_api/stadium/test");
}

function update() {
  axios
    .post("/stadium_api/stadium/update")
    .then((res) => {})
    .catch((err) => {
      console.error("API 호출 실패:", err);
    });
}
</script>

<style scoped>
.main-area {
  display: flex;       /* 또는 display: flex; flex-direction: column; */
  flex-direction: column;  /* 수직 정렬 시 */
  gap: 16px;           /* 자식들 사이의 간격 */
}
</style>