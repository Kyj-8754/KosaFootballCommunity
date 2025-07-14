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
import { ref, onMounted, inject, computed } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const token = inject('token')
const loginUserNo = inject('userNo')
const authCode = inject('authCode')
const route = useRoute()

const member = ref(null)
const style = ref(null)
const stat = ref(null)
const friends = ref([])
const myClubList = ref([])
const profileInfo = ref(null)

const isMyProfile = computed(() => {
  return member.value?.userNo === loginUserNo.value
})

// 등급 조회
const getLevelLabel = (score) => {
  if (score == null) return '아직 평가를 받지 못했어요'
  if (score >= 9) return '🔥 프로'
  if (score >= 7) return '🏅 세미 프로'
  if (score >= 5) return '🟦 아마추어'
  if (score >= 3) return '🟢 비기너'
  return '🔰 루키'
}

// 매니저 권한 부여 버튼 표시 조건
const canGrantManager = computed(() => {
  return (
    authCode.value === 'ROLE_A1' &&
    member.value?.authCode === 'A3' &&
    loginUserNo.value !== member.value?.userNo
  )
})

// 매니저 권한 해제 버튼 표시 조건
const canRevokeManager = computed(() => {
  return (
    authCode.value === 'ROLE_A1' &&
    member.value?.authCode === 'A2' &&
    loginUserNo.value !== member.value?.userNo
  )
})

const friendLink = computed(() => {
  if (!member.value || !loginUserNo?.value) return {}
  const isMe = member.value.userNo === loginUserNo.value
  if (isMe) {
    return { name: 'Member_Friend' }
  } else {
    return {
      name: 'Member_Other_Friend',
      query: { userNo: member.value.userNo }
    }
  }
})

// 회원 정보 조회 함수
const fetchMemberDetail = async () => {
  const userNo = route.query.userNo
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
    myClubList.value = res.data.myClubList || []
    style.value = res.data.userStyle
    stat.value = res.data.userStat
    profileInfo.value = res.data.profileInfo
  } catch (err) {
    console.error('회원 정보 조회 실패:', err)
  }
}

// 친구 목록 조회 함수
const loadFriendList = async () => {
  const userNo = route.query.userNo
  if (!userNo) return
  try {
    const res = await axios.get('/login_api/mypage/friends', {
      params: { userNo },
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    })
    if (res.data?.res_code === '200') {
      friends.value = res.data.data
    }
  } catch (err) {
    console.error('친구 목록 불러오기 실패', err)
  }
}

// 관리자 권한 부여
const grantManager = async () => {
  const userNo = route.query.userNo
  if (!confirm('관리자 권한을 부여하시겠습니까?')) return

  try {
    const res = await axios.post('/login_api/admin/grantManager', { userNo }, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    })
    alert(res.data.res_msg)
    if (res.data.res_code === '200') {
      await fetchMemberDetail()
    }
  } catch (err) {
    alert('권한 부여 중 오류 발생')
    console.error(err)
  }
}

// 관리자 권한 해제
const revokeManager = async () => {
  const userNo = route.query.userNo
  if (!confirm('관리자 권한을 해제하시겠습니까?')) return

  try {
    const res = await axios.post('/login_api/admin/revokeManager', { userNo }, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    })
    alert(res.data.res_msg)
    if (res.data.res_code === '200') {
      await fetchMemberDetail()
    }
  } catch (err) {
    alert('권한 해제 중 오류 발생')
    console.error(err)
  }
}

onMounted(async () => {
  await fetchMemberDetail()
  console.log('👤 로그인 유저:', loginUserNo.value)
  console.log('👤 프로필 유저:', member.value?.userNo)
  console.log('👥 같음?', loginUserNo.value === member.value?.userNo)
  console.log('🔐 authCode:', authCode?.value)
  console.log('👤 member:', member.value?.authCode)
  console.log('👤 team:', myClubList.value)
  await loadFriendList()
})
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
