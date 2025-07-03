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
import { ref, inject, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const loginUserNo = inject('userNo')
const token = inject('token')
const friends = ref([])
const member = ref({})


// 친구 목록 + 로그인 유저와의 관계 상태까지 함께 조회
const loadFriendList = async () => {
  const targetUserNo = route.query.userNo
  if (!targetUserNo || !loginUserNo?.value) return

  try {
    const res = await axios.get('/login_api/mypage/otherFriends', {
      params: {
        targetUserNo,        // 조회 대상 사용자의 친구 목록
        loginUserNo: loginUserNo.value  // 로그인한 사용자 기준의 관계 판단
      },
      headers: {
      Authorization: `Bearer ${token.value}`
      }
    })

    if (res.data?.res_code === '200') {
      friends.value = res.data.data // [{ userNo, userName, userAddr, relationStatus }, ...]
    }
  } catch (err) {
    console.error('친구 목록 조회 실패:', err)
  }
}

const requestFriend = async (targetUserNo) => {
  try {
    await axios.post('/login_api/mypage/request', {
      requesterNo: loginUserNo.value,
      requestedNo: targetUserNo
    },{
      headers: {
      Authorization: `Bearer ${token.value}`
      }
    })
    alert('친구 요청을 보냈습니다.')
    await loadFriendList() // 다시 불러와서 relationStatus 반영
  } catch (error) {
    console.error('친구 요청 실패:', error)
    alert('친구 요청에 실패했습니다.')
  }
}

onMounted(loadFriendList)

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
  } catch (err) {
    console.error('회원 정보 조회 실패:', err)
  }
  await loadFriendList()
})
</script>

<style scoped>
.request-btn.disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
