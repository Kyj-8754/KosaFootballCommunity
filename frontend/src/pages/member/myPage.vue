<template>
  <div class="container-fluid main-container">
    <div class="row h-100">
      <main class="main-area">
        <div class="profile-wrapper">
          <div class="profile-info">
            <div class="user-header">
              <div class="user-text-group">
                <h2 class="user-name">{{ member?.userName || '불러오는 중...' }}</h2>
                <p class="user-meta">
                  {{ member?.userAddr || '불러오는 중...' }} {{ member?.userDetailAddr }}
                  <span v-if="member?.styleCode"> · {{ style.styleName }}</span>
                  <span v-if="member?.statCode"> · {{ stat.statName }}</span>
                </p>
                <div class="user-team">
                  <template v-for="(club, index) in myClubList" :key="club.clubId">
                    <router-link :to="`/club/${club.teamCode}`" class="club-link">{{ club.clubName }}</router-link>
                    <span v-if="index < myClubList.length - 1"> · </span>
                  </template>
                </div>
              </div>
            </div>
            <div class="info-box-group">
              <div class="info-box">
                <div class="info-title">나의 코드</div>
                <div class="info-value">{{ member?.userCode }}</div>
              </div>
              <div class="info-box">
                <div class="info-title">매너</div>
                <div class="info-value">{{ profileInfo?.manner == null ? '아직 평가를 받지 못했어요' : profileInfo.manner.toFixed(1) + '점' }}</div>
              </div>
              <div class="info-box">
                <div class="info-title">레벨</div>
                <div class="info-value">{{ getLevelLabel(profileInfo?.level) }}</div>
              </div>
            </div>
          </div>
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
const { member, style, stat, myClubList, profileInfo, showPasswordChangeBtn, isManager, getLevelLabel } = useMemberDetail()
</script>

<style scoped>
.profile-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 40px;
  flex-wrap: nowrap;
  width: 100%;
  padding: 32px 40px;
  font-family: 'Pretendard', sans-serif;
}

.profile-info {
  flex: 0 0 50%;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.user-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}
.user-text-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.user-name {
  font-size: 1.8rem;
  font-weight: 700;
  color: #212529;
  margin: 0;
}
.user-meta {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.4;
}
.user-team {
  font-size: 0.95rem;
  color: #444;
}
.club-link {
  color: #007bff;
  text-decoration: none;
}
.club-link:hover {
  text-decoration: underline;
}

.info-box-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.info-box {
  background-color: #f7f6f6f5;
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

.button-group {
  flex: 1;
  max-width: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 40px;
}
.button-group .btn {
  padding: 14px 20px;
  width: 260px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 24px;
  text-align: center;
}
</style>
