<template>
  <div class="profile-wrapper">
    <div class="left-panel">
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
    </div>

    <div class="right-panel">
      <div class="card-rounded-box match-info">
        <div class="match-box">
          <p class="title">⚽ 경기</p>
          <p class="count">{{ profileInfo?.matchCount ?? 0 }}</p>
        </div>
        <div class="match-box">
          <p class="title">👑 POM</p>
          <p class="count">{{ profileInfo?.POMCount ?? 0 }}</p>
        </div>
      </div>

      <div class="card-rounded-box card-info">
        <p>스마일 카드 <span class="card-count">{{ profileInfo?.smileCardCount ?? 0 }}</span></p>
        <p>옐로 카드 <span class="card-count">{{ profileInfo?.yellowCardCount ?? 0 }}</span></p>
        <p>레드 카드 <span class="card-count">{{ profileInfo?.redCardCount ?? 0 }}</span></p>
      </div>
    </div>
    <div class="bottom-info-box-group">
      <div class="info-box">
        <div class="info-title">매너</div>
        <div class="info-value">
          {{ profileInfo?.manner == null ? '아직 평가를 받지 못했어요' : profileInfo.manner.toFixed(1) + '점' }}
        </div>
      </div>
      <div class="info-box">
        <div class="info-title">레벨</div>
        <div class="info-value">
          {{ getLevelLabel(profileInfo?.level) || '아직 평가를 받지 못했어요' }}
        </div>
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
  flex-wrap: wrap;
  gap: 48px;
  padding: 32px 40px;
  font-family: 'Pretendard', sans-serif;
}

.left-panel {
  flex: 0 0 45%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

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

.user-info {
  font-size: 15px;
  color: #666;
  line-height: 1.4;
  margin-top: -2px;
}

.user-info .btn-link {
  text-decoration: none !important;
  color: #007bff;
}

.friend-count {
  font-size: 16px;
  font-weight: 700;
  color: #2b2b2b;
  margin-top: 4px;
  text-decoration: none !important;
}

.comment-box {
  margin-top: 18px;
  background-color: #f7f6f6f5;
  border-radius: 20px;
  padding: 16px 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.04);
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

.bottom-info-box-group {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  width: 100%;
}

.bottom-info-box-group .info-box {
  flex: 1;
  background-color: #f7f6f6f5;
  border-radius: 30px;
  padding: 16px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.04);
  font-size: 15px;
  color: #333;
  font-weight: 600;
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

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.match-info {
  display: flex;
  align-items: center;
  gap: 20px;
  background-color: #f1f3f5;
  padding: 16px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.04);
  min-height: 120px;
}
.match-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
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

.card-info {
  padding: 16px;
  border-radius: 10px;
  font-size: 14px;
  background-color: #f1f3f5;
  box-shadow: 0 2px 4px rgba(0,0,0,0.04);
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

.left-panel,
.right-panel,
.match-box {
  background: transparent;
  border: none;
  box-shadow: none;
}

.card-rounded-box {
  background-color: #f7f6f6f5;
  border-radius: 40px;
  padding: 24px 32px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 120px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.match-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  text-align: center;
  gap: 40px;
  flex-direction: row;
  flex-wrap: nowrap;
}

.match-box .title {
  font-size: 14px;
  color: #444;
}

.match-box .count {
  font-size: 22px;
  font-weight: bold;
  margin-top: 4px;
}

.card-info p {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  margin: 4px 0;
  color: #333;
}

.card-info .card-count {
  font-weight: 600;
}

</style>
