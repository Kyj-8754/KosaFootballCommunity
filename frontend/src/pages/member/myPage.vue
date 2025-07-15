<template>
  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <div class="profile-wrapper">
          <!-- 왼쪽: 사용자 정보 -->
          <div class="profile-info">
            <div class="user-name">{{ member?.userName }}</div>
            <div class="user-addr">
              {{ member?.userAddr }} {{ member?.userDetailAddr }} · 수비 · 스피드
            </div>
            <div class="user-team">
              서울FC, 안양FC
            </div>

            <div class="info-box-group">
              <div class="info-box">
                <div class="info-title">나의 코드</div>
                <div class="info-value">{{ member?.userCode }}</div>
              </div>
              <div class="info-box">
                <div class="info-title">매너</div>
                <div class="info-value">아직 평가 받지 못했어요</div>
              </div>
              <div class="info-box">
                <div class="info-title">레벨</div>
                <div class="info-value">세미프로</div>
              </div>
            </div>
          </div>

          <!-- 오른쪽: 버튼 영역 -->
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
.profile-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 40px;
  flex-wrap: nowrap;
  width: 100%;
}

/* 왼쪽 프로필 영역 */
.profile-info {
  flex: 1;
  max-width: 50%;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

/* 오른쪽 버튼 영역 */
.button-group {
  flex: 1;
  max-width: 50%;
  display: flex;
  flex-direction: column;
  align-items: center; /* 👉 중앙 정렬 */
  justify-content: center;
  gap: 16px;
  margin-top: 40px;
}

/* 버튼 스타일 */
.button-group .btn {
  padding: 14px 20px; /* 👉 높이 키움 */
  width: 260px;       /* 👉 너비 넓힘 */
  font-size: 1rem;
  font-weight: 600;
  border-radius: 24px;
  text-align: center;
}

/* 사용자 이름 등 */
.user-name {
  font-size: 1.6rem;
  font-weight: 700;
  margin-bottom: 6px;
}
.user-addr,
.user-team {
  font-size: 0.95rem;
  color: #555;
}

/* 코드/매너/레벨 카드 */
.info-box-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 20px;
}
.info-box {
  background-color: #e9e9e9;
  border-radius: 30px;
  padding: 16px;
  text-align: center;
}
.info-title {
  font-size: 0.95rem;
  color: #333;
  margin-bottom: 6px;
}
.info-value {
  font-size: 1.1rem;
  font-weight: bold;
  color: #111;
}
</style>
