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
            </tr>
            <tr v-if="clubMember.length === 0">
              <td colspan="4" class="text-center text-muted">등록된 멤버가 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div class="mt-4 text-end">
      <button class="btn btn-outline-secondary" @click="goBack">뒤로가기</button>
    </div>
  </div>
</template>




<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const token = inject('token')
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
</script>

