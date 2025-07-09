<template>
  <div>
    <h2>클럽 가입 신청자 리스트</h2>
    <table class="apply-table"><!-- 추가: 클래스명 -->
      <thead>
        <tr>
          <th>신청자 번호</th>
          <th>신청자 이름</th>
          <th>신청 일시</th>
          <th>상태</th>
          <th>처리</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="apply in applyList" :key="apply.apply_id">
          <td>{{ apply.appli_user_no }}</td>
          <td>{{ apply.user_name || '-' }}</td>
          <td>{{ formatDate(apply.apply_date) }}</td>
          <td>{{ apply.status }}</td>
          <td>
            <button @click="approve(apply.apply_id)" :disabled="apply.status !== 'pending'">승인</button>
            <button @click="reject(apply.apply_id)" :disabled="apply.status !== 'pending'">거절</button>
          </td>
        </tr>
        <tr v-if="applyList.length === 0">
          <td colspan="5" style="text-align:center;">신청자가 없습니다.</td>
        </tr>
      </tbody>
    </table>
      <!-- ✅ 우측 하단 고정 뒤로가기 버튼 -->
     <button class="btn btn-secondary apply-back-btn" @click="goBack">뒤로가기</button>

  </div>
</template>

<style scoped>
.apply-table { /* 추가: 표 전체 스타일 */
  width: 100%;
  border-collapse: collapse;
  margin-top: 24px; /* 추가: 위 간격 */
  font-size: 16px;
  background: #fff;
  box-shadow: 0 1px 8px 0 rgba(0,0,0,0.04); /* 추가: 살짝 그림자 */
  border-radius: 8px;
  overflow: hidden;
}

.apply-table th, .apply-table td { /* 추가: 셀 스타일 */
  border-bottom: 1px solid #ececec;
  padding: 12px 8px;
  text-align: center;
}

.apply-table th { /* 추가: 헤더 스타일 */
  background: #f7f8fa;
  font-weight: 600;
}

.apply-table tr:last-child td { /* 추가: 마지막 행 border 제거 */
  border-bottom: none;
}

.apply-table button { /* 추가: 버튼 스타일 */
  padding: 5px 14px;
  border-radius: 5px;
  border: 1px solid #aaa;
  background: #f6f6f6;
  color: #333;
  margin: 0 2px;
  cursor: pointer;
  transition: background 0.2s;
}

.apply-table button:disabled { /* 추가: 비활성 버튼 스타일 */
  background: #e0e0e0;
  color: #aaa;
  border: 1px solid #ddd;
  cursor: not-allowed;
}





</style>


<script setup>
import { ref, onMounted } from 'vue'
import { useRoute,useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()  
const teamCode = route.params.teamCode
const clubId = ref(null)
const applyList = ref([])

onMounted(async () => {
  if (!teamCode) {
    alert('teamCode 파라미터가 필요합니다.')
    return
  }
  await fetchClubId()
})

async function fetchClubId() {
  try {
    const res = await axios.get('/club_api/idByTeamCode', { params: { teamCode } })
    clubId.value = res.data.club_id
    if (!clubId.value) {
      alert('club_id를 찾을 수 없습니다.')
      return
    }
    fetchApplyList()
  } catch (e) {
    alert('club_id 조회 실패')
    console.error(e)
  }
}

async function fetchApplyList() {
  try {
    const res = await axios.get('/club_api/apply/listWithName', { params: { club_id: clubId.value } })
    applyList.value = res.data
  } catch (e) {
    alert('가입 신청자 목록을 불러오지 못했습니다')
    console.error(e)
  }
}

async function approve(apply_id) {
  try {
    await axios.post('/club_api/apply/approve', { apply_id })
    alert('승인 처리 완료')
    fetchApplyList()
  } catch (e) {
    alert('승인 실패')
    console.error(e)
  }
}

async function reject(apply_id) {
  try {
    await axios.post('/club_api/apply/reject', { apply_id })
    alert('거절 처리 완료')
    fetchApplyList()
  } catch (e) {
    alert('거절 실패')
    console.error(e)
  }
}


function formatDate(dateStr) {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  if (isNaN(date)) return dateStr;
  const pad = n => n.toString().padStart(2, '0');
  return [
    date.getFullYear(),
    pad(date.getMonth() + 1),
    pad(date.getDate())
  ].join('-') + ' ' +
    [
      pad(date.getHours()),
      pad(date.getMinutes()),
      pad(date.getSeconds())
    ].join(':');
}



function goBack() {
  router.go(-1)
}

</script>

