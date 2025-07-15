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
      >
        <!-- 왼쪽: 날짜 및 제목 -->
        <div @click="goToMatchDetail(match.match_id)" style="cursor: pointer;">
          <div class="fw-bold">
            {{ formatDate(match.match_date) }} {{ formatTime(match.match_date) }}
          </div>
          <div>{{ match.match_title }}</div>
        </div>

        <!-- 오른쪽: 상태 뱃지 + 버튼 -->
        <div class="d-flex align-items-center" style="gap: 12px; margin-right: 1rem;">
          <span
            class="badge rounded-pill d-flex align-items-center"
            :class="{
              'bg-primary': match.match_status === 'active',
              'bg-warning text-dark': match.match_status === 'waiting',
              'bg-secondary': match.match_status === 'completed' || match.match_status === 'cancelled'
            }"
            style="height: 30px; padding: 0 12px; font-size: 0.875rem;"
          >
            {{ getStatusLabel(match.match_status) }}
          </span>

          <button
            v-if="!match.applied"
            @click.stop="applyToMatch(match.match_id)"
            class="btn btn-outline-success btn-sm"
            style="height: 30px; min-width: 80px;"
          >
            참가
          </button>
          <button
            v-else
            @click.stop="cancelMatch(match.match_id)"
            class="btn btn-outline-danger btn-sm"
            style="height: 30px; min-width: 80px;"
          >
            참가 취소
          </button>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted, computed, inject } from "vue";
import axios from "axios";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();
const userNo = inject("userNo");
const matches = ref([]);

const isActiveTab = (path) => route.path === path;
const goToMatchDetail = (matchId) =>
  router.push(`/match/matchdetail/${matchId}`);

const applyToMatch = async (matchId) => {
  try {
    await axios.post("/board_api/match/apply/approve", {
      match_id: matchId,
      club_id: null, // 필요 시 props로 club_id 넘기기
      user_no: userNo.value,
    });
    fetchMatches();
  } catch (err) {
    console.error("❌ 참가 실패:", err);
  }
};

const cancelMatch = async (matchId) => {
  try {
    await axios.delete("/board_api/match/cancel", {
      params: {
        matchId,
        userNo: userNo.value,
      },
    });
    fetchMatches();
  } catch (err) {
    console.error("❌ 취소 실패:", err);
  }
};

const checkUserApplied = async (matchId) => {
  try {
    const res = await axios.get("/board_api/match/applied", {
      params: {
        matchId,
        userNo: userNo.value,
      },
    });
    return res.data === true;
  } catch (err) {
    console.error("❌ 신청 여부 확인 실패:", err);
    return false;
  }
};

const fetchMatches = async () => {
  try {
    const res = await axios.get("/match/league/closed");
    const rawMatches = Array.isArray(res.data) ? res.data : res.data.data || [];

    const withApplied = await Promise.all(
      rawMatches.map(async (match) => {
        const applied = await checkUserApplied(match.match_id);
        return { ...match, applied };
      })
    );

    matches.value = withApplied;
  } catch (err) {
    console.error("❌ 매치 목록 불러오기 실패:", err);
  }
};

const filteredMatches = computed(() => {
  return matches.value.sort(
    (a, b) => new Date(a.match_date) - new Date(b.match_date)
  );
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
    case "completed":
      return "진행 완료";
    case "cancelled":
      return "취소됨";
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
