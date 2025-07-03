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
      <router-link v-if="member" :to="friendLink" class="friend-count router-link">{{ friends.length }}명의 친구</router-link>

      <router-link v-if="member" :to="{name: 'Member_Profile_Update', query: { userNo: member.userNo }}" class="btn btn-primary">프로필 설정</router-link>

      <div class="comment-box">
        <h3>소개글</h3>
        <p class="user-comment">{{ member?.userComment || '소개글이 없습니다...' }}</p>
      </div>
      <div class="info-box">
        <p></p>
      </div>
      <div class="info-box">
        <p class="label">매너</p>
        <p class="value">😊 좋아요</p>
      </div>

      <div class="info-box">
        <p class="label">레벨</p>
        <p class="value">🟦 루키</p>
      </div>
    </div>

    <!-- 오른쪽 영역 -->
    <div class="right-panel">
      <div class="match-info">
        <div class="match-box">
          <p class="title">⚽ 경기</p>
          <p class="count">0</p>
        </div>
        <div class="match-box">
          <p class="title">👑 POM</p>
          <p class="count">0</p>
        </div>
      </div>

      <div class="card-info">
        <p>스마일 카드 <span class="card-count">0</span></p>
        <p>옐로 카드 <span class="card-count">0</span></p>
        <p>레드 카드 <span class="card-count">0</span></p>
      </div>

      <div class="praise-section">
        <h3>👏 칭찬해요</h3>
        <p class="no-praise">칭찬 내역이 없어요</p>
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
const route = useRoute()
const member = ref(null)
const style = ref(null)
const stat = ref(null)
const friends = ref([])

const friendLink = computed(() => {
  if (!member.value || !loginUserNo?.value) return {}

  const isMe = member.value.userNo === loginUserNo.value

  return {
    name: isMe ? 'Member_Friend' : 'Member_Other_Friend',
    query: {
      userNo: isMe ? loginUserNo.value : member.value.userNo
    }
  }
})

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
    style.value = res.data.userStyle
    stat.value = res.data.userStat
  } catch (err) {
    console.error('회원 정보 조회 실패:', err)
  }
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
</style>
