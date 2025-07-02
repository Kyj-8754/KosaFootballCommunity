<template>
  <div>
    <h2>클럽 가입 신청자 리스트</h2>
    <table>
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
          <td>{{ apply.apply_date }}</td>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const teamCode = route.params.teamCode // 주소에서 추출
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
    console.log('[fetchClubId] 응답:', res.data)
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
    console.log('[fetchApplyList] club_id:', clubId.value)
   const res = await axios.get('/club_api/apply/listWithName', { params: { club_id: clubId.value } })
    console.log('[fetchApplyList] 신청자 응답:', res.data)
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
</script>
