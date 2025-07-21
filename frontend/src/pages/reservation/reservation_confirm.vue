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
      <p><strong>유형:</strong> {{ convertType(reservation.reservation_type) }}</p>
      <p><strong>가격:</strong> {{ reservation.price }}</p>
      <p><strong>예약 현황:</strong> {{ reservation.status === 'reserved' ? '예약 완료' : reservation.status === 'cancelled' ? '예약 취소' : reservation.status === 'expired' ? '예약 만료' : '예약 안됨' }}</p>
      <p><strong>결제 현황:</strong> {{ reservation.payment_status === 'paid' ? '결제 완료' : reservation.payment_status === 'canceled' ? '결제 취소됨' : reservation.payment_status === 'refunded' ? '결제 환불됨' : '미결제' }}</p>
    </div>

    <div class="text-center" style="margin-top: 2rem;">
      <button  v-if="reservation.payment_status === 'pending'" @click="requestPayment" class="button button-pay">
        결제하기
      </button>

      <button  v-if="reservation.payment_status === 'paid'" @click="refundPayment" class="button button-cancel">
        결제취소
      </button>

      <button v-if="reservation.status === 'reserved'" @click="cancleReservation" class="button button-cancel">
        예약취소
      </button>

    </div>

  </div>


</template>

<script setup>
  import { reservation_confirm } from '@/utils/script/reservation'
const {
    user,
    stadium,
    reservation,
		cancleReservation,
    requestPayment,
    refundPayment,
    convertType,
	} = reservation_confirm()
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