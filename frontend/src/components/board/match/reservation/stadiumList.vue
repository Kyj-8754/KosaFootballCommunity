<template>
  <div class="recruit-stadium-list">
    <h3>구장 목록</h3>

    <!-- 검색 폼 -->
    <div class="search-box d-flex align-items-center gap-2 mb-3">
      <select v-model="searchType" class="form-select w-auto">
        <option v-for="type in searchTypes" :key="type.value" :value="type.value">
          {{ type.label }}
        </option>
      </select>
      <input v-model="searchValue" type="text" class="form-control w-auto" placeholder="검색어" />
      <button @click="handleSearch" class="btn btn-outline-primary btn-sm">검색</button>
      <button @click="resetSearch" class="btn btn-secondary btn-sm">초기화</button>
    </div>

    <!-- 구장 카드 목록 -->
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-3 mb-3">
      <div v-for="item in stadiums" :key="item.svcid" class="col">
        <div class="card h-100 shadow-sm" @click="select(item.svcid)" style="cursor: pointer;">
          <div class="card-body">
            <h5 class="card-title">{{ item.svcnm }}</h5>
            <p><strong>지역명:</strong> {{ item.areanm }}</p>
            <p><strong>장소명:</strong> {{ item.placenm }}</p>
            <p><strong>연락처:</strong> {{ item.telno }}</p>
            <p><strong>가격:</strong> {{ item.price }}</p>
            <p><strong>운영시간:</strong> {{ item.v_MIN }} ~ {{ item.v_MAX }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <Pagination
      :currentPage="currentPage"
      :totalPages="totalPages"
      @changePage="changePage"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import Pagination from '@/components/pagination.vue' 

const emit = defineEmits(['select'])

const searchTypes = [
  { label: '전체', value: '' },
  { label: '지역명', value: 'areanm' },
  { label: '장소명', value: 'placenm' }
]

const searchType = ref('')
const searchValue = ref('')
const stadiums = ref([])

const currentPage = ref(1)
const totalPages = ref(1)

const fetchData = async () => {
  try {
    const res = await axios.get('/stadium_api/stadium/list', {
      params: {
        pageNo: currentPage.value,
        searchType: searchType.value,
        searchValue: searchValue.value
      }
    })

    stadiums.value = res.data.pageResponse.list || []
    totalPages.value = res.data.pageResponse.totalPage || 1
  } catch (err) {
    console.error('구장 목록 불러오기 실패:', err)
  }
}

const resetSearch = () => {
  searchType.value = ''
  searchValue.value = ''
  currentPage.value = 1
  fetchData()
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const changePage = (page) => {
  currentPage.value = page
  fetchData()
}

const select = (svcid) => {
  emit('select', svcid)
}

onMounted(fetchData)
</script>

<style scoped>
.card {
  transition: transform 0.2s ease;
}
.card:hover {
  transform: scale(1.02);
}
</style>
