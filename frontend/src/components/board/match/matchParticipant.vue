<template>
  <div class="participant-list">
    <!-- ✅ 탭 -->
    <div class="tabs">
      <button
        v-for="tab in tabOptions"
        :key="tab"
        :class="['tab-btn', { active: activeTab === tab }]"
        @click="activeTab = tab"
      >
        {{ tabLabelMap[tab] }}
      </button>
    </div>

    <div v-if="loading" class="loading">불러오는 중...</div>

    <ul v-else-if="filteredParticipants.length > 0" class="participant-ul">
      <li v-for="p in filteredParticipants" :key="p.user_no" class="participant-item">
        <div class="user-info">
          <strong>{{ p.user_name }}</strong>
          <template v-if="props.matchCode === 'league' && p.club_name">
            (<span class="club-name">{{ p.club_name }}</span>)
          </template>
          <span class="date">· 신청일: {{ formatDate(p.created_at) }}</span>
        </div>

        <div
          v-if="userNo === props.matchUserNo || userNo === props.matchManagerNo"
          class="status-buttons"
        >
          <button
            v-if="p.user_status !== 'approve'"
            class="btn btn-approve"
            @click="changeStatus(p, 'approve')"
          >
            승인
          </button>
          <button
            v-if="p.user_status !== 'reject'"
            class="btn btn-reject"
            @click="changeStatus(p, 'reject')"
          >
            거절
          </button>
        </div>
      </li>
    </ul>

    <div v-else class="empty-message">해당 상태의 참가자가 없습니다.</div>
  </div>
</template>

<script setup>
import { ref, watch, computed, inject } from 'vue'
import axios from 'axios'
import { format } from 'date-fns'

const props = defineProps({
  matchId: Number,
  matchCode: String,
  matchUserNo: Number,
  matchManagerNo: Number
})

const userNo = inject('userNo')

const participants = ref([])
const loading = ref(false)
const tabOptions = ['apply', 'approve', 'reject']
const activeTab = ref('apply')

const tabLabelMap = {
  apply: '신청자',
  approve: '승인됨',
  reject: '거절됨'
}

const filteredParticipants = computed(() =>
  participants.value.filter(p => p.user_status === activeTab.value)
)

const fetchParticipants = async () => {
  if (!props.matchId) return
  loading.value = true
  try {
    const endpoint = props.matchCode === 'league'
      ? '/board_api/match/participantswithclub'
      : '/board_api/match/participants'

    const res = await axios.get(endpoint, {
      params: { matchId: props.matchId }
    })

    participants.value = res.data
  } catch (err) {
    console.error('참가자 목록 조회 실패:', err)
    participants.value = []
  } finally {
    loading.value = false
  }
}

const changeStatus = async (participant, newStatus) => {
  try {
    await axios.post('/board_api/match/status', {
      match_id: props.matchId,
      user_no: participant.user_no,
      user_status: newStatus
    })
    participant.user_status = newStatus
  } catch (err) {
    console.error('❌ 상태 변경 실패:', err)
    alert('상태 변경에 실패했습니다.')
  }
}

const formatDate = (datetimeStr) => {
  if (!datetimeStr) return '-'
  return format(new Date(datetimeStr), 'yyyy-MM-dd HH:mm')
}

watch(() => props.matchId, fetchParticipants, { immediate: true })
</script>

<style scoped>
.participant-list {
  padding: 16px;
  background: #fdfdfd;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 14px;
}

.tab-btn {
  padding: 6px 14px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background-color: #f0f0f0;
  cursor: pointer;
  transition: 0.2s;
}
.tab-btn:hover {
  background-color: #e0e0e0;
}
.tab-btn.active {
  background-color: #007bff;
  color: #fff;
  border-color: #007bff;
}

.loading,
.empty-message {
  padding: 12px;
  text-align: center;
  color: #666;
}

.participant-ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.participant-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border: 1px solid #eee;
  padding: 10px 12px;
  margin-bottom: 8px;
  border-radius: 6px;
  transition: box-shadow 0.2s;
}
.participant-item:hover {
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.05);
}

.user-info {
  font-size: 14px;
}
.club-name {
  color: #555;
}
.date {
  margin-left: 8px;
  color: #999;
}

.status-buttons {
  display: flex;
  gap: 6px;
}

.btn {
  padding: 4px 10px;
  font-size: 13px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  transition: 0.2s;
}
.btn-approve {
  background-color: #4caf50;
  color: #fff;
}
.btn-approve:hover {
  background-color: #43a047;
}
.btn-reject {
  background-color: #f44336;
  color: #fff;
}
.btn-reject:hover {
  background-color: #e53935;
}
</style>