<template>
  <div class="match-description-card">
    <!-- 탭 버튼 -->
    <div class="tab-buttons">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        @click="activeTab = tab.key"
        :class="{ active: activeTab === tab.key }"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 탭별 내용 출력 -->
    <div class="tab-content">
      <div v-if="activeTab === 'description'" v-html="descriptionHtml" />
      <div v-else-if="activeTab === 'participants'">
        <MatchParticipant
          :matchId="matchId"
          :matchCode="matchCode"
          :matchUserNo="matchUserNo"
          :matchManagerNo="matchManagerNo"
          @statusChanged="$emit('refreshRequest')"
        />
      </div>
      <div v-else-if="activeTab === 'results'">
        <MatchResult :matchId="matchId" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps } from 'vue'
import MatchParticipant from '@/components/board/match/matchParticipant.vue'
import MatchResult from './matchResult.vue'

const props = defineProps({
  description: { type: String, default: '' },
  matchId: { type: Number, required: true },
  matchCode: { type: String, required: true },
  matchUserNo: { type: Number, required: true },
  matchManagerNo: { type: Number, required: true },
  matchStatus: { type: String, required: true } // ✅ 추가
})

const activeTab = ref('description')

const tabs = computed(() => {
  const base = [
    { key: 'description', label: '매치 설명' },
    { key: 'participants', label: '참가자 목록' }
  ]
  if (props.matchStatus === 'completed' && props.matchCode === 'league') {
    base.push({ key: 'results', label: '경기 결과' })
  }
  return base
})

// HTML 렌더링용 computed
const descriptionHtml = computed(() => props.description || '<p>매치 설명이 없습니다.</p>')
</script>

<style scoped>
.match-description-card {
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.tab-buttons {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tab-buttons button {
  padding: 6px 12px;
  background-color: #eee;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

.tab-buttons button.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}

.tab-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.tab-content img {
  max-width: 100%;
  height: auto;
}
</style>
