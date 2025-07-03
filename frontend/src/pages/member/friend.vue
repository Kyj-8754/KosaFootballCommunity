<template>
  <div class="friend-container">
    <!-- 탭 영역 -->
    <div class="tabs">
      <div
        v-for="tab in tabList"
        :key="tab.value"
        :class="['tab', activeTab === tab.value ? 'tab--focused' : '']"
        @click="activeTab = tab.value"
      >
        <p class="tab-title">{{ tab.label }}</p>
        <span class="tab-count">{{ getCount(tab.value) }}</span>
      </div>
    </div>

    <!-- 탭 컨텐츠 영역 -->
    <div class="tab-content">
      <!-- 친구 목록 -->
      <template v-if="activeTab === 'friends'">
        <div v-if="friends.length > 0">
          <router-link
            v-for="friend in friends"
            :key="friend.userNo"
            :to="{ name: 'Member_Profile', query: { userNo: friend.userNo } }"
            class="friend-info-link"
          >
            <div class="friend-info">
              <strong>{{ friend.userName }}</strong>
              ({{ friend.userAddr }})
            </div>
          </router-link>
        </div>
        <div v-else class="no-data">😢 아직 친구가 없어요</div>
      </template>

      <!-- 신청대기 목록 -->
      <template v-else-if="activeTab === 'pending'">
        <div v-if="pending.length > 0">
          <router-link
            v-for="user in pending"
            :key="user.userNo"
            :to="{ name: 'Member_Profile', query: { userNo: user.userNo } }"
            class="pending-item"
          >
            <div class="pending-info">
              <span class="user-summary">
                <strong>{{ user.userName }}</strong>
                ({{ user.userAddr }})
              </span>
              <div class="action-buttons">
                <button
                  @click.stop.prevent="acceptFriendRequest(user.userNo)"
                  class="btn btn-success btn-sm"
                >
                  수락
                </button>
                <button
                  @click.stop.prevent="rejectFriendRequest(user.userNo)"
                  class="btn btn-danger btn-sm"
                >
                  거절
                </button>
              </div>
            </div>
          </router-link>
        </div>
        <div v-else class="no-data">⏳ 신청 대기 중인 친구가 없어요</div>
      </template>


      <!-- 친구 검색 -->
      <template v-else-if="activeTab === 'searchFriend'">
        <div class="search-wrapper">
          <div class="search-row">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="친구 코드를 입력하세요"
              class="search-input"
            />
            <button @click="searchFriends" class="search-btn">검색</button>
          </div>

          <div v-if="searchResults.length > 0">
            <div
              class="result-info"
              v-for="(friend, index) in searchResults"
              :key="index"
            >
              <strong>{{ friend.userName }}</strong>
              ({{ friend.userAddr }})

              <template v-if="userNo.value !== friend.userNo">
                <button
                  v-if="friend.relationStatus === 'WAIT'"
                  disabled
                  class="request-btn disabled"
                >
                  친구 요청됨
                </button>
                <button
                  v-else-if="friend.relationStatus == null"
                  @click="requestFriend(friend.userNo)"
                  class="request-btn"
                >
                  친구 신청
                </button>
              </template>
            </div>
          </div>

          <div v-else class="no-data">🔍 검색 결과가 없습니다</div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, inject, watch, onMounted } from 'vue'
import axios from 'axios'

const token = inject('token')
const userNo = inject('userNo')
const activeTab = ref('friends')

const tabList = [
  { label: '친구', value: 'friends' },
  { label: '신청대기', value: 'pending' },
  { label: '친구검색', value: 'searchFriend' }
]

const friends = ref([])
const pending = ref([])
const searchKeyword = ref('')
const searchResults = ref([])

const getCount = (type) => {
  if (type === 'friends') return friends.value.length
  if (type === 'pending') return pending.value.length
  if (type === 'searchFriend') return searchResults.value.length
  return 0
}

