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
import { inject } from 'vue'
import { useFriendTabs } from '@/utils/script/user'

const userNo = inject('userNo')
const token = inject('token')

const {
  activeTab,
  tabList,
  friends,
  pending,
  searchKeyword,
  searchResults,
  getCount,
  loadFriendList,
  loadPendingRequests,
  searchFriends,
  requestFriend,
  acceptFriendRequest,
  rejectFriendRequest
} = useFriendTabs(userNo, token)

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
