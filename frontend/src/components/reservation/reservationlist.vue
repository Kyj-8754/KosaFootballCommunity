<template>
  <div class="reservation-list">
    <div v-if="!reservations || reservations.length === 0">예약 정보가 없습니다.</div>
    <ul v-else>
      <li class="list-header">
        <span class="num-col">번호</span>
        <span class="name-col">구장명</span>
        <span class="match-col">리그/매치</span>
         <span class="date-col" @click="toggleSort('slot_date')" style="cursor: pointer">
    예약일
    <span v-if="sortKey === 'slot_date'">({{ sortOrder === 'asc' ? '▲' : '▼' }})</span>
  </span>
  <span class="status-col" @click="toggleSort('status')" style="cursor: pointer">
    상태
    <span v-if="sortKey === 'status'">({{ sortOrder === 'asc' ? '▲' : '▼' }})</span>
  </span>
      </li>
      <li v-for="(item, index) in pagedReservations" :key="item.reservation_id" class="reservation-item" @click="goToDetail(item.reservation_id)"> 
              <span class="num-col">{{ startIndex + index + 1 }}</span>
              <span class="name-col">{{ item.svcnm }}</span>
              <span class="match-col">{{ convertType(item.reservation_type) }}</span>
              <span class="date-col">{{ item.slot_date }}</span>
              <span 
                class="status-col" 
                :class = "{
                reserved: item.status === 'reserved',
                expired: item.status === 'expired',
                cancelled: item.status === 'cancelled'
                }"
              >
            {{ convertStatus(item.status) }}
          </span>
      </li>
    </ul>
    <!-- 페이지네이션 버튼 -->
    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 1">이전</button>
      <span>페이지 {{ currentPage }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="endIndex >= reservations.length">다음</button>
    </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
const router = useRouter();

// props 받기
const props = defineProps({
  user_no: {
    type: [String, Number],
    required: true
  }
})

// 예약 필터
const sortKey = ref(null);  // "slot_date" or "status"
const sortOrder = ref('asc'); // 'asc' or 'desc'

const sortedReservations = computed(() => {
  const sorted = [...reservations.value];
  if (!sortKey.value) return sorted;

  return sorted.sort((a, b) => {
    const aVal = a[sortKey.value];
    const bVal = b[sortKey.value];

    // 날짜 비교
    if (sortKey.value === 'slot_date') {
      const dateA = new Date(aVal);
      const dateB = new Date(bVal);
      return sortOrder.value === 'asc' ? dateA - dateB : dateB - dateA;
    }

    // 문자열 비교 (status)
    if (sortKey.value === 'status') {
      if (aVal < bVal) return sortOrder.value === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortOrder.value === 'asc' ? 1 : -1;
      return 0;
    }

    return 0;
  });
});

const toggleSort = (key) => {
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortKey.value = key;
    sortOrder.value = 'asc';
  }
};



//페이지 버튼, 버튼 이벤트
const currentPage = ref(1);
const pageSize = 10;
const totalPages = computed(() => {
  return Math.ceil(reservations.value.length / pageSize);
});

const startIndex = computed(() => (currentPage.value - 1) * pageSize);
const endIndex = computed(() => currentPage.value * pageSize);


const pagedReservations = computed(() => sortedReservations.value.slice(startIndex.value, endIndex.value));

const nextPage = () => {
  if (endIndex.value < reservations.value.length) currentPage.value++;
};
const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};

// api로 받아온 데이터
const reservations = ref([])

const fetchReservations = async () => {
  const res = await axios.post('/reservation_api/reservation/list', {
      user_no: props.user_no   // 필요 시 props로 받아 처리 가능
  })
  reservations.value = res.data.reservationList
}

// 예약상태명 바꾸기
const convertStatus = (status) => {
  switch (status) {
    case 'reserved':
      return '예약';
    case 'cancelled':
      return '예약 취소';
    case 'expired':
      return '예약 기간 만료';
    default:
      return '알 수 없음';
  }
};

// 리그 상태명 바꾸기
const convertType = (reservation_type) => {
  switch (reservation_type) {
    case 'social':
      return '소셜';
    case 'match':
      return '리그';
    default:
      return '알 수 없음';
  }
};


// 이동
const goToDetail = (id) => {
  router.push({ name: 'reservation_Confirm', params: { reservationId: id } });
};


onMounted(fetchReservations)
</script>

<style scoped>
.reservation-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.list-header,
.reservation-item {
  display: flex;
  padding: 12px;
  border-bottom: 1px solid #eee;
  align-items: center;
  cursor: pointer;
}

.list-header {
  background-color: #f8f8f8;
  font-weight: bold;
}

.num-col {
  width: 50px;
  text-align: center;
}

.name-col {
  flex: 2;
  word-break: break-word;
}

.date-col {
  flex: 1.5;
  text-align: center;
}

.status-col {
  flex: 1;
  text-align: center;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 13px;
}

.status-col.reserved {
  background-color: #eafaf1;
  color: #219653;
}

.status-col.cancelled {
  background-color: #fdeaea;
  color: #d93025;
}

.status-col.expired {
  background-color: #f0f0f0;
  color: #757575;
}

.reservation-item:hover {
  background-color: #f1f8ff;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 16px;
}

.pagination button {
  border: none;
  background-color: #f0f0f0;
  padding: 6px 12px;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:hover:not(:disabled) {
  background-color: #ddd;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>