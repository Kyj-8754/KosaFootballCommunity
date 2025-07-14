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
  max-width: 1000px;
  margin: 50px auto;
  font-family: 'Noto Sans KR', sans-serif;
  color: #212529;
  padding: 0 1rem;
}

/* 탭 영역 */
.tabs {
  display: flex;
  gap: 20px;
  border-bottom: 1px solid #dee2e6;
  margin-bottom: 2rem;
}

.tab {
  cursor: pointer;
  padding: 14px 22px;
  border-radius: 6px 6px 0 0;
  font-weight: 500;
  color: #495057;
  transition: all 0.2s ease-in-out;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 1rem;
}

.tab:hover {
  background-color: #f8f9fa;
}

.tab--focused {
  background-color: white;
  border: 1px solid #dee2e6;
  border-bottom: 2px solid white;
  color: #0d6efd;
  font-weight: 700;
}

.tab-title {
  margin: 0;
  font-size: 1rem;
  line-height: 1.2;
}

.tab-count {
  font-size: 0.95rem;
  font-weight: 600;
  color: #0d6efd;
  line-height: 1.2;
  transform: translateY(1px); /* 숫자 수직 정렬 */
}

/* 탭 콘텐츠 영역 */
.tab-content {
  border: 1px solid #dee2e6;
  border-radius: 0 0 8px 8px;
  padding: 2rem;
  background-color: #fff;
  min-height: 200px;
}

/* 친구 카드, 대기 목록, 검색 결과 공통 */
.friend-info-link,
.pending-item,
.result-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  background-color: #f8f9fa;
  margin-bottom: 12px;
  text-decoration: none;
  color: inherit;
  transition: background-color 0.2s;
}

.friend-info-link:hover,
.pending-item:hover,
.result-info:hover {
  background-color: #e9f2ff;
}

/* 대기 항목만 별도 강조 */
.pending-item {
  background-color: #fff3cd;
  border-color: #ffeeba;
  color: #856404;
}

/* 대기 항목 내부 구조 */
.pending-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.user-summary {
  flex-grow: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 버튼 영역 */
.action-buttons {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

/* 검색 영역 */
.search-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-row {
  display: flex;
  gap: 12px;
}

.search-input {
  flex: 1;
  padding: 12px;
  font-size: 1rem;
  border: 1px solid #ced4da;
  border-radius: 6px;
}

.search-btn {
  padding: 12px 20px;
  background-color: #0d6efd;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  font-size: 0.95rem;
}

.search-btn:hover {
  background-color: #0b5ed7;
}

/* 친구 신청 버튼 */
.request-btn {
  padding: 8px 14px;
  font-size: 0.85rem;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
}

.request-btn:hover {
  background-color: #218838;
}

.request-btn.disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 데이터 없음 메시지 */
.no-data {
  text-align: center;
  color: #868e96;
  font-size: 0.95rem;
  margin-top: 20px;
}

/* 반응형 대응 */
@media (max-width: 768px) {
  .friend-container {
    max-width: 95%;
    padding: 0 1rem;
  }

  .tab-content {
    padding: 1.2rem;
  }

  .search-row {
    flex-direction: column;
  }

  .result-info,
  .friend-info-link,
  .pending-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-buttons {
    margin-top: 0.5rem;
  }
}
</style>
