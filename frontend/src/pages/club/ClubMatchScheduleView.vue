<template>
  <div class="container my-4">
    <h2 class="fw-bold mb-3">리그 일정</h2>

    <!-- 버튼 우측 정렬 -->
<div class="mb-3 text-end" style="margin-top: -25px">
  <router-link
    to="/recruitBoard"
    class="tab-btn me-2"
    :class="{ active: isActiveTab('/recruitBoard') }"
  >
    팀원 모집 게시판
  </router-link>
  <router-link
    to="/club"
    class="tab-btn me-2"
    :class="{ active: isActiveTab('/club') }"
  >
    클럽 순위
  </router-link>
</div>

    <!-- ✅ 매치 일정 리스트 출력 -->
    <div class="list-group">
      <div
        v-for="match in filteredMatches"
        :key="match.match_id"
        class="list-group-item d-flex justify-content-between align-items-center"
        @click="goToMatchDetail(match.match_id)"
        style="cursor: pointer">
        <div>
          <div class="fw-bold">
            {{ formatDate(match.match_date) }}
            {{ formatTime(match.match_date) }}
          </div>
          <div>{{ match.match_title }}</div><!-- 나중에 주소 바꿔야함 클럽 매치 신청으로-->
        </div>
        <div>
          <span
            class="badge rounded-pill"
            :class="{
              'bg-primary': match.match_status === 'active',
              'bg-warning text-dark': match.match_status === 'waiting',
            }"
          >
            {{ getStatusLabel(match.match_status) }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();
const matches = ref([]);

function isActiveTab(path) {
  return route.path === path;
}

// ✅ 👇 전역에 있어야 템플릿에서 접근 가능
const goToMatchDetail = (matchId) => {
  router.push(`/match/matchdetail/${matchId}`);
};

const fetchMatches = async () => {
  try {
    const res = await axios.get("/match/league/closed");

    console.log("✅ 서버 응답 (res.data):", res.data);

    const responseData = res.data;

    matches.value = Array.isArray(responseData)
      ? responseData
      : responseData.data || [];

    console.log("✅ matches.value (after parsing):", matches.value);
  } catch (err) {
    console.error("❌ 매치 목록 불러오기 실패:", err);
  }
};

// 여기서 조건 걸기 
const filteredMatches = computed(() => {
  return matches.value
    .sort((a, b) => new Date(a.match_date) - new Date(b.match_date)); 
});

const formatDate = (str) => {
  const date = new Date(str);
  return `${date.getMonth() + 1}월 ${date.getDate()}일`;
};

const formatTime = (str) => {
  const date = new Date(str);
  return `${date.getHours().toString().padStart(2, "0")}:00시`;
};

const getStatusLabel = (code) => {
  switch (code) {
    case "waiting":
      return "대기중";
    case "active":
      return "진행중";
    default:
      return code;
  }
};

onMounted(fetchMatches);
</script>

<style scoped>
.tab-btn {
  padding: 0.375rem 0.75rem;
  border: 1px solid #ced4da; /* 연한 회색 테두리 */
  border-radius: 0.25rem;
  background-color: white;
  color: black;
  text-decoration: none;
  font-weight: 500;
  transition: none;
}

.tab-btn:hover {
  background-color: white !important;
  color: black !important;
  border-color: #ced4da !important;
  box-shadow: none !important;
}

.tab-btn.active {
  background-color: white !important;
  color: black !important;
  border-color: black !important;
}
</style>
