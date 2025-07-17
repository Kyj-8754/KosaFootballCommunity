<template>
    
	<h2>구장 예약</h2>

 <div class="container mt-5">
    <!-- 구장 정보 -->
    <div class="card mb-3">
      <div class="card-header">구장 정보</div>
      <div class="card-body">
        <h5 class="card-title">{{ stadiumDB.svcnm }}</h5>
        <p class="card-text">주소: {{ stadiumDB.adres }}</p>
        <p class="card-text">운영시간: {{ stadiumDB.v_MIN }} ~ {{ stadiumDB.v_MAX }}</p>
        <p class="card-text">가격: {{ stadiumDB.price }}</p>
      </div>
    </div>

    <!-- 유저 정보 -->
    <div class="card mb-3">
      <div class="card-header">예약자 정보</div>
      <div class="card-body">
        <p>이름: {{ UserDB.userName }}</p>
        <p>전화 번호: {{ UserDB.userPhone }}</p>
      </div>
    </div>

    <!-- 예약 정보 -->
   <div class="card mb-3">
  <div class="card-header">예약 정보</div>
  <div class="card-body">
    <div class="mb-3">
      <label class="form-label">예약 날짜</label>
      <div class="form-control-plaintext">{{ date }}</div>
    </div>
    <div class="mb-3">
      <label for="time" class="form-label">예약 시간</label>
      <select class="form-select" v-model="reservation.slot_id">
        <option disabled value="">시간을 선택하세요</option>
        <option
          v-for="slot in timeSlots"
          :key="slot.value"
          :value="slot.value"
          :disabled="slot.disabled"
        >
          {{ slot.label }}
        </option>
      </select>
    </div>
    <div class="mb-3">
      <label for="type" class="form-label">예약 유형</label>
      <select v-model="reservation.reservation_type" class="form-select">
        <option disabled value="">예약 유형을 선택하세요</option>
        <option value="social">소셜</option>
        <option value="match">매치</option>
      </select>
    </div>
    <button class="btn btn-primary mt-2" @click="openConfirmModal">예약하기</button>
  </div>
</div>

    <!-- 예약 확인 모달 -->
    <div class="modal fade" tabindex="-1" :class="{ show: showModal }" style="display: block;" v-if="showModal">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">예약 확인</h5>
            {{ selectedDate }}
          </div>
          <div class="modal-body">
            <p>{{ reservation.date }} {{ reservation.time }}예약하시겠습니까?</p>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="showModal = false">아니오</button>
            <button class="btn btn-primary" @click="confirmReservation">예</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
  import { form } from '@/utils/script/reservation'
const {
  reservation,
  timeSlots,
  UserDB,
  stadiumDB,
  openConfirmModal,
  confirmReservation,

}=form()

</script>


<style scoped>
	.modal {
  background-color: rgba(0, 0, 0, 0.5);
}
</style>