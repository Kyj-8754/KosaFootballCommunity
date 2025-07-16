<template>
  <div class="container py-5">
    <h2 class="fw-bold mb-4 text-center">
      {{ club && club.club_name ? club.club_name : "클럽 상세 정보" }}
    </h2>

    <div v-if="club">
      <div class="row mb-4">
        <!-- 좌측 상단: 이미지 등록 (고정 크기) -->
        <div class="col-md-3">
          <div class="border rounded p-3 text-center" style="height: 250px">
            <!-- ✅ 이 자리에 아래 코드 삽입 -->
            <img
              :src="
                club.logo_path
                  ? `/club_api${club.logo_path}`
                  : fallbackImg
              "
              @error="handleImageError"
              alt="클럽 로고"
              class="img-fluid mb-3"
              style="max-height: 250px; object-fit: contain"
            />
          </div>
          <!-- ✅ 버튼은 아래로 분리해서 배치 -->
          <div class="d-flex justify-content-center mt-4">
            <ClubApplyButton
              :club-id="club.club_id"
              :owner-user-no="club.user_no"
              class="btn btn-outline-primary"
              style="font-size: 1rem; padding: 1rem 3rem"
            />
          </div>
        </div>

        <!-- 우측 4개의 동일 박스 -->
        <div class="col-md-9">
          <div class="row row-cols-2 g-3">
            <!-- 박스 1: 경기 전적 -->
            <div class="col">
              <div
                class="border rounded shadow-sm h-100 bg-white"
                style="padding: 16px 16px 16px 16px"
              >
                <div class="fw-bold mb-2" style="margin-top: -1px">
                  경기 전적
                </div>

                <!-- 승/무/패 -->
                <div class="d-flex justify-content-between text-center mb-3">
                  <div class="flex-fill text-center">
                    <div class="text-muted small">승</div>
                    <div class="fs-5 fw-bold">{{ club.win_count || 0 }}</div>
                  </div>
                  <div class="flex-fill border-start border-end text-center">
                    <div class="text-muted small">무</div>
                    <div class="fs-5 fw-bold">{{ club.draw_count || 0 }}</div>
                  </div>
                  <div class="flex-fill text-center">
                    <div class="text-muted small">패</div>
                    <div class="fs-5 fw-bold">{{ club.loss_count || 0 }}</div>
                  </div>
                </div>

                <hr />

                <!-- 참가수 / 팀 레벨 / 멤버 수 -->
                <div class="d-flex justify-content-between text-muted small">
                  <div>
                    참가수:
                    <b>
                      {{
                        (club.win_count || 0) +
                        (club.draw_count || 0) +
                        (club.loss_count || 0)
                      }}
                    </b>
                  </div>
                  <div>
                    팀 레벨:
                    <span class="badge bg-primary">{{ teamLevel }}</span>
                  </div>
                  <div>
                    멤버:
                    <b>{{ clubMember.length }}</b>
                  </div>
                </div>
              </div>
            </div>

            <!-- 박스 2: 주요 멤버 -->
            <div class="col">
              <div
                class="border rounded p-3 h-100 bg-light-subtle position-relative"
              >
                <div
                  class="d-flex justify-content-between align-items-center mb-2"
                >
                  <strong>주요 멤버</strong>
                  <button
                    class="btn btn-link p-0 small text-primary fw-bold"
                    style="text-decoration: none"
                    @click="goToMemberList"
                  >
                    전체 보기
                  </button>
                </div>
                <div class="d-flex flex-column gap-2 mt-2">
                  <div
                    v-for="member in topMembers"
                    :key="member.user_no"
                    class="p-3 bg-white rounded-3 shadow-sm d-flex align-items-center gap-3"
                    style="min-height: 60px"
                  >
                    <div class="flex-grow-1">
                      <span class="fw-bold fs-6">{{ member.user_name }}</span>
                    </div>
                    <span class="badge bg-primary bg-opacity-75 fs-7">
                      참가수: {{ member.match_count }}
                    </span>
                    <span class="badge bg-warning text-dark ms-2 fs-7">
                      POM: {{ member.pom }}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 박스 3: 클럽 상세 정보 -->
            <div class="col">
              <div class="border rounded p-3 h-100 bg-white shadow-sm">
                <h6 class="fw-bold mb-3">클럽 상세 정보</h6>

                <template v-if="clubInfo">
                  <!-- 성별 -->
                  <div
                    class="d-flex justify-content-between align-items-center mb-2"
                  >
                    <span class="text-muted">성별</span>
                    <span class="badge bg-light text-dark">{{
                      clubInfo.gender
                    }}</span>
                  </div>

                  <!-- 나이대 -->
                  <div
                    class="d-flex justify-content-between align-items-center mb-2"
                  >
                    <span class="text-muted">나이대</span>
                    <span class="badge bg-light text-dark">{{
                      clubInfo.age_group
                    }}</span>
                  </div>

                  <!-- 활동 요일 -->
                  <div
                    class="d-flex justify-content-between align-items-center mb-2"
                  >
                    <span class="text-muted">활동 요일</span>
                    <div>
                      <span
                        v-for="day in clubInfo?.active_days
                          ? clubInfo.active_days.split(',')
                          : []"
                        :key="day"
                        class="badge bg-info text-dark me-1"
                      >
                        {{ day }}
                      </span>
                    </div>
                  </div>

                  <!-- 활동 시간 -->
                  <div
                    class="d-flex justify-content-between align-items-center"
                  >
                    <span class="text-muted">활동 시간</span>
                    <div>
                      <span
                        v-for="time in clubInfo?.active_times
                          ? clubInfo.active_times.split(',')
                          : []"
                        :key="time"
                        class="badge bg-success text-white me-1"
                      >
                        {{ time }}
                      </span>
                    </div>
                  </div>
                </template>

                <template v-else>
                  <div
                    class="d-flex flex-column align-items-center justify-content-center"
                    style="height: 130px"
                  >
                    <p class="text-muted mb-0">
                      클럽 상세 정보를 입력해주세요.
                    </p>
                  </div>
                </template>
              </div>
            </div>

            <!-- 박스 4: 팀 소개 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>팀 소개</strong>
                <p class="mb-0">{{ club.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 팀장만 수정/신청목록 버튼 노출 -->
      <div class="mb-2 text-end">
        <button
          v-if="club && userNo && club.user_no === userNo"
          @click="goToApplyList"
          class="btn btn-warning me-2"
        >
          신청목록
        </button>

        <button
          v-if="club && userNo && club.user_no === userNo"
          @click="goToEdit"
          class="btn btn-primary me-2"
        >
          수정하기
        </button>
        <button @click="goToList" class="btn btn-outline-secondary">
          목록으로
        </button>
      </div>
    </div>

    <p v-else class="text-muted mt-5 text-center">
      클럽 정보를 불러오는 중입니다...
    </p>
  </div>
</template>

<script setup>
import ClubApplyButton from "./ClubApplyButton.vue";
import { ref, inject, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";

const token = inject("token");
const userNo = inject("userNo");
const router = useRouter();
const route = useRoute();

const club = ref(null);
const clubInfo = ref(null); // ✅ 클럽 상세정보 상태 추가
const clubMember = ref([]);

// ⭐️ 경기수 내림차순 상위 3명만 추출하는 computed
const topMembers = computed(() => {
  // match_count가 null/빈값일 때 0으로 간주
  return [...clubMember.value]
    .sort((a, b) => parseInt(b.match_count || 0) - parseInt(a.match_count || 0))
    .slice(0, 3);
});

// ✅ 팀 레벨 산출 공식 (백엔드와 동일, 브론즈~다이아)
function calculateClubLevel(win, draw, loss) {
  const total = win + draw + loss;
  if (total === 0) return "브론즈";
  const rate = (win / total) * 100;
  if (rate >= 90) return "다이아";
  if (rate >= 70) return "플래티넘";
  if (rate >= 50) return "골드";
  if (rate >= 30) return "실버";
  return "브론즈";
}
const teamLevel = computed(() => {
  if (!club.value) return "-";
  return calculateClubLevel(
    club.value.win_count || 0,
    club.value.draw_count || 0,
    club.value.loss_count || 0
  );
});

onMounted(async () => {
  const teamCode = route.params.teamCode;
  try {
    // 1. club, clubMember는 실패하면 alert
    const response = await axios.get(`/club_api/club/code/${teamCode}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });
    club.value = response.data;

    if (club.value && club.value.club_id) {
      const memberRes = await axios.get(
        `/club_api/club/member/list/${club.value.club_id}`
      );
      clubMember.value = memberRes.data;
    }
  } catch (error) {
    // 이 부분은 club, clubMember만!
    alert("클럽 정보를 불러오는 데 실패했습니다.");
    return; // 아래 clubInfo 조회 진행하지 않음
  }

  // 2. clubInfo는 반드시 별도 try-catch에서!
  if (club.value && club.value.club_id) {
    try {
      const clubInfoRes = await axios.get(`/club_api/club/${club.value.club_id}`);
      clubInfo.value = clubInfoRes.data;
    } catch (infoErr) {
      if (infoErr.response && infoErr.response.status === 404) {
        // 상세 정보 없음(404)은 안내문구만!
        clubInfo.value = null;
      } else {
        // 진짜 장애만 alert
        alert("클럽 상세 정보 조회 중 오류가 발생했습니다.");
      }
    }
  }
});

// 클럽의 총 경기 수 계산 함수
const getTotalGames = (clubObj) =>
  (clubObj.win_count || 0) +
  (clubObj.draw_count || 0) +
  (clubObj.loss_count || 0);

// 클럽의 승률 계산 함수
const calcWinRate = (clubObj) => {
  const total = getTotalGames(clubObj);
  return total === 0 ? 0 : Math.round((clubObj.win_count / total) * 100);
};

// 신청자 목록 페이지로 이동 (팀장만 가능)
const goToApplyList = () => {
  if (club.value) {
    router.push(`/club/${club.value.team_code}/applyList`);
  }
};

// 수정 페이지로 이동 (팀장만 가능)
const goToEdit = () => {
  if (club.value) {
    router.push(`/club/${club.value.team_code}/updateForm`);
  }
};

// 클럽 목록 페이지로 이동
const goToList = () => {
  router.push({ name: "Club_List" });
};

const goToMemberList = () => {
  if (club.value && club.value.team_code) {
    router.push(`/club/${club.value.team_code}/memberList`);
  }
};

const fallbackImg = ref("https://placehold.co/100x100?text=No+Logo");

// 이미지 로딩 실패 시 fallback 이미지로 변경
const handleImageError = (event) => {
  event.target.src = fallbackImg.value;
};
</script>
