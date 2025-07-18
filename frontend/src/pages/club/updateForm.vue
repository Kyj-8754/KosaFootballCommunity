<template>
  <div class="container py-5">
    <h2 class="fw-bold mb-4 text-center">클럽 정보 수정</h2>

    <div v-if="club">
      <div class="row gx-4 gy-5 align-items-start">
        <!-- 좌측: 팀 로고 & 오버뷰 -->
        <div class="col-lg-3">
          <!-- 이미지 업로드 + 미리보기 (Base64) -->
          <div class="bg-light rounded p-4 text-center shadow-sm">
            <!-- Base64로 미리보기 -->
            <!-- <img
              :src="
                base64Image ||
                (club.logo_path
                  ? `https://www.http://www.itsfootball.store/${club.logo_path}`
                  : 'https://via.placeholder.com/120')
              "
              alt="클럽 로고"
              class="img-fluid mb-3"
              style="max-height: 250px; object-fit: contain"
            /> -->
            <img
              :src="club.logo_path || 'https://via.placeholder.com/120'"
              alt="클럽 로고"
              class="img-fluid mb-3"
              style="max-height: 250px; object-fit: contain"
            />

            <div v-if="isClubOwner" class="mb-2">
              <div
                class="form-text mb-2 text-secondary"
                style="font-size: 0.95em"
              >
                10MB 이하의 <b>이미지</b> 파일만 가능합니다.
              </div>

              <!-- ✅ base64 변환용 input -->
              <input
                type="file"
                accept="image/*"
                @change="handleFileChange"
                class="form-control mb-2"
              />

              <!-- 업로드 버튼 -->
              <button
                v-if="base64Image"
                @click="uploadBase64Logo"
                class="btn btn-sm btn-outline-primary mb-2"
              >
                업로드
              </button>
            </div>
          </div>
        </div>

        <!-- 우측: 메인 정보 (카드형 2x2 그리드) -->
        <div class="col-lg-9">
          <div class="row row-cols-1 row-cols-md-2 g-4">
            <!-- 1. 경기 전적 -->
            <div class="col">
              <div class="border rounded p-4 shadow-sm h-100 bg-white">
                <div class="fw-bold mb-2">경기 전적</div>
                <div class="mb-1">
                  승: <b>{{ club.win_count }}</b>
                </div>
                <div class="mb-1">
                  무: <b>{{ club.draw_count }}</b>
                </div>
                <div class="mb-1">
                  패: <b>{{ club.loss_count }}</b>
                </div>
                <div class="text-muted mt-2 small">
                  참가수:
                  {{
                    (club.win_count || 0) +
                    (club.draw_count || 0) +
                    (club.loss_count || 0)
                  }}
                  <span class="ms-3"
                    >팀 레벨:
                    <span class="badge bg-primary">{{ teamLevel }}</span></span
                  >
                </div>
              </div>
            </div>

            <!-- 박스 2: 주요 멤버 -->
            <div class="col">
              <div class="border rounded p-3 h-100 bg-light-subtle">
                <strong class="mb-2 d-block">주요 멤버</strong>
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
            <!-- 3. 간단한 팀 정보/수정 폼 -->
            <div class="col">
              <div class="border rounded p-4 shadow-sm h-100 bg-white">
                <div class="fw-bold mb-2">팀 정보</div>
                <!-- 성별 -->
                <div class="mb-2">
                  <label class="fw-bold">성별</label>
                  <select v-model="clubInfo.gender" class="form-select">
                    <option value="">선택</option>
                    <option
                      v-for="opt in genderOptions"
                      :key="opt"
                      :value="opt"
                    >
                      {{ opt }}
                    </option>
                  </select>
                </div>
                <!-- 나이대 -->
                <div class="mb-2">
                  <label class="fw-bold">나이대</label>
                  <select v-model="clubInfo.age_group" class="form-select">
                    <option value="">선택</option>
                    <option
                      v-for="opt in ageGroupOptions"
                      :key="opt"
                      :value="opt"
                    >
                      {{ opt }}
                    </option>
                  </select>
                </div>
                <!-- 활동 요일 -->
                <div class="mb-2">
                  <label class="fw-bold">활동 요일</label><br />
                  <label
                    v-for="day in daysOptions"
                    :key="day"
                    class="me-2 small"
                  >
                    <input
                      type="checkbox"
                      :value="day"
                      v-model="clubInfo.active_days"
                    />
                    {{ day }}
                  </label>
                </div>
                <!-- 활동 시간 -->
                <div class="mb-2">
                  <label class="fw-bold">활동 시간</label><br />
                  <label
                    v-for="time in timeOptions"
                    :key="time"
                    class="me-2 small"
                  >
                    <input
                      type="checkbox"
                      :value="time"
                      v-model="clubInfo.active_times"
                    />
                    {{ time }}
                  </label>
                </div>
              </div>
            </div>
            <!-- 4. 팀 소개글 -->
            <div class="col">
              <div class="border rounded p-4 shadow-sm h-100 bg-white">
                <div class="fw-bold mb-2">팀 소개</div>
                <textarea
                  v-model="club.description"
                  class="form-control"
                  rows="7"
                  placeholder="클럽 설명을 입력하세요"
                ></textarea>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 저장/취소 버튼 (오른쪽 정렬) -->
      <div
        v-if="club && userNo && club.user_no === userNo"
        class="text-end mt-4"
      >
        <button @click="submitUpdate" class="btn btn-primary me-2">
          저장하기
        </button>
        <button @click="cancelUpdate" class="btn btn-outline-secondary">
          취소하기
        </button>
      </div>
      <div v-else class="text-end mt-4">
        <span class="text-muted">팀장만 클럽 정보를 수정할 수 있습니다.</span>
      </div>
    </div>
    <p v-else class="text-muted mt-5 text-center">
      클럽 정보를 불러오는 중입니다...
    </p>
  </div>
