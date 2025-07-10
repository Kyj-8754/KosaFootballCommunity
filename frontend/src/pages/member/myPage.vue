<template>
  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <div class="container">
          <div class="card-header text-center">
            <h3 class="mb-0">회원 상세보기</h3>
          </div>

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
              <tr>
                <th class="text-start w-25">회원 코드</th>
                <td class="text-start">{{ member?.userCode || '불러오는 중...' }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="mt-4 d-flex justify-content-center gap-3" v-if="member">
          <router-link :to="{ name: 'Member_Profile', query: { userNo: member.userNo } }" class="btn btn-outline-primary">프로필보기</router-link>
          <router-link :to="{name: 'Home'}" class="btn btn-outline-primary">신청 내역</router-link>
          <router-link :to="{name: 'Home'}" class="btn btn-outline-primary">사용 내역</router-link>
          <router-link :to="{ name: 'Member_Friend' }" class="btn btn-outline-primary">친구</router-link>
          <router-link :to="{name: 'Home'}" class="btn btn-outline-primary">친구가 신청한 매치</router-link>
          <router-link :to="{ name: 'Member_UpdateForm' }" class="btn btn-primary">회원 정보 수정</router-link>
          <router-link v-if="showPasswordChangeBtn" :to="{name: 'Member_PassWordUpdateForm' }" class="btn btn-primary">비밀번호 변경</router-link>
          <router-link :to="{ name: 'Member_MemberDelete' }" class="btn btn-primary">회원 탈퇴</router-link>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, computed } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const token = inject('token')
const route = useRoute()
const member = ref(null)
const userNo = inject('userNo')
const loginType = inject('loginType')

const showPasswordChangeBtn = computed(() => loginType?.value === 'local')

onMounted(async () => {
  if (!userNo?.value) {
    console.warn('userNo가 없습니다.')
    return
  }

  try {
    const res = await axios.get(`/login_api/mypage/detailView?userNo=${userNo.value}`, {
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

<style scoped>
.main-container {
  display: flex;
  justify-content: center;
  align-items: start;
  padding: 40px 20px;
}

.main-area {
  display: flex;
  gap: 40px;
  width: 100%;
  max-width: 1200px;
}

.main-area .container {
  flex: 1;
}

.mt-4.d-flex {
  flex-direction: column !important;
  align-items: flex-start !important;
  gap: 12px !important;
  padding-left: 20px;
  border-left: 1px solid #ddd;
  margin-top: 0 !important;
}

.mt-4.d-flex .btn {
  width: 200px;
  text-align: left;
  padding: 10px 16px;
}
</style>
