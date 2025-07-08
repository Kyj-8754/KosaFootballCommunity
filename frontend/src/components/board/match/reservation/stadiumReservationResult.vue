<template>
  <div class="container mt-4" v-if="reservation && Object.keys(reservation).length">
    <h3 class="text-center mb-4">예약 확인</h3>

    <!-- 🏟 구장 정보 -->
    <div class="card mb-3">
      <div class="card-header">구장 정보</div>
      <div class="card-body">
        <p><strong>구장명:</strong> {{ stadium.svcnm }}</p>
        <p><strong>주소:</strong> {{ stadium.adres }}</p>
        <p><strong>연락처:</strong> {{ stadium.telno }}</p>
      </div>
    </div>

    <!-- 👤 사용자 정보 -->
    <div class="card mb-3">
      <div class="card-header">사용자 정보</div>
      <div class="card-body">
        <p><strong>이름:</strong> {{ user.userName }}</p>
        <p><strong>전화번호:</strong> {{ user.userPhone }}</p>
      </div>
    </div>

    <!-- 📅 예약 정보 -->
    <div class="card mb-4">
      <div class="card-header">예약 정보</div>
      <div class="card-body">
        <p><strong>날짜:</strong> {{ reservation.slot_date }}</p>
        <p><strong>시간:</strong> {{ reservation.start_time }} ~ {{ reservation.end_time }}</p>
        <p><strong>유형:</strong> {{ reservation.reservation_type }}</p>
        <p><strong>가격:</strong> {{ reservation.price }} 원</p>
      </div>
    </div>

    <!-- 결제 상태에 따라 버튼 표시 -->
    <div class="text-center">
      <button
        v-if="!isPaid"
        class="btn btn-success me-2"
        @click="requestPayment"
      >
        💳 결제하기
      </button>

      <button
        v-if="isPaid"
        class="btn btn-outline-primary"
        @click="goToMatchRegister"
      >
        ⚽ 매치 등록하러 가기
      </button>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const props = defineProps({
  reservationId: { type: String, required: true }
})

const user = ref({})
const stadium = ref({})
const reservation = ref({})
const isPaid = ref(false);

onMounted(async () => {
  try {
    const res = await axios.post('/reservation_api/reservation/reservation_confirm', {
      reservation_id: props.reservationId
    });

    const reservationData = res.data.reservationDB;
    if (!reservationData) {
      throw new Error('예약 데이터가 없습니다.');
    }

    reservation.value = reservationData;
    const { user_no, svcid, reservation_id } = reservationData;

    const [userRes, stadiumRes, paidRes] = await Promise.all([
      axios.get('/login_api/mypage/detailView', {
        params: { userNo: user_no }
      }),
      axios.get('/stadium_api/stadium/detailView', {
        params: { SVCID: svcid }
      }),
      axios.get('/board_api/match/reservation-paid', {
        params: { reservationId: reservation_id }
      })
    ]);

    user.value = userRes.data.member;
    stadium.value = stadiumRes.data.stadiumDB.stadium;
    isPaid.value = paidRes.data.paid === true;

  } catch (err) {
    console.error('예약 확인 실패:', err);
    alert('예약 정보를 불러오는 중 오류가 발생했습니다.');
  }
});

const openCenteredPopup = (url, title, w, h) => {
  const dualScreenLeft = window.screenX ?? window.screenLeft;
  const dualScreenTop = window.screenY ?? window.screenTop;
  const width = window.outerWidth ?? document.documentElement.clientWidth;
  const height = window.outerHeight ?? document.documentElement.clientHeight;
  const systemZoom = width / window.screen.availWidth;

  const left = dualScreenLeft + (width - w) / 2 / systemZoom;
  const top = dualScreenTop + (height - h) / 2 / systemZoom;

  const popup = window.open(
    url,
    title,
    `scrollbars=yes, width=${w}, height=${h}, top=${top}, left=${left}`
  );

  if (popup?.focus) popup.focus();
};

const goToMatchRegister = () => {
  router.push({
    name: 'matchregister',
    state: {
      reservation: reservation.value,
      user: user.value,
      stadium: stadium.value
    }
  });
};
</script>
