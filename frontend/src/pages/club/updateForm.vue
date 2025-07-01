<template>
  <div class="container py-5">
    <h2 class="fw-bold mb-4 text-center">클럽 정보 등록 및 수정</h2>

    <div v-if="club">
      <div class="row g-4">
        <!-- 이미지 미리보기 -->
        <div class="col-md-3">
          <div class="border rounded p-3 text-center h-100">
            <img
              :src="club.logo_path || 'https://via.placeholder.com/100'"
              alt="클럽 로고"
              class="img-fluid mb-2"
              style="max-height: 100px; object-fit: contain;"
            />
            <small class="text-muted">이미지 변경은 별도 화면에서</small>
          </div>
        </div>

        <!-- 폼 4칸 -->
        <div class="col-md-9">
          <div class="row row-cols-2 g-3">
            <!-- 경기 전적 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>경기 전적</strong>
                <input type="number" :value="club.win_count" class="form-control my-1" placeholder="승" readonly />
                <input type="number" :value="club.draw_count" class="form-control my-1" placeholder="무" readonly />
                <input type="number" :value="club.loss_count" class="form-control my-1" placeholder="패" readonly />
              </div>
            </div>

            <!-- 주요 멤버 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>주요 멤버 리스트</strong>
                <ul class="list-unstyled mt-2 text-muted">
                  <li>여기엔 멤버 리스트가 들어감 (클릭시 멤버 전체 리스트 이동)</li>
                </ul>
              </div>
            </div>

            <!-- 간단한 팀 정보 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>간단한 팀 정보(주 경기 시간, 나이대, 성별, 팀 레벨)</strong>
                <textarea
                  v-model="club.simple_info"
                  class="form-control"
                  rows="4"
                  placeholder="간단한 클럽 정보를 입력하세요"
                ></textarea>
              </div>
            </div>

            <!-- 팀 소개 -->
            <div class="col">
              <div class="border rounded p-3 h-100">
                <strong>간단한 팀 소개</strong>
                <textarea
                  v-model="club.description"
                  class="form-control"
                  rows="4"
                  placeholder="클럽 설명을 입력하세요"
                ></textarea>
              </div>
            </div>
          </div>
        </div>
      </div>
<!-- 
      저장/취소 버튼: 팀장(user_no)만 --> 
  <div v-if="club && userNo && club.user_no === userNo">
  <button @click="submitUpdate">저장하기</button>
  <button @click="cancelUpdate">취소하기</button>
</div> 

<div v-else>
  <span class="text-muted">팀장만 클럽 정보를 수정할 수 있습니다.</span>
</div>

    </div>
    <p v-else class="text-muted mt-5 text-center">클럽 정보를 불러오는 중입니다...</p>
  </div>
</template>


<script setup>
import { ref, inject, computed, onMounted, watchEffect } from 'vue' // watchEffect 추가
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const token = inject('token')
const userNo = inject('userNo') // 반드시 userNo (ref 타입이어야 함)

const router = useRouter()
const route = useRoute()

const club = ref(null)

// 팀장 여부: club.user_no와 userNo.value를 모두 Number로 변환해서 비교 (타입 불일치 방지)
const isClubOwner = computed(() =>
  club.value && userNo && Number(club.value.user_no) === Number(userNo.value) // ← 수정!
)

// onMounted(async () => {
//   const teamCode = route.params.teamCode
//   try {
//     const response = await axios.get(`/club_api/code/${teamCode}`, {
//       headers: { Authorization: `Bearer ${token.value}` }
//     })
//     club.value = response.data
//   } catch (e) {
//     alert('클럽 정보를 불러오는 데 실패했습니다.')
//   }
// })

onMounted(async () => {
  const teamCode = route.params.teamCode
  try {
    const response = await axios.get(`/club_api/code/${teamCode}`, {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    club.value = response.data
    console.log('클럽 전체 객체:', club.value) // ← 이거 추가!
  } catch (e) {
    alert('클럽 정보를 불러오는 데 실패했습니다.')
  }
})


const submitUpdate = async () => {
  if (!isClubOwner.value) {
    alert('팀장만 수정할 수 있습니다.')
    return
  }
  try {
    await axios.put(`/club_api/${club.value.club_id}`, club.value, {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    alert('수정이 완료되었습니다.')
    router.push(`/club/${club.value.team_code}`)
  } catch (e) {
    alert('수정 중 오류가 발생했습니다.')
  }
}

const cancelUpdate = () => {
  if (club.value) {
    router.push(`/club/${club.value.team_code}`)
  } else {
    router.back()
  }
}

// 실제 값과 타입을 콘솔에서 확인 (Number 변환해서 출력)
watchEffect(() => {
  console.log('userNo:', userNo && userNo.value, 'type:', typeof (userNo && userNo.value)); // ← 타입도 확인
  console.log('club.user_no:', club.value && club.value.user_no, 'type:', typeof (club.value && club.value.user_no)); // ← 타입도 확인
  console.log('같은가?:', club.value && userNo && Number(club.value.user_no) === Number(userNo.value)); // ← Number로 변환 후 비교
});
</script>
