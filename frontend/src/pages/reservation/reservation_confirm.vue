<template>
  <div class="max-w-2xl mx-auto mt-8 p-6 bg-white rounded-xl shadow-md space-y-6">
    <h2 class="text-2xl font-bold text-center">예약 확인</h2>

    <!-- 구장 정보 -->
    <div class="border-b pb-4">
      <h3 class="text-lg font-semibold mb-2">🏟️ 구장 정보</h3>
      <p><strong>구장명:</strong> {{ stadium.name }}</p>
      <p><strong>주소:</strong> {{ stadium.address }}</p>
      <p><strong>연락처:</strong> {{ stadium.tel }}</p>
    </div>

    <!-- 유저 정보 -->
    <div class="border-b pb-4">
      <h3 class="text-lg font-semibold mb-2">👤 사용자 정보</h3>
      <p><strong>이름:</strong> {{ user.name }}</p>
      <p><strong>전화번호:</strong> {{ user.phone }}</p>
    </div>

    <!-- 예약 정보 -->
    <div class="pb-4">
      <h3 class="text-lg font-semibold mb-2">📅 예약 정보</h3>
      <p><strong>날짜:</strong> {{ reservation.date }}</p>
      <p><strong>시간:</strong> {{ reservation.time }}</p>
      <p><strong>유형:</strong> {{ reservation.reservation_type }}</p>
    </div>

    <div class="text-center">
      <button @click="requestPayment"
              class="mt-4 bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-xl">
        💳 결제하기
      </button>
    </div>
  </div>
</template>

<script setup>
const stadium = {
  name: '마루공원 풋살장',
  address: '서울시 강남구 테헤란로 123',
  tel: '02-123-4567'
};

const user = {
  name: '홍길동',
  phone: '010-1234-5678'
};

const reservation = {
  date: '2025-07-01',
  time: '18:00 ~ 20:00',
  reservation_type: 'social'
};

const requestPayment = () => {
  // 결제 요청 로직 작성 예정
  alert('결제 로직 연결 예정');
};
</script>
