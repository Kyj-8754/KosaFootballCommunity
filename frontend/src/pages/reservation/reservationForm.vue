<template>
    
	<h2>구장 예약</h2>

 <div class="container mt-5">
    <!-- 🏟 구장 정보 -->
    <div class="card mb-3">
      <div class="card-header">구장 정보</div>
      <div class="card-body">
        <h5 class="card-title">{{ stadiumDB.svcnm }}</h5>
        <p class="card-text">주소: {{ stadiumDB.adres }}</p>
        <p class="card-text">운영시간: {{ stadiumDB.v_MIN }} ~ {{ stadiumDB.v_MAX }}</p>
      </div>
    </div>

    <!-- 🙋‍♂️ 유저 정보 -->
    <div class="card mb-3">
      <div class="card-header">예약자 정보</div>
      <div class="card-body">
        <p>이름: {{ UserDB.userName }}</p>
        <p>전화 번호: {{ UserDB.userPhone }}</p>
      </div>
    </div>

    <!-- 📅 예약 정보 -->
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

    <!-- ✅ 예약 확인 모달 -->
    <div class="modal fade" tabindex="-1" :class="{ show: showModal }" style="display: block;" v-if="showModal">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">예약 확인</h5>
            {{ selectedDate }}
          </div>
          <div class="modal-body">
            <p>{{ reservation.date }} {{ reservation.time }}에 예약하시겠습니까?</p>
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
import axios from 'axios'
import { ref, onMounted, inject, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {StadiumDataStore} from '@/stores/stadiumStore'

const router = useRouter();
const route = useRoute();
const SVCID = route.query.SVCID;
const date = route.query.date;

//구장 불러오기
const stadiumStore = StadiumDataStore()
const stadiumDB = stadiumStore.stadiumData

//아이디 관련
const user_no = inject('userNo')
const reservationDB = ref({})	// 예약
const UserDB = ref({}) // 유저

watch(UserDB, (newVal) => {
  if (newVal && newVal.userNo) {
    reservation.value.user_no = newVal.userNo;
  }
});

const reservation = ref({
  slot_id: '',               // 선택한 시간 슬롯 ID
  reservation_type: '',                 // 'social' or 'match'
  user_no: UserDB.value.user_no   // 사용자 번호
})

const showModal = ref(false)

const openConfirmModal = () => {
  if (!reservation.value.slot_id || !reservation.value.reservation_type) {
    alert('날짜와 시간을 모두 선택하세요.')
    return
  }
  showModal.value = true
}

const confirmReservation = async () => {
  showModal.value = false
  console.log(reservation.value);
  try {
    const res = await axios.post(
      '/reservation_api/reservation/reservation_std',
      reservation.value,
      { headers: { 'Content-Type': 'application/json' } }
    );

    const reservationId = res.data.reservation_id;
    alert(res.data); // "예약이 완료되었습니다." 또는 실패 메시지
    router.push({name: 'reservation_Confirm', params: {reservationId}})
  } catch (error) {
    alert("서버 오류가 발생했습니다.");
    console.error(error);
  }
}

	// 유저 데이터 가져오기
	const fetchUserData = async () => {
		const res = await axios.get('/login_api/mypage/detailView', {params: { userNo: user_no.value }});
		UserDB.value = res.data.member;
	};

	// 예약 관련 가져오기
	const fetchReservationData = async () => {
		const res = await axios.post('/reservation_api/reservation/reservationForm', { SVCID: SVCID , date: date } )
    .then(res => {
      if (res.data.res_code === '200'){
        reservationDB.value = res.data.slots;
        console.log(reservationDB.value);
      } else {
        alert(res.data.res_msg);
      }
    });
	};	

onMounted(async () => {
 await fetchReservationData();
 await fetchUserData();
})


const timeSlots = computed(() => {
  if (!Array.isArray(reservationDB.value)) return [];  // 확실하게 배열 체크
  return reservationDB.value.map(slot => {
    const timeRange = `${slot.startTime} ~ ${slot.endTime}`;
    return {
      label: slot.reservationStatus === 'reserved'
        ? `${timeRange} (예약됨)`
        : timeRange,
      value: slot.slotid,
      disabled: slot.reservationStatus === 'reserved'
    };
  });
});
</script>


<style scoped>
	.modal {
  background-color: rgba(0, 0, 0, 0.5);
}
</style>