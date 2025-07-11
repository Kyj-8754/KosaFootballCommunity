<template>
  <div class="profile-wrapper">
    <!-- 왼쪽 영역 -->
    <div class="left-panel">
      <h2 class="user-name">{{ member?.userName || '불러오는 중...' }}</h2>
      <p class="user-info">
        {{ member?.userAddr || '불러오는 중...' }}
        <span v-if="member?.styleCode">
          · {{ style.styleName }}
        </span>
        <span v-if="member?.statCode">
          · {{ stat.statName }}
        </span>
      </p>
      <p class="user-info" v-if="myClubList.length">
        <template v-for="(club, index) in myClubList" :key="club.clubId">
          <router-link :to="`/club/${club.teamCode}`" class="btn btn-link p-0 m-0">
            {{ club.clubName }}
          </router-link>
          <span v-if="index < myClubList.length - 1"> · </span>
        </template>
      </p>
      <div class="button-group">
        <button v-if="canGrantManager" class="btn btn-success"@click="grantManager">매니저 권한 부여</button>
        <button v-if="canRevokeManager" class="btn btn-danger"@click="revokeManager">매니저 권한 해제</button>
      </div>
      
      <router-link v-if="member" :to="friendLink" class="friend-count router-link">{{ friends.length }}명의 친구</router-link>

      <router-link v-if="member && isMyProfile" :to="{ name: 'Member_Profile_Update' }" class="btn btn-primary">프로필 설정</router-link>

      <div class="comment-box">
        <h3>소개글</h3>
        <p class="user-comment">{{ member?.userComment || '소개글이 없습니다...' }}</p>
      </div>
      <div class="info-box">
        <p></p>
      </div>
      
      <div class="info-box">
        <p class="label">매너</p>
        <p class="value">{{ profileInfo?.manner == null? '아직 평가를 받지 못했어요' : profileInfo.manner.toFixed(1) + '점'}}</p>
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
  gap: 30px;
  padding: 30px;
  font-family: 'Pretendard', sans-serif;
}

.left-panel {
  width: 300px;
  border-right: 1px solid #eee;
  padding-right: 30px;
}

.right-panel {
  flex: 1;
}

.user-name {
  font-size: 24px;
  font-weight: bold;
}

.user-info {
  margin-top: 5px;
  color: #666;
}

.friend-count {
  margin: 10px 0;
  font-weight: bold;
}

.profile-settings {
  margin: 20px 0;
  padding: 8px 12px;
  background: #f0f0f0;
  border-radius: 8px;
  display: inline-block;
  cursor: pointer;
}

.info-box {
  margin-top: 20px;
}

.label {
  font-size: 14px;
  color: #999;
}

.value {
  font-size: 16px;
  font-weight: bold;
}

.match-info {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
}

.match-box .title {
  font-size: 14px;
  color: #666;
}

.match-box .count {
  font-size: 20px;
  font-weight: bold;
}

.card-info p {
  margin: 5px 0;
  font-size: 14px;
}

.card-count {
  float: right;
  font-weight: bold;
}

.praise-section {
  margin-top: 30px;
}

.praise-section h3 {
  font-size: 16px;
  margin-bottom: 10px;
}

.no-praise {
  color: #999;
}

.comment-box h3 {
  font-size: 20px;
}

.button-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 10px 0 20px;
}

.button-group button,
.button-group a {
  min-width: 120px;
  text-align: center;
}
</style>
