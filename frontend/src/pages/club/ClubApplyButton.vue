<template>
  <!-- 👇 수정된 조건식 -->
  <template v-if="!isOwner">
    <button
      v-if="status === 'none' && isLoggedIn"
      class="btn btn-outline-primary"
      v-bind="attrs"
      @click="handleApply" > 가 입 신 청
    </button>

    <button
      v-else-if="status === 'pending'"
      class="btn btn-outline-secondary btn-sm me-2"
      @click="handleCancel"
    >
      가입 신청 취소
    </button>

    <span
      v-else-if="status === 'approved'"
      class="badge bg-success align-middle"
    >
      이미 클럽 멤버입니다
    </span>

    <span
      v-else-if="status === 'rejected'"
      class="badge bg-danger align-middle"
    >
      신청이 거절되었습니다
    </span>

    <span
      v-else-if="status === 'canceled'"
      class="badge bg-warning text-dark align-middle"
    >
      신청이 취소되었습니다
    </span>
  </template>
</template>

<script setup>
import { ref, inject, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { useAttrs } from 'vue'
const isOwner = computed(
  () => parseInt(userNo?.value) === parseInt(props.ownerUserNo)
);

const attrs = useAttrs()
// 부모에서 넘겨주는 props
const props = defineProps({
  clubId: [Number, String], // 모집글 번호
  ownerUserNo: [Number, String], // 팀장 userNo
});

const token = inject("token");
const userNo = inject("userNo");
const router = useRouter();

const status = ref("none"); // pending/approved/rejected/canceled/none
const isLoggedIn = computed(() => !!token?.value);

const handleApply = async () => {
  if (!isLoggedIn.value) {
    alert("가입 신청을 하려면 로그인해야 합니다.");
    router.push("/member/loginForm");
    return;
  }
  try {
    await axios.post(
      "/club_api/apply/club",
      {
        club_id: props.clubId,
        appli_user_no: Number(userNo.value),
      },
      {
        headers: { Authorization: `Bearer ${token?.value}` },
      }
    );
    status.value = "pending";
    alert("✅ 가입 신청이 완료되었습니다.");
  } catch (e) {
    console.error("가입 신청 실패:", e);
    alert("❌ 가입 신청 실패");
  }
};

const handleCancel = async () => {
  try {
    await axios.delete("/club_api/apply/club", {
      data: {
        club_id: props.clubId,
        appli_user_no: Number(userNo.value),
      },
      headers: { Authorization: `Bearer ${token?.value}` },
    });
    status.value = "canceled";
    alert("✅ 가입 신청이 취소되었습니다.");
  } catch (e) {
    console.error("가입 취소 실패:", e);
    alert("❌ 가입 취소 실패");
  }
};

// 내 가입 상태 조회
const fetchApplyStatus = async () => {
  if (!userNo?.value) return;
  try {
    const res = await axios.get("/club_api/apply/club/status", {
      params: {
        club_id: props.clubId,
        user_no: userNo.value,
      },
    });
    status.value =
      typeof res.data.status === "string" ? res.data.status : "none";
  } catch {
    status.value = "none";
  }
};

// 상태 변경시/진입시 항상 상태 조회
onMounted(fetchApplyStatus);
watch(() => props.clubId, fetchApplyStatus);
</script>
