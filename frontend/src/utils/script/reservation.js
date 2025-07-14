import axios from 'axios';
import { inject, onMounted, ref, onUnmounted, computed, watch} from 'vue';
import { useRoute, useRouter } from 'vue-router'
import {StadiumDataStore} from '@/stores/stadiumStore'

export function reservation_confirm(){
    const token = inject('token');
const router = useRouter();
const route = useRoute();
const reservation = ref({});
const user = ref({});
const stadium = ref({});
const userNo = inject('userNo') // 로그인한 유저 정보 가져옴
const authCode = inject('authCode') // 로그인한 유저 권한

console.log(authCode?.value, userNo.value);

// 결제 핸들러 이벤트, 감지해서 메시지를 띄우고 닫힘
const handlePaymentMessage = (event) => {
  switch (event.data) {
    case 'paymentSuccess':
      router.go(0);
      break;
    case 'paymentFail':
      break;
    case 'paymentCancel':
      break;
    default:
      console.warn("알 수 없는 결제 메시지:", event.data);
  }
}


onMounted(() => {
  loadReservationDetails(); // 함수 실행
  window.addEventListener('message', handlePaymentMessage); // 리스너 등록
});

onUnmounted(() => {
  window.removeEventListener('message', handlePaymentMessage)
})

const loadReservationDetails = async () =>{
  const reservation_id = route.params.reservationId;
  const res = await axios.post('/reservation_api/reservation/reservation_confirm', {
      reservation_id: reservation_id });

  reservation.value = res.data.reservationDB;
  const { user_no, svcid } = reservation.value;

  // 병렬로 사용자 정보와 구장 정보 가져오기
  const [userRes, stadiumRes] = await Promise.all([
    axios.get(`/login_api/mypage/detailView` , {params: { userNo: user_no },
      headers: {
      Authorization: `Bearer ${token.value}`
      }}
    ),
    axios.get(`/stadium_api/stadium/detailView`, { params: { SVCID: svcid } })
  ]);

  user.value = userRes.data.member;
  stadium.value = stadiumRes.data.stadiumDB.stadium;
  };

  const cancelMatchByTypeAndId = async (type, id) => {
    console.log("🛰️ 매치 상태 변경 요청: ", { type, id });

    try {
      const res = await axios.post('/board_api/match/matches/cancel', null, {
        params: {
          type,
          id
        }
      });
      console.log("✅ 매치 상태 변경 성공 응답:", res.data);
    } catch (error) {
      console.error("❌ 매치 상태 변경 실패:", error);
    }
  };

// 예약 취소
 const cancleReservation = async () => {

  const confirmPayment = confirm("정말 예약을 취소하시겠습니까?");
  if (!confirmPayment) return;

   try {
        await axios.post('/reservation_api/reservation/cancel',{
        reservation: reservation.value,
        user_no: userNo.value
    });
   // 성공 시 알림 띄우고, 페이지 이동
    alert('예약이 성공적으로 취소되었습니다.');

    // 💡 매치 상태도 취소로 변경
    await cancelMatchByTypeAndId("reservation", reservation.value.reservation_id);

    window.location.reload();
  } catch (err) {
    console.error(err);
    alert('예약 취소 실패: ' + (err.response?.data?.message || '서버 오류'));
  }

  }

// 결제 요청
const requestPayment = async () => {

  const confirmPayment = confirm("결제 하시겠습니까?");
  if (!confirmPayment) return;
  
  try{
    const res = await axios.post('/kakao_api/kakaopay/ready', {
      item_name: stadium.value.svcnm,
      total_amount: reservation.value.price,
      partner_order_id: reservation.value.reservation_id,
      partner_user_id: userNo.value,
      authCode: authCode.value
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



// 환불 요청
const refundPayment = async () => {

  const confirmCancel = confirm("정말 결제를 취소하시겠습니까?");
  if (!confirmCancel) return;  // 취소 시 함수 종료

  try{
    const res = await axios.post('/kakao_api/kakaopay/refund', {
      reservation: reservation.value,
      user_no: userNo.value
    });
 
    if (res.data.success) {
      alert("결제가 환불되었습니다.");

      // 💡 매치 상태도 취소로 변경
      await cancelMatchByTypeAndId("reservation", reservation.value.reservation_id);

      router.go(0);  // 새로고침
    } else {
      alert("환불 처리 중 문제가 발생했습니다.");
    }

  } catch (err) {
    if (err.response?.data?.message) {
      alert(err.response.data.message);
    } else {
      alert("결제 취소 중 오류가 발생했습니다.");
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


    return {
        reservation,
        cancleReservation,
        requestPayment,
        refundPayment,
    }
}




export function form(){
    
const router = useRouter();
const route = useRoute();
const SVCID = route.query.SVCID;
const date = route.query.date;

//구장 불러오기
const stadiumStore = StadiumDataStore()
const stadiumDB = stadiumStore.stadiumData

//아이디 관련
const token = inject('token')
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
  user_no: UserDB.value.user_no,   // 사용자 번호
  price: stadiumDB.price
})

const showModal = ref(false) // 모달창 띄우는 용도

// 날짜, 시간 검증 로직
const openConfirmModal = () => {
  if (!reservation.value.slot_id || !reservation.value.reservation_type) {
    alert('날짜와 시간을 모두 선택하세요.')
    return
  }
  showModal.value = true
}


// 예약하기
const confirmReservation = async () => {
  showModal.value = false
  try {
    const res = await axios.post(
      '/reservation_api/reservation/reservation_std',
      reservation.value,
      { headers: { 'Content-Type': 'application/json' } }
    );

    if (res.data.res_code === '200') {
    alert(res.res_msg);

    // 🎯 stadium 정보 초기화
    // stadiumStore.clearStadium();
    const reservationId = res.data.reservation_id;
    router.push({name: 'reservation_Confirm', params: {reservationId}});
  }
  } catch (error) {
    alert("서버 오류가 발생했습니다.");
    console.error(error);
  }
}

	// 유저 데이터 가져오기
	const fetchUserData = async () => {
		const res = await axios.get('/login_api/mypage/detailView', {params: { userNo: user_no.value },headers: {
        Authorization: `Bearer ${token.value}`
      }});
		UserDB.value = res.data.member;
	};

	// 예약 관련 가져오기
	const fetchReservationData = async () => {
		const res = await axios.post('/reservation_api/reservation/reservationForm', { SVCID: SVCID , date: date } )
    .then(res => {
      if (res.data.res_code === '200'){
        reservationDB.value = res.data.slots;
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

return {
    stadiumDB,
  openConfirmModal,
  confirmReservation,

}
}