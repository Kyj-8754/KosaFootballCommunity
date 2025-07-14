<template>
  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <div class="profile-wrapper">
          <!-- 왼쪽: 프로필 정보 -->
          <div class="profile-info">
            <div class="card-header">
              <h3 class="mb-0">회원 상세보기</h3>
            </div>

            <table class="table table-group-divider">
              <tbody>
                <tr>
                  <th class="text-start">이름</th>
                  <td class="text-start">{{ member?.userName || '불러오는 중...' }}</td>
                </tr>
                <tr>
                  <th class="text-start">주소</th>
                  <td class="text-start">{{ member?.userAddr || '불러오는 중...' }}</td>
                </tr>
                <tr>
                  <th class="text-start">상세 주소</th>
                  <td class="text-start">{{ member?.userDetailAddr || '불러오는 중...' }}</td>
                </tr>
                <tr>
                  <th class="text-start">회원 코드</th>
                  <td class="text-start">{{ member?.userCode || '불러오는 중...' }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 오른쪽: 버튼 목록 -->
          <div class="button-group" v-if="member">
            <router-link :to="{ name: 'Member_Profile', query: { userNo: member.userNo } }" class="btn btn-outline-primary">프로필보기</router-link>
            <router-link :to="{ name: 'Reservation_List' }" class="btn btn-outline-primary">예약 내역</router-link>
            <router-link v-if="isManager" :to="{ name: 'Payment_List' }" class="btn btn-outline-primary">결제 내역</router-link>
            <router-link :to="{ name: 'Member_Friend' }" class="btn btn-outline-primary">친구</router-link>
            <router-link :to="{ name: 'Member_UpdateForm' }" class="btn btn-primary">회원 정보 수정</router-link>
            <router-link v-if="showPasswordChangeBtn" :to="{ name: 'Member_PassWordUpdateForm' }" class="btn btn-primary">비밀번호 변경</router-link>
            <router-link :to="{ name: 'Member_MemberDelete' }" class="btn btn-primary">회원 탈퇴</router-link>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>


<script setup>
import { useMemberDetail } from '@/utils/script/user'
const { member, showPasswordChangeBtn, isManager } = useMemberDetail()
</script>

<style scoped>
.main-area {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
}

/* 전체 레이아웃 */
.profile-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 40px;
  flex-wrap: wrap;
}

/* 프로필 정보 영역 */
.profile-info {
  flex: 1;
  min-width: 300px;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.profile-field {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.field-row {
  display: flex;
  flex-direction: column;
}
.field-label {
  font-weight: 600;
  color: #495057;
  font-size: 0.95rem;
  margin-bottom: 4px;
}
.field-value {
  color: #212529;
  font-size: 1rem;
  word-break: break-word;
}

/* 제목 */
.card-header {
  margin-bottom: 1.5rem;
}
.card-header h3 {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
  text-align: left;
  color: #212529;
}

/* 버튼 그룹 */
.button-group {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  min-width: 220px;
}

.button-group .btn {
  padding: 10px 16px;
  width: 200px;
  font-size: 0.95rem;
  font-weight: 600;
  border-radius: 6px;
  text-align: left;
}
</style>
