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
    </div>

    <div class="text-center">
      <button @click="requestPayment"
              class="mt-4 bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-xl">
        💳 결제하기
      </button>
      <button @click="requestPayment"
              class="mt-4 bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-xl">
        💳 결제취소
      </button>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import {onMounted, ref} from 'vue';
import { useRoute, useRouter } from 'vue-router'

const route = useRoute();
const reservation = ref({});
const user = ref({});
const stadium = ref({});

onMounted(async () =>{
  const reservation_id = route.params.reservationId;
  const res = await axios.post('/reservation_api/reservation/reservation_confirm', {
      reservation_id: reservation_id });

  reservation.value = res.data.reservationDB[0];
  const { user_no, svcid } = reservation.value;

  // 병렬로 사용자 정보와 구장 정보 가져오기
  const [userRes, stadiumRes] = await Promise.all([
    axios.get(`/login_api/mypage/detailView` , {params: { userNo: user_no }}),
    axios.get(`/stadium_api/stadium/detailView`, { params: { SVCID: svcid } })
  ]);

  user.value = userRes.data.member;
  stadium.value = stadiumRes.data.stadiumDB.stadium;

  reservation.value = res.data.reservationDB[0];
  
})

const requestPayment = () => {
  // 결제 요청 로직 작성 예정
  alert('결제 로직 연결 예정');
};
</script>
