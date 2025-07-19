<template>
  <div class="payment-list">
    <div v-if="!reservations || reservations.length === 0">결제 정보가 없습니다.</div>
    <ul v-else>
      <li class="list-header">
        <span class="num-col">번호</span>
        <span class="name-col">구장명</span>
        <span class="date-col" @click="toggleSort('slot_date')" style="cursor: pointer">
          결제일
          <span v-if="sortKey === 'slot_date'">({{ sortOrder === 'asc' ? '▲' : '▼' }})</span>
        </span>
        <span class="amount-col">결제 금액</span>
        <span class="status-col" @click="toggleSort('status')" style="cursor: pointer">
          결제 상태
          <span v-if="sortKey === 'status'">({{ sortOrder === 'asc' ? '▲' : '▼' }})</span>
        </span>
        </li>
        <li v-for="(item, index) in pagedReservations" :key="item.reservation_id" class="payment-item" @click="goToDetail(item.reservation_id)"> 
              <span class="num-col">{{ startIndex + index + 1 }}</span>
              <span class="name-col">{{ item.svcnm }}</span>
              <span class="date-col">{{ item.paid_at ? item.paid_at : '-' }}</span>
              <span class="amount-col">{{ item.amount }}</span>
              <span 
                class="status-col" 
                :class="{
                  paid: item.status === 'paid',
                  refunded: item.status === 'refunded',
                  pending: item.status === 'pending' || item.status === '미 결제'
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

    <div class="total-amount">
  총 결제 금액: {{ totalPaidAmount.toLocaleString() }} 원
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

      const statusPriority = {
        paid: 1,
        refunded: 2,
        unpaid: 3,
        canceled: 4
      };

      if (sortKey.value === 'status') {
        const aPriority = statusPriority[aVal] || 99;
        const bPriority = statusPriority[bVal] || 99;

        return sortOrder.value === 'asc'
          ? aPriority - bPriority
          : bPriority - aPriority;
      }

    return 0;
  });
});

// 오름차순, 내림차순용도
const toggleSort = (key) => {
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortKey.value = key;
    sortOrder.value = 'asc';
  }
};



// 결제 금액
const totalPaidAmount = computed(() => {
  return reservations.value
    .filter(item => item.status === 'paid')
    .reduce((sum, item) => sum + Number(item.amount || 0), 0);
});


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
  const res = await axios.post('/reservation_api/reservation/paymet_list', {
      user_no: props.user_no   // 필요 시 props로 받아 처리 가능
  })
  reservations.value = res.data.reservationList
}

// 예약상태명 바꾸기
const convertStatus = (status) => {
  switch (status) {
    case 'paid':
      return '결제 완료';
    case 'canceled':
      return '예약 취소';
    case 'pending':
      return '미 결제';
    case 'refunded':
      return '환불 됨';
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
.payment-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.list-header,
.payment-item {
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

.status-col.paid {
  background-color: #eafaf1;
  color: #219653;
}

.status-col.refunded {
  background-color: #fdeaea;
  color: #d93025;
}

.status-col.pending {
  background-color: #fdeaea;
  color: #d93025;
}

.payment-item:hover {
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

.total-amount {
  text-align: right;
  font-weight: bold;
  margin-top: 12px;
  font-size: 16px;
  color: #333;
}
</style>