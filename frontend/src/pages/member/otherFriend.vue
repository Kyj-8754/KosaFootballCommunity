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
.request-btn.disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