</template>
<script setup>
import { ref, inject, computed, onMounted, watchEffect } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";

const token = inject("token");
const userNo = inject("userNo");
const router = useRouter();
const route = useRoute();

const club = ref(null);
const clubInfo = ref({
  gender: "",
  age_group: "",
  active_days: [],
  active_times: [],
});
const clubMember = ref([]);

const genderOptions = ["남성", "여성", "혼성"];
const ageGroupOptions = ["10~20", "20~30", "30~40", "40~50"];
const daysOptions = ["월", "화", "수", "목", "금", "토", "일"];
const timeOptions = ["아침", "오후", "저녁", "야간", "심야"];

const isClubOwner = computed(
  () => club.value && userNo && Number(club.value.user_no) === Number(userNo.value)
);

// ✅ Base64 관련 상태
const base64Image = ref("");
const selectedFile = ref(null);

function handleFileChange(event) {
  const file = event.target.files[0];
  if (!file) return;
  selectedFile.value = file;

  const reader = new FileReader();
  reader.onload = () => {
    base64Image.value = reader.result;
  };
  reader.readAsDataURL(file);
}

// ✅ Base64 업로드 요청 정확히 수정됨
async function uploadBase64Logo() {
  if (!base64Image.value || !club.value) {
    alert("업로드할 이미지 또는 클럽 정보가 없습니다.");
    return;
  }

  try {
    const { data } = await axios.post(
      `/club_api/${club.value.club_id}/uploadLogoBase64`,
      base64Image.value, // ✅ 문자열 단독 전송
      {
        headers: {
          Authorization: `Bearer ${token.value}`,
          'Content-Type': 'text/plain' // ✅ 중요
        }
      }
    );
    club.value.logo_path = data;
    alert("로고가 업로드되었습니다.");
    base64Image.value = "";
    selectedFile.value = null;
  } catch (e) {
    alert("로고 업로드 실패!");
    console.error(e);
  }
}

onMounted(async () => {
  const teamCode = route.params.teamCode;
  try {
    const response = await axios.get(`/club_api/code/${teamCode}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });
    club.value = response.data;

    if (club.value && club.value.club_id) {
      try {
        const clubInfoRes = await axios.get(`/club_info/${club.value.club_id}`);
        clubInfo.value = {
          ...clubInfoRes.data,
          active_days: clubInfoRes.data.active_days
            ? clubInfoRes.data.active_days.split(",")
            : [],
          active_times: clubInfoRes.data.active_times
            ? clubInfoRes.data.active_times.split(",")
            : [],
        };
      } catch (infoErr) {
        if (infoErr.response?.status === 404) {
          clubInfo.value = {
            gender: "",
            age_group: "",
            active_days: [],
            active_times: [],
          };
        } else {
          alert("클럽 상세 정보 조회 중 오류가 발생했습니다.");
        }
      }

      const memberRes = await axios.get(
        `/club_api/member/list/${club.value.club_id}`
      );
      clubMember.value = memberRes.data;
    }
  } catch (e) {
    alert("클럽 정보를 불러오는 데 실패했습니다.");
  }
});

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

const topMembers = computed(() => {
  return [...clubMember.value]
    .sort((a, b) => parseInt(b.match_count || 0) - parseInt(a.match_count || 0))
    .slice(0, 3);
});

const submitUpdate = async () => {
  if (!isClubOwner.value) {
    alert("팀장만 수정할 수 있습니다.");
    return;
  }

  const { gender, age_group, active_days, active_times } = clubInfo.value;
  if (
    !gender ||
    !age_group ||
    active_days.length === 0 ||
    active_times.length === 0
  ) {
    alert("⚠️ 성별, 나이대, 활동 요일 및 활동 시간을 모두 선택해주세요.");
    return;
  }

  try {
    const clubInfoPayload = {
      ...clubInfo.value,
      club_id: club.value.club_id,
      active_days: active_days.join(","),
      active_times: active_times.join(","),
    };

    await axios.put(`/club_api/${club.value.club_id}`, club.value, {
      headers: { Authorization: `Bearer ${token.value}` },
    });
    await axios.put(`/club_info/${club.value.club_id}`, clubInfoPayload, {
      headers: { Authorization: `Bearer ${token.value}` },
    });

    alert("수정이 완료되었습니다.");
    router.push(`/club/${club.value.team_code}`);
  } catch (e) {
    alert("수정 중 오류가 발생했습니다.");
    console.error(e);
  }
};

const cancelUpdate = () => {
  if (club.value) {
    router.push(`/club/${club.value.team_code}`);
  } else {
    router.back();
  }
};

watchEffect(() => {
  console.log("userNo:", userNo?.value, "club.user_no:", club.value?.user_no);
});
</script>
