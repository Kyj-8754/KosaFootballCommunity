<template>
  <div class="match-description-card">
    <!-- 탭 버튼 영역 -->
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

    <!-- 탭별 내용 -->
    <div class="tab-content">
      <p v-if="activeTab === 'notice'">
        {{ notice || '공지사항이 없습니다.' }}
      </p>
      <p v-if="activeTab === 'detail'">
        {{ detail || '상세 안내 정보가 없습니다.' }}
      </p>
      <p v-if="activeTab === 'description'">
        {{ description || '매치 설명이 없습니다.' }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps } from 'vue'

const props = defineProps({
  notice: {
    type: String,
    default: ''
  },
  detail: {
    type: String,
    default: ''
  },
  description: {
    type: String,
    default: ''
  }
})

const activeTab = ref('notice')

const tabs = [
  { key: 'notice', label: '공지사항' },
  { key: 'detail', label: '안내사항' },
  { key: 'description', label: '매치 설명' }
]
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

.tab-content p {
  white-space: pre-wrap;
  line-height: 1.5;
  font-size: 14px;
  color: #333;
  margin: 0;
}
</style>
