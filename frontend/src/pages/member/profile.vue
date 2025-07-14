<template>
  <div class="profile-wrapper">
    <!-- 왼쪽 영역 -->
    <div class="left-panel">
      <!-- 이름 + 버튼 수평 정렬 -->
      <div class="user-header">
        <h2 class="user-name">{{ member?.userName || '불러오는 중...' }}</h2>
        <div class="button-group">
          <button v-if="canGrantManager" class="btn btn-success" @click="grantManager">매니저 권한 부여</button>
          <button v-if="canRevokeManager" class="btn btn-danger" @click="revokeManager">매니저 권한 해제</button>
        </div>
      </div>

      <p class="user-info">
        {{ member?.userAddr || '불러오는 중...' }}
        <span v-if="member?.styleCode"> · {{ style.styleName }}</span>
        <span v-if="member?.statCode"> · {{ stat.statName }}</span>
      </p>

      <p class="user-info" v-if="myClubList.length">
        <template v-for="(club, index) in myClubList" :key="club.clubId">
          <router-link :to="`/club/${club.teamCode}`" class="btn btn-link p-0 m-0">{{ club.clubName }}</router-link>
          <span v-if="index < myClubList.length - 1"> · </span>
        </template>
      </p>

      <router-link v-if="member" :to="friendLink" class="friend-count router-link">{{ friends.length }}명의 친구</router-link>

      <router-link v-if="member && isMyProfile" :to="{ name: 'Member_Profile_Update' }" class="btn btn-primary">프로필 설정</router-link>

      <div class="comment-box">
        <h3>소개글</h3>
        <p class="user-comment">{{ member?.userComment || '소개글이 없습니다...' }}</p>
      </div>

      <div class="info-box">
        <p class="label">매너</p>
        <p class="value">{{ profileInfo?.manner == null ? '아직 평가를 받지 못했어요' : profileInfo.manner.toFixed(1) + '점' }}</p>
      </div>

      <div class="info-box">
        <p class="label">레벨</p>
        <p class="value">{{ getLevelLabel(profileInfo?.level) }}</p>
      </div>
    </div>

    <!-- 오른쪽 영역 -->
    <div class="right-panel">
      <div class="match-info">
        <div class="match-box">
          <p class="title">⚽ 경기</p>
          <p class="count">{{ profileInfo?.matchCount ?? 0 }}</p>
        </div>
        <div class="match-box">
          <p class="title">👑 POM</p>
          <p class="count">{{ profileInfo?.POMCount ?? 0 }}</p>
        </div>
      </div>

      <div class="card-info">
        <p>스마일 카드 <span class="card-count">{{ profileInfo?.smileCardCount ?? 0 }}</span></p>
        <p>옐로 카드 <span class="card-count">{{ profileInfo?.yellowCardCount ?? 0 }}</span></p>
        <p>레드 카드 <span class="card-count">{{ profileInfo?.redCardCount ?? 0 }}</span></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useProfileDetail } from '@/utils/script/user.js'

const {
  member,
  style,
  stat,
  friends,
  myClubList,
  profileInfo,
  isMyProfile,
  canGrantManager,
  canRevokeManager,
  getLevelLabel,
  friendLink,
  grantManager,
  revokeManager
} = useProfileDetail()
</script>

<style scoped>
.profile-wrapper {
  display: flex;
  gap: 48px;
  padding: 32px 40px;
  font-family: 'Pretendard', sans-serif;
}

/* 왼쪽 프로필 */
.left-panel {
  flex: 0 0 45%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 이름 + 버튼 수평 정렬 */
.user-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-size: 24px;
  font-weight: 700;
  color: #212529;
  margin: 0;
}

/* 버튼 */
.button-group {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.button-group button {
  min-width: 110px;
  font-size: 13px;
  padding: 6px 12px;
}

/* 주소, 포지션 등 */
.user-info {
  font-size: 15px;
  color: #666;
  line-height: 1.4;
  margin-top: -2px;
}

/* 친구 수 */
.friend-count {
  font-size: 16px;
  font-weight: 700;
  color: #2b2b2b;
  margin-top: 4px;
}

/* 소개글 */
.comment-box {
  margin-top: 18px;
}
.comment-box h3 {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 6px;
  color: #222;
}
.user-comment {
  font-size: 17px;
  color: #333;
  line-height: 1.6;
  white-space: pre-line;
}

/* 매너 / 레벨 */
.info-box {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  padding: 6px 0;
  border-bottom: 1px solid #f0f0f0;
}
.label {
  color: #888;
}
.value {
  font-weight: 600;
  color: #212529;
}

/* 오른쪽 패널 */
.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 경기 / POM */
.match-info {
  display: flex;
  gap: 20px;
}
.match-box {
  flex: 1;
  padding: 16px;
  border-radius: 8px;
  text-align: center;
  background-color: #f9f9f9;
}
.match-box .title {
  font-size: 13px;
  color: #777;
  margin-bottom: 4px;
}
.match-box .count {
  font-size: 20px;
  font-weight: 700;
  color: #222;
}

/* 카드 정보 */
.card-info {
  padding: 16px;
  border-radius: 8px;
  font-size: 14px;
  background-color: #f9f9f9;
}
.card-info p {
  display: flex;
  justify-content: space-between;
  margin: 6px 0;
  color: #444;
}
.card-count {
  font-weight: 600;
}

/* 공통 배경 제거 */
.left-panel,
.right-panel,
.comment-box,
.match-box,
.card-info {
  background: transparent;
  border: none;
  box-shadow: none;
}
</style>
