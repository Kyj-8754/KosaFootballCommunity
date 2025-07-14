<template>
  <div>
    <h3>{{member?.userName}}의 친구 {{ friends.length }}</h3>
    <div v-if="friends.length > 0">
    <div
      v-for="friend in friends"
      :key="friend.userNo"
      class="friend-info-wrapper"
    >
      <router-link
        :to="{ name: 'Member_Profile', query: { userNo: friend.userNo } }"
        class="friend-info-link"
      >
        <div class="friend-info">
          <strong>{{ friend.userName }}</strong>
          ({{ friend.userAddr }})
        </div>
      </router-link>

      <!-- 로그인 유저 본인은 버튼 표시 안 함 -->
      <template v-if="loginUserNo !== friend.userNo">
        <!-- relationStatus가 WAIT이면 비활성화 -->
        <button
          v-if="friend.relationStatus === 'WAIT'"
          disabled
          class="request-btn disabled"
        >
          친구 요청됨
        </button>

        <!-- relationStatus가 null이면 요청 가능 -->
        <button
          v-else-if="friend.relationStatus == null"
          @click.stop.prevent="requestFriend(friend.userNo)"
          class="request-btn"
        >
          친구 신청
        </button>
      </template>
    </div>
  </div>
  <div v-else>
    😢 친구가 없습니다.
  </div>

  </div>
</template>

<script setup>
import { useOtherFriendList } from '@/utils/script/user'
const {
  loginUserNo,
  friends,
  member,
  requestFriend
} = useOtherFriendList()
</script>

<style scoped>
h3 {
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  margin-top: 2rem; /* ⬅ 상단 간격 추가 */
  color: #333;
}

/* 전체 wrapper 간격 추가하고 중앙 정렬 느낌 */
div {
  padding: 0 1rem;
}

/* 친구 정보 wrapper */
.friend-info-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #eee;
  padding: 12px 16px;
  margin-bottom: 12px;
  border-radius: 8px;
  background-color: #fafafa;
}

/* 친구 링크 영역 */
.friend-info-link {
  text-decoration: none;
  color: inherit;
  flex-grow: 1;
}

.friend-info {
  font-size: 1rem;
  color: #212529;
}

.friend-info strong {
  font-weight: 600;
}

/* 버튼 */
.request-btn {
  padding: 6px 12px;
  font-size: 0.9rem;
  border: none;
  border-radius: 6px;
  background-color: #0d6efd;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s ease-in-out;
}

.request-btn:hover {
  background-color: #0b5ed7;
}

.request-btn.disabled {
  background-color: #ccc;
  color: #666;
  cursor: not-allowed;
}
</style>
