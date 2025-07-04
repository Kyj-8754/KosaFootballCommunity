<template>
  <div class="max-w-2xl mx-auto mt-8 p-6 bg-white rounded-xl shadow-md space-y-6">
    <h2 class="text-2xl font-bold text-center">예약 확인</h2>

    <!-- 구장 정보 -->
    <div class="border-b pb-4">
      <h3 class="text-lg font-semibold mb-2">🏟️ 구장 정보</h3>
      <p><strong>구장명:</strong> {{ stadium.svcnm }}</p>
      <p><strong>주소:</strong> {{ stadium.adres }}</p>
      <p><strong>연락처:</strong> {{ stadium.telno }}</p>
    </div>

    <!-- 유저 정보 -->
    <div class="border-b pb-4">
      <h3 class="text-lg font-semibold mb-2">👤 사용자 정보</h3>
      <p><strong>이름:</strong> {{ user.userName }}</p>
      <p><strong>전화번호:</strong> {{ user.userPhone }}</p>
    </div>

    <!-- 예약 정보 -->
    <div class="pb-4">
      <h3 class="text-lg font-semibold mb-2">📅 예약 정보</h3>
      <p><strong>날짜:</strong> {{ reservation.slot_date }}</p>
      <p><strong>시간:</strong> {{ reservation.start_time }} ~ {{ reservation.end_time }}</p>
      <p><strong>유형:</strong> {{ reservation.reservation_type }}</p>
      <p><strong>가격:</strong> {{ reservation.price }}</p>
      <p><strong>예약 현황:</strong> {{ reservation.status }}</p>
    </div>

    <div class="text-cente" style="margin-top: 2rem;">
      <button @click="requestPayment" class="button button-pay">
        결제하기
      </button>

      <button @click="cancleReservation" class="button button-cancel">
        예약취소
      </button>
    </div>

  </div>

 
</template>

<script setup>
import axios from 'axios';
import { inject, onMounted, ref} from 'vue';
import { useRoute } from 'vue-router'


const route = useRoute();
const reservation = ref({});
const user = ref({});
const stadium = ref({});
const userNo = inject('userNo') // 로그인한 유저 정보 가져옴


onMounted(async () =>{
  const reservation_id = route.params.reservationId;
  const res = await axios.post('/reservation_api/reservation/reservation_confirm', {
      reservation_id: reservation_id });

  reservation.value = res.data.reservationDB;
  const { user_no, svcid } = reservation.value;

  // 병렬로 사용자 정보와 구장 정보 가져오기
  const [userRes, stadiumRes] = await Promise.all([
    axios.get(`/login_api/mypage/detailView` , {params: { userNo: user_no }}),
    axios.get(`/stadium_api/stadium/detailView`, { params: { SVCID: svcid } })
  ]);

  user.value = userRes.data.member;
  stadium.value = stadiumRes.data.stadiumDB.stadium;
  
})

// 예약 취소
 const cancleReservation = async () => {
    const res = await axios.post('/reservation_api/reservation/cancle',{
        reservation: reservation.value,
        user_no: userNo
    })
  }

// 결제 요청
const requestPayment = async () => {
  try{
    const res = await axios.post('/kakao_api/kakaopay/ready', {
      item_name: stadium.value.svcnm,
      total_amount: reservation.value.price,
      partner_order_id: reservation.value.reservation_id,
      partner_user_id: reservation.value.user_no
    });
  const redirectUrl = res.data.next_redirect_pc_url
    if (redirectUrl) {
        openCenteredPopup(redirectUrl, '카카오페이 결제', 500, 700);
      } else {
        alert("결제 URL을 받아오지 못했습니다.");
      }

  } catch (err) {
    // 서버에서 온 에러 메시지 처리
    if (err.response && err.response.data?.message) {
      const message = err.response?.data?.message || "결제 요청 중 알 수 없는 오류가 발생했습니다.";
      alert(message);
    } else {
      alert("결제 요청 중 오류가 발생했습니다.");
    }
  }
};

const openCenteredPopup = (url, title, w, h) => {
  // 현재 브라우저 창 기준 위치
  const dualScreenLeft = window.screenX ?? window.screenLeft
  const dualScreenTop = window.screenY ?? window.screenTop

  const width = window.outerWidth ?? document.documentElement.clientWidth
  const height = window.outerHeight ?? document.documentElement.clientHeight

  const systemZoom = width / window.screen.availWidth

  const left = dualScreenLeft + (width - w) / 2 / systemZoom
  const top = dualScreenTop + (height - h) / 2 / systemZoom

  const popup = window.open(
    url,
    title,
    `scrollbars=yes, width=${w}, height=${h}, top=${top}, left=${left}`
  )

  if (popup?.focus) popup.focus()
}
</script>

<style scoped>
.button {
  padding: 10px 16px;
  font-weight: bold;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  margin: 0 10px;
  border: none;
  cursor: pointer;
}

.button-pay {
  background-color: #2563eb; /* blue-600 */
  color: white;
}

.button-cancel {
  background-color: #ef4444; /* red-500 */
  color: white;
}

.button:hover {
  opacity: 0.9;
}
</style>