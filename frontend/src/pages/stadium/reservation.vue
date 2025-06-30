<template>
    
	<h2>결제창 만들거임 ㅇㅋ? </h2>

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
        <p>이름: {{ userName }}</p>
      </div>
    </div>

    <!-- 📅 예약 정보 -->
    <div class="card mb-3">
      <div class="card-header">예약 선택</div>
      <div class="card-body">
        <div class="mb-3">
          <label for="date" class="form-label">예약 날짜</label>
            {{ selectedDate }}
        </div>
        <div class="mb-3">
          <label for="time" class="form-label">예약 시간</label>
          <select class="form-select" v-model="reservation.time">
            <option disabled value="">시간을 선택하세요</option>
            <option v-for="time in timeSlots" :key="time">{{ time }}</option>
          </select>
        </div>
        <button class="btn btn-primary" @click="openConfirmModal">예약하기</button>
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
import { ref, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute();
const SVCID = route.query.SVCID;
const selectedDate = route.query.date;

	//아이디 관련
const userId = inject('userId')
const userName = inject('userName')
const stadiumDB = ref({ list: [] })	// 게시물 

const user = ref({
  name: '홍길동',
  phone: '010-1234-5678'
})

const timeSlots = ref([
  '06:00', '07:00', '08:00', '09:00',
  '10:00', '11:00', '12:00', '13:00',
  '14:00', '15:00', '16:00', '17:00',
  '18:00', '19:00', '20:00', '21:00'
])

const reservation = ref({
  date: selectedDate,
  time: ''
})

const showModal = ref(false)

const openConfirmModal = () => {
  if (!reservation.value.date || !reservation.value.time) {
    alert('날짜와 시간을 모두 선택하세요.')
    return
  }
  showModal.value = true
}

const confirmReservation = () => {
  showModal.value = false
  alert('예약이 완료되었습니다.')
  // 여기에 axios 등으로 POST 처리 가능
}

	// 게시물 불러오기
	const fetchStadiumData = async () => {
		const res = await axios.get('/stadium_api/stadium/detailView', { params: { SVCID } });
		stadiumDB.value = res.data.stadiumDB;
	};	

onMounted(async () => {
 await fetchStadiumData();
})
</script>


<style scoped>
	.modal {
  background-color: rgba(0, 0, 0, 0.5);
}
</style>