<template>
  <div class="container py-5">
    <h2 class="fw-bold mb-4 text-center">클럽 전체 멤버</h2>
    <div class="card shadow-sm">
      <div class="card-body p-0">
        <table class="table mb-0 align-middle">
          <thead class="table-light">
            <tr>
              <th style="min-width:120px;" class="text-start ps-4">이름</th>
              <th style="width:90px" class="text-center">참가수</th>
              <th style="width:80px" class="text-center">POM</th>
              <th style="width:130px" class="text-center">가입일</th>
              <th style="width:80px" class="text-center"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="member in clubMember" :key="member.user_no">
              <td class="fw-semibold align-middle text-start ps-4">{{ member.user_name }}</td>
              <td class="text-center">
                <span class="badge bg-primary bg-opacity-75 fs-7">
                  {{ member.match_count }}
                </span>
              </td>
              <td class="text-center">
                <span class="badge bg-warning text-dark fs-7">
                  {{ member.pom }}
                </span>
              </td>
              <td class="text-center">
                <span class="text-muted">{{ member.joined_at?.substring(0,10) }}</span>
              </td>
              <td class="text-center">
                <!-- 팀장일 때만 강퇴 버튼 노출, 본인(userNo)이 아니면 -->
                <button
                  v-if="club && userNo && club.user_no === userNo && member.user_no !== userNo"
                  @click="kickMember(member.user_no)"
                  class="btn btn-warning btn-sm"
                >
                  강퇴
                </button>
              </td>
            </tr>
            <tr v-if="clubMember.length === 0">
              <td colspan="5" class="text-center text-muted">등록된 멤버가 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
        <div class="mt-4 text-end">
      <!-- 일반 멤버(팀장 제외, 본인만)에게만 노출되는 클럽 탈퇴 버튼 -->
      <button
        v-if="club && userNo && club.user_no !== userNo && clubMember.some(m => m.user_no === userNo)"
        class="btn btn-outline-danger me-2"
        @click="withdrawMember"
      >클럽 탈퇴</button>
      <button class="btn btn-outline-secondary" @click="goBack">뒤로가기</button>
    </div>
  </div>
</template>



<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const token = inject('token')
const userNo = inject('userNo') // 작업중
const router = useRouter()
const route = useRoute()

const club = ref(null)         // 클럽 기본 정보
const clubMember = ref([])     // 전체 멤버 리스트

onMounted(async () => {
  const teamCode = route.params.teamCode
  try {
    // 1. teamCode로 클럽 객체(club_id 포함) 조회
    const response = await axios.get(`/club_api/code/${teamCode}`, {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    club.value = response.data

    // 2. club_id로 멤버 리스트 조회
    if (club.value && club.value.club_id) {
      const memberRes = await axios.get(`/club_api/member/list/${club.value.club_id}`, {
        headers: { Authorization: `Bearer ${token.value}` }
      })
      clubMember.value = memberRes.data
    }
  } catch (error) {
    alert('클럽 멤버 리스트 조회 실패')
    router.back()
  }
})

const goBack = () => router.back()

async function kickMember(memberUserNo) {
  if (!club.value || !club.value.club_id) {
    alert('클럽 정보가 올바르지 않습니다.');
    return;
  }
  if (!confirm('정말 이 멤버를 강퇴하시겠습니까?')) return;
  try {
    await axios.post(
      '/club_api/apply/kick',
      null, // POST의 바디는 필요 없음 (params로만 전달)
      {
        params: {
          club_id: club.value.club_id,
          user_no: memberUserNo
        },
        headers: { Authorization: `Bearer ${token.value}` }
      }
    );
    alert('강퇴 처리 완료');
    // 멤버 리스트 새로고침
    const memberRes = await axios.get(`/club_api/member/list/${club.value.club_id}`, {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    clubMember.value = memberRes.data
  } catch (e) {
    alert('강퇴 처리 실패: ' + (e.response?.data || ''));
    console.error(e);
  }
}


async function withdrawMember() {
  if (!club.value || !club.value.club_id) {
    alert('클럽 정보가 올바르지 않습니다.');
    return;
  }
  if (!confirm('정말 클럽에서 탈퇴하시겠습니까?')) return;
  try {
    await axios.post(
      '/club_api/apply/withdraw',
      null,
      {
        params: {
          club_id: club.value.club_id,
          user_no: userNo.value
        },
        headers: { Authorization: `Bearer ${token.value}` }
      }
    );
    alert('클럽에서 정상적으로 탈퇴하였습니다.');
    router.push('/club'); // 탈퇴 후 이동 경로
  } catch (e) {
    alert('클럽 탈퇴 실패: ' + (e.response?.data || ''));
    console.error(e);
  }
}

</script>

