<template>
  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <div class="container mt-5">
          <div class="card-header text-center">
            <h3 class="mb-0">회원 상세보기</h3>
          </div>

          <!-- 데이터가 있을 때만 렌더링 -->
          <table class="table table-group-divider">
            <tbody>
              <tr>
                <th class="text-start">이름</th>
                <td class="text-start">{{ member?.userName || '불러오는 중...' }}</td>
              </tr>
              <tr>
                <th class="text-start w-25">주소</th>
                <td class="text-start">{{ member?.userAddr || '불러오는 중...' }}</td>
              </tr>
              <tr>
                <th class="text-start w-25">상세 주소</th>
                <td class="text-start">{{ member?.userDetailAddr || '불러오는 중...' }}</td>
              </tr>
            </tbody>
          </table>

          <div class="mt-4 d-flex justify-content-center gap-3" v-if="member">
            <router-link :to="{name: 'Home'}" class="btn btn-outline-primary">프로필보기</router-link>
            <router-link :to="{name: 'Home'}" class="btn btn-outline-primary">신청내역</router-link>
            <router-link :to="{name: 'Home'}" class="btn btn-outline-primary">사용 내역</router-link>
            <router-link :to="{name: 'Member_Friend', query: { userNo: member.userNo }}" class="btn btn-outline-primary">친구</router-link>
            <router-link :to="{name: 'Home'}" class="btn btn-outline-primary">친구가 신청한 매치</router-link>
            <router-link :to="{name: 'Member_UpdateForm', query: { userNo: member.userNo }}" class="btn btn-primary">프로필 수정</router-link>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const token = inject('token')
const route = useRoute()
const member = ref(null)

onMounted(async () => {
  const userNo = route.query.userNo // 쿼리 파라미터에서 userNo 받기
  if (!userNo) {
    console.warn('userNo 쿼리 파라미터가 없습니다.')
    return
  }

  try {
    const res = await axios.get(`/login_api/mypage/detailView?userNo=${userNo}`, {
  headers: {
    Authorization: `Bearer ${token.value}`
  }
})
    member.value = res.data.member
  } catch (err) {
    console.error('회원 정보 조회 실패:', err)
  }
})
</script>
