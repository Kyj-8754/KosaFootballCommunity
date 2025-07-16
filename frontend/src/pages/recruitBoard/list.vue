<template>
  <div class="container my-5">
    <h2 class="fw-bold mb-3">모집 게시판</h2>

    <!-- 버튼 우측 정렬 -->
    <div class="d-flex justify-content-end mb-3">
      <router-link
        to="/club/clubMatchSchedule"
        class="tab-btn me-2"
        :class="{ active: isActiveTab('/club/clubMatchSchedule') }"
      >
        리그 일정
      </router-link>
      <router-link
        to="/club"
        class="tab-btn"
        :class="{ active: isActiveTab('/club') }"
      >
        클럽 순위
      </router-link>
    </div>

    <!-- 버튼 + 검색/정렬 한 줄 -->
    <div class="mb-3 d-flex justify-content-between align-items-center">
      <!-- 왼쪽: 등록/클럽생성 버튼 한 줄로 -->
      <div class="d-flex align-items-center">
        <router-link
          v-if="hasClub"
          to="/recruitBoard/new"
          class="btn btn-primary btn-sm me-2"
        >
          등록하기
        </router-link>
        <button class="btn btn-outline-primary btn-sm" @click="goToClubCreate">
          클럽 생성하기
        </button>
      </div>
      <!-- 오른쪽: 검색/정렬 -->
      <div class="d-flex align-items-center">
        <input
          type="text"
          v-model="searchTeam"
          placeholder="팀명 검색"
          class="form-control form-control-sm me-2"
          style="width: 200px"
        />
        <button
          class="btn btn-outline-primary btn-sm me-2"
          @click="fetchRecruits()"
        >
          최신순
        </button>
        <button
          class="btn btn-outline-secondary btn-sm"
          @click="fetchRecruits('popular')"
        >
          인기순
        </button>
      </div>
    </div>

    <!-- 모집글 목록 -->
    <div v-if="filteredRecruits.length === 0" class="alert alert-warning">
      등록된 모집글이 없습니다.
    </div>
    <ul class="list-group border rounded" style="overflow: hidden">
      <router-link
        v-for="recruit in filteredRecruits"
        :key="recruit.bno"
        :to="`/recruitBoard/${recruit.bno}`"
        tag="li"
        class="list-group-item d-flex justify-content-between align-items-center text-dark text-decoration-none"
        style="
          cursor: pointer;
          border-bottom: 1px solid #e0e0e0;
          background: #fff;
          min-height: 58px;
        "
      >
        <!-- 클럽명 -->
        <div
          class="fw-bold me-3"
          style="
            width: 30%;
            display: flex;
            align-items: center;
            padding-left: 18px;
            height: 100%;
          "
        >
          {{ recruit.club_name }}
        </div>

        <!-- 모집글 제목 및 기타 정보 (정확하게 세로 중앙!) -->
        <div
          class="flex-grow-1"
          style="
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding-left: 30px;
            gap: 2px;
            height: 100%;
          "
        >
          <div
            class="fw-bold"
            style="font-size: 1.09em; line-height: 1.25; margin-bottom: 2px"
          >
            {{ recruit.title }}
          </div>
          <div class="small text-muted" style="line-height: 1.2; margin: 0">
            조회수: {{ recruit.view_count }} | 등록일:
            {{ formatDate(recruit.reg_date) }}
          </div>
        </div>

        <!-- 신청수 뱃지 + 모집 상태 뱃지 -->
        <div class="d-flex align-items-center" style="gap: 8px;">
          <span
            class="badge bg-light text-dark"
            style="font-size: 0.98em; min-width: 68px; font-weight: normal;"
          >
            신청 {{ recruit.apply_count }}명
          </span>
          <span
            class="badge"
            :class="recruit.is_closed === 1 ? 'bg-secondary' : 'bg-success'"
            style="font-size: 0.98em; min-width: 68px"
          >
            {{ recruit.is_closed === 1 ? "모집 마감" : "모집 중" }}
          </span>
        </div>
      </router-link>
    </ul>
  </div>
</template>


<script setup>
import { ref, computed, onMounted, inject } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";
import { useRoute } from "vue-router";

// 1. provide된 값 받기
const token = inject("token");
const userNo = inject("userNo");
const router = useRouter();
const route = useRoute();
function isActiveTab(path) {
  return route.path === path;
}
// const fallbackImg = "https://placehold.co/40x40?text=IMG";
// // ✅ 이미지 로딩 실패 시 기본 이미지로 대체
// const handleImageError = (event) => {
//   event.target.src = fallbackImg;
// };

// 2. 상태(reactive 변수)
const recruits = ref([]);
const searchTeam = ref("");
const hasClub = ref(false);

// 3. computed
const filteredRecruits = computed(() => {
  const keyword = searchTeam.value.toLowerCase();
  return recruits.value.filter((recruit) =>
    (recruit.club_name || "").toLowerCase().includes(keyword)
  );
});

// 4. 메서드
async function fetchRecruits(sortType = "") {
  try {
    const url =
      sortType === "popular" ? "/recruits_api?sort=popular" : "/recruits_api";
    const response = await axios.get(url);
    recruits.value = response.data;
  } catch (e) {
    alert("모집글을 불러오는 데 실패했습니다.");
    console.error(e);
  }
}

async function checkHasClub() {
  if (!userNo?.value) return;
  try {
    const response = await axios.get(`/club_api/club/hasClub/${userNo.value}`);
    hasClub.value = response.data.result;
  } catch (e) {
    console.error("클럽 조회 실패", e);
    hasClub.value = false;
  }
}

function formatDate(dateTime) {
  if (!dateTime || typeof dateTime !== "string") return "";
  return dateTime.split(" ")[0].split("T")[0];
}

function goToClubCreate() {
  console.log("token:", token);
  console.log("token.value:", token?.value);
  if (token?.value) {
    router.push("/club/registForm");
  } else {
    alert("클럽 생성을 하려면 로그인해야 합니다.");
    router.push("/member/loginForm");
  }
}

// 5. 라이프사이클
onMounted(() => {
  fetchRecruits();
  checkHasClub();
});
</script>
<style scoped>
.tab-btn {
  padding: 0.375rem 0.75rem;
  border: 1px solid #ced4da; /* 연회색 테두리 */
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
  border-color: black !important; /* 강조하고 싶다면 */
}
</style>
