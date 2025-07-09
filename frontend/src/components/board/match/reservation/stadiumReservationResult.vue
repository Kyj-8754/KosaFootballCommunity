<template>
  <div class="container mt-4">
    <!-- ✅ 예약 정보가 있을 때 -->
    <div v-if="reservation && Object.keys(reservation).length">
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

    <!-- ❌ 예약 정보가 없을 때 -->
    <div v-else class="text-center text-danger mt-5">
      <p>예약 정보가 존재하지 않습니다.</p>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { onMounted, ref, inject  } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const props = defineProps({
  reservationId: {
    type: [String, Number],
    required: true
  },
  boardId: {
    type: [String, Number],
    required: true
  }
})

const user = ref({})
const stadium = ref({})
const reservation = ref({})
const isPaid = ref(false);
const authCode = inject('authCode');
const userNo = inject('userNo');

const loadReservationData = async () => {
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
  }
};

onMounted(async () => {
  await loadReservationData();
});

const requestPayment = async () => {
  const confirmPayment = confirm("결제 하시겠습니까?");
  if (!confirmPayment) return;

  try {
    const res = await axios.post('/kakao_api/kakaopay/ready', {
      item_name: stadium.value.svcnm,
      total_amount: reservation.value.price,
      partner_order_id: reservation.value.reservation_id,
      partner_user_id: userNo.value,
      authCode: authCode.value
    });

    const redirectUrl = res.data.next_redirect_pc_url;
    if (redirectUrl) {
      openCenteredPopup(redirectUrl, '카카오페이 결제', 500, 700);
    } else {
      alert("결제 URL을 받아오지 못했습니다.");
    }

  } catch (err) {
    if (err.response?.data?.message) {
      alert(err.response.data.message);
    } else {
      alert("결제 요청 중 오류가 발생했습니다.");
    }
  }
};

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

  // ✅ 팝업이 닫히면 loadReservationData() 실행
  const checkClosed = setInterval(() => {
    if (popup.closed) {
      clearInterval(checkClosed);
      loadReservationData(); // 💡 무조건 상태 새로고침
    }
  }, 500);
};

const goToMatchRegister = () => {
  router.push({
    name: 'matchregister',
    state: {
      svcid: stadium.value.svcid,
      userNo: user.value.userNo,
      slot_date: reservation.value.slot_date,
      start_time: reservation.value.start_time,
      reservation_type: reservation.value.reservation_type,
      reservation_id: reservation.value.reservation_id,
      board_id: props.boardId
    }
  });
};
</script>
