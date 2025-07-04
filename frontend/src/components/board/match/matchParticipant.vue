<template>
  <div class="participant-list">
    <!-- ✅ 탭 -->
    <div class="tabs">
      <button
        v-for="tab in tabOptions"
        :key="tab"
        :class="{ active: activeTab === tab }"
        @click="activeTab = tab"
      >
        {{ tabLabelMap[tab] }}
      </button>
    </div>

    <div v-if="loading">불러오는 중...</div>
    <div v-else>
      <ul v-if="filteredParticipants.length > 0">
        <li v-for="p in filteredParticipants" :key="p.user_no" class="participant-item">
          <strong>{{ p.user_name }}</strong>
          <template v-if="props.matchCode === 'league' && p.club_name">
            (<span class="club-name">{{ p.club_name }}</span>)
          </template>
          - 신청일: {{ formatDate(p.created_at) }}
          <select
            v-if="userNo === props.matchUserNo || userNo === props.matchManagerNo"
            v-model="p.user_status"
            @change="updateStatus(p)"
          >
            <option value="apply">신청자</option>
            <option value="approve">승인됨</option>
            <option value="reject">거절됨</option>
          </select>
        </li>
      </ul>
      <div v-else>해당 상태의 참가자가 없습니다.</div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, inject } from 'vue'
import axios from 'axios'
import { format } from 'date-fns'

const props = defineProps({
  matchId: { type: Number, required: true },
  matchCode: { type: String, required: true },
  matchUserNo: { type: Number, required: true },
  matchManagerNo: { type: Number, required: true }
})

const participants = ref([])
const loading = ref(false)

const tabOptions = ['apply', 'approve', 'reject']
const activeTab = ref('apply')

const userNo = inject('userNo')

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

const updateStatus = async (participant) => {
  try {
    await axios.post('/board_api/match/status', {
      match_id: props.matchId,
      user_no: participant.user_no,
      user_status: participant.user_status
    })
    console.log('✅ 상태 변경 완료:', participant)
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
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: #f9f9f9;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tabs button {
  padding: 6px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: #eee;
  cursor: pointer;
}

.tabs button.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.participant-item {
  margin-bottom: 8px;
}

select {
  margin-left: 8px;
  padding: 4px;
}
</style>