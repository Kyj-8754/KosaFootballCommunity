<template>
  <div class="query-bar">
    <!-- 1행: 날짜, 성별, 마감여부, 경기 상태, 게시글 상태, 지역 -->
    <div class="filter-row">
      <input type="date" v-model="query.afterDate" />

      <select v-model="query.gender_condition">
        <option value="">성별 제한</option>
        <option value="all">성별 무관</option>
        <option value="male">남성 한정</option>
        <option value="female">여성 한정</option>
      </select>

      <select v-model="query.match_closed">
        <option value="">마감 여부</option>
        <option value="active">모집중</option>
        <option value="closed">마감</option>
      </select>

      <select v-model="query.match_status">
        <option value="">경기 상태</option>
        <option value="waiting">대기중</option>
        <option value="active">진행중</option>
        <option value="completed">진행 완료</option>
      </select>

      <!-- ✅ 지역 드롭다운 추가 -->
      <select v-model="query.areanm">
        <option value="">전체 지역</option>
        <option v-for="area in areaList" :key="area" :value="area">
          {{ area }}
        </option>
      </select>
    </div>

    <!-- 2행: 검색창 + 버튼 -->
    <div class="search-row">
      <input
        v-model="query.match_title"
        type="text"
        placeholder="검색할 내용을 입력해주세요"
        class="search-input"
      />
      <button @click="onQuery">검색</button>
      <button @click="onReset" type="button">초기화</button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import axios from 'axios'

const areaList = ref([])
const emit = defineEmits(['query'])

const initialQuery = {
  match_title: '',
  afterDate: '',
  gender_condition: '',
  match_closed: '',
  match_status: '',
  areanm: '' // ✅ 지역 초기값 추가
}

const query = reactive({ ...initialQuery })

const onQuery = () => {
  emit('query', { ...query })
}

const onReset = () => {
  Object.assign(query, initialQuery)
  emit('query', { ...initialQuery })
}

onMounted(async () => {
  try {
    const res = await axios.get('/board_api/match/areas') // 백엔드 엔드포인트 호출
    areaList.value = res.data
  } catch (err) {
    console.error('지역 목록 불러오기 실패:', err)
  }
})
</script>

<style scoped>
.query-bar {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.filter-row,
.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

input,
select,
button {
  padding: 6px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.search-input {
  flex: 1;
}

button {
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

button[type="button"]:last-of-type {
  background-color: #6c757d;
}
</style>