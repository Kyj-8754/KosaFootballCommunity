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
const teamCode = route.params.teamCode
const clubId = ref(null)
const applyList = ref([])

// 1. teamCode로 club_id 조회
async function fetchClubIdAndApplyList() {
  try {
    // club_id 먼저 조회 (API는 반드시 team_code → club_id를 반환해야 함)
    const res = await axios.get('/club/info', { params: { team_code: teamCode } })
    clubId.value = res.data.club_id
    // club_id로 신청자 리스트 조회
    fetchApplyList()
  } catch (e) {
    alert('클럽 정보 조회 실패')
  }
}

// 2. club_id로 신청자 리스트 조회
async function fetchApplyList() {
  if (!clubId.value) return
  try {
    const res = await axios.get('/club/apply/list', { params: { club_id: clubId } })
    applyList.value = res.data
  } catch (e) {
    alert('가입 신청자 목록을 불러오지 못했습니다')
  }
}

// 승인 처리
async function approve(apply_id) {
  try {
    await axios.post('/club/apply/approve', { apply_id })
    alert('승인 처리 완료')
    fetchApplyList()
  } catch (e) {
    alert('승인 실패')
  }
}

// 거절 처리
async function reject(apply_id) {
  try {
    await axios.post('/club/apply/reject', { apply_id })
    alert('거절 처리 완료')
    fetchApplyList()
  } catch (e) {
    alert('거절 실패')
  }
}

onMounted(fetchClubIdAndApplyList)
</script>