const loadFriendList = async () => {
  if (!userNo?.value) return
  try {
    const res = await axios.get('/login_api/mypage/friends', {
      params: { userNo: userNo.value },
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

const loadPendingRequests = async () => {
  if (!userNo?.value) return
  try {
    const res = await axios.get('/login_api/mypage/pending', {
      params: { userNo: userNo.value },
      headers: {
      Authorization: `Bearer ${token.value}`
      }
    })
    if (res.data?.res_code === '200') {
      pending.value = res.data.data
    }
  } catch (err) {
    console.error('신청 대기 목록 불러오기 실패', err)
  }
}

const searchFriends = async () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    searchResults.value = []
    return
  }

  try {
    const res = await axios.get('/login_api/mypage/search', {
      params: {
        keyword,
        loginUserNo: userNo.value
      },
      headers: {
      Authorization: `Bearer ${token.value}`
      }
    })

    if (res.data?.res_code === '200' && res.data.data) {
      searchResults.value = [res.data.data]
    } else {
      searchResults.value = []
    }
  } catch (err) {
    console.error('🔍 친구 검색 오류', err)
    searchResults.value = []
    alert('검색 중 문제가 발생했습니다.')
  }
}

const requestFriend = async (targetUserNo) => {
  if (!userNo?.value) {
    alert('로그인이 필요합니다.')
    return
  }

  try {
    await axios.post('/login_api/mypage/request', {
      requesterNo: userNo.value,
      requestedNo: targetUserNo
    },{
      headers: {
      Authorization: `Bearer ${token.value}`
      }
    })
    alert('친구 요청을 보냈습니다.')
    await searchFriends() // 상태 갱신
  } catch (err) {
    console.error('친구 요청 실패:', err)
    alert('친구 요청에 실패했습니다.')
  }
}

const acceptFriendRequest = async (requesterNo) => {
  try {
    const res = await axios.post('/login_api/mypage/accept', {
      requesterNo,
      requestedNo: userNo.value
    },{
      headers: {
      Authorization: `Bearer ${token.value}`
      }
    })
    if (res.data?.res_code === '200') {
      alert('친구 요청을 수락했습니다.')
      pending.value = pending.value.filter(user => user.userNo !== requesterNo)
      await loadFriendList()
    } else {
      alert('친구 수락에 실패했습니다.')
    }
  } catch (err) {
    console.error('친구 수락 오류', err)
    alert('친구 수락 중 오류가 발생했습니다.')
  }
}

const rejectFriendRequest = async (requesterNo) => {
  try {
    const res = await axios.post('/login_api/mypage/reject', {
      requesterNo,
      requestedNo: userNo.value
    },{
      headers: {
      Authorization: `Bearer ${token.value}`
      }
    })
    if (res.data?.res_code === '200') {
      alert('친구 요청을 거절했습니다.')
      pending.value = pending.value.filter(user => user.userNo !== requesterNo)
    } else {
      alert('친구 거절에 실패했습니다.')
    }
  } catch (err) {
    console.error('친구 거절 오류', err)
    alert('친구 거절 중 오류가 발생했습니다.')
  }
}

onMounted(() => {
  if (userNo?.value) {
    loadFriendList()
    loadPendingRequests()
  }
})

watch(userNo, (val) => {
  if (val) {
    loadFriendList()
    loadPendingRequests()
  }
})
</script>

<style scoped>
.friend-container {
  width: 600px;
  margin: 30px auto;
  font-family: sans-serif;
}

.tabs {
  display: flex;
  gap: 20px;
  border-bottom: 1px solid #ccc;
  margin-bottom: 20px;
}

.tab {
  cursor: pointer;
  padding: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab--focused {
  font-weight: bold;
  color: #007bff;
  border-bottom: 2px solid #007bff;
}

.tab-title {
  margin: 0;
}

.tab-count {
  color: #007bff;
  font-weight: bold;
}

.tab-content {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  min-height: 150px;
}

.no-data {
  text-align: center;
  color: #999;
  font-size: 14px;
}

.search-wrapper {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.search-row {
  display: flex;
  gap: 10px;
}

.search-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.search-btn {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-btn:hover {
  background-color: #0056b3;
}

.result-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #ddd;
}

.request-btn {
  padding: 6px 12px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.request-btn:hover {
  background-color: #218838;
}

.request-btn.disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pending-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: nowrap;
  gap: 10px;
  width: 100%;
}

.pending-info span {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-grow: 1;
}

.action-buttons {
  display: flex;
  flex-shrink: 0;
  gap: 6px;
}
</style>
