<template>
  <div class="container my-5">
    <div class="controls d-flex justify-content-between align-items-center mb-3 flex-wrap">
    <div class="d-flex align-items-center gap-2">
      <label class="fw-bold">건수:</label>
      <select v-model="pageSize" class="form-select w-auto" @change="onPageSizeChange">
      <option v-for="size in [10, 20, 50, 100]" :key="size" :value="size">{{ size }}</option>
      </select>
    </div>
    <div class="input-group w-auto">
      <select v-model="searchType" class="form-select w-auto">
        <option value="userName">이름</option>
        <option value="userCode">회원코드</option>
      </select>
      <input v-model="searchValue" type="text" class="form-control" placeholder="검색어 입력" />
      <button class="btn btn-primary" @click="onSearch">검색</button>
    </div>
  </div>

  <table class="table memberList-table table-bordered shadow-sm">
  <caption class="text-center fs-5 fw-bold">회원 목록</caption>
    <thead class="table-light">
      <tr>
        <th>번호</th>
        <th>이름</th>
        <th>주소</th>
        <th>등록일</th>
      </tr>
    </thead>
    <tbody>
      <tr v-if="users.length === 0">
        <td colspan="5" class="text-center">존재하는 회원 정보가 없습니다</td>
      </tr>
      <tr v-for="(user, index) in users" :key="user.userId" class="clickable-row" @click="goDetail(user.userNo)">
        <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
        <td>{{ user.userName }}</td>
        <td>{{ user.userAddr }}</td>
        <td>{{ formatDate(user.userRegDate) }}</td>
      </tr>
    </tbody>
  </table>

    <div class="text-center mt-4">
    <button v-if="paging.prev"
    class="btn btn-outline-secondary btn-sm"
    @click="goToPage(Number(paging.pageBarStart) - 1)">
    &lt;
    </button>

    <button v-for="pageNo in pageRange"
    :key="pageNo"
    class="btn btn-sm mx-1"
    :class="pageNo === paging.nowPage ? 'btn-primary' : 'btn-outline-primary'"
    @click="goToPage(pageNo)">
    {{ pageNo }}
    </button>

    <button v-if="paging.next"
    class="btn btn-outline-secondary btn-sm"
    @click="goToPage(Number(paging.pageBarEnd) + 1)">
    &gt;
    </button>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted , inject } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const token = inject('token')

const users = ref([])
const searchType = ref('userName')
const searchValue = ref('')
const pageSize = ref(10)
const currentPage = ref(1)
const totalPages = ref(1)
const paging = ref({})

const startPage = computed(() => Math.max(1, currentPage.value - 2))
const endPage = computed(() => Math.min(totalPages.value, currentPage.value + 2))

const pageRange = computed(() => {
  const range = []
  for (let i = paging.value.pageBarStart; i <= paging.value.pageBarEnd; i++) {
    range.push(i)
  }
  return range
})

const fetchUserList = async () => {
  try {
    const res = await axios.get('/login_api/admin/userList', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        searchValue: searchValue.value,
        searchType: searchType.value
      },
      headers: {
      Authorization: `Bearer ${token.value}`
      }
    })
    users.value = Array.isArray(res.data.userList) ? res.data.userList : []
    totalPages.value = res.data.paging?.totalPage || 1
    paging.value = res.data.paging || {}
  } catch (e) {
    console.error('회원 목록 조회 실패:', e)
    users.value = []
    paging.value = {}
  }
}

const goToPage = (page) => {
  currentPage.value = page
  fetchUserList()
}

const onPageSizeChange = () => {
  currentPage.value = 1
  fetchUserList()
}

const onSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

const goDetail = (userNo) => {
  router.push({
    name: 'Member_Profile',
    query: { userNo }
  })
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`
}

onMounted(() => {
  fetchUserList()
})
</script>
<style scoped>
body {
  background-color: #f8f9fa;
  font-family: 'Pretendard', sans-serif;
}

.memberList-table {
  width: 100%;
  max-width: 800px;
  margin: 40px auto;
  border-collapse: collapse;
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  font-size: 0.95rem;
}

.memberList-table th,
.memberList-table td {
  padding: 14px 12px;
  border: 1px solid #dee2e6;
  text-align: center;
  vertical-align: middle;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.memberList-table caption {
  caption-side: top;
  font-size: 1.4rem;
  font-weight: 600;
  padding: 18px 0;
  color: #212529;
}

.clickable-row:hover {
  background-color: #f1f1f1;
  cursor: pointer;
}

.controls {
  width: 100%;
  max-width: 800px;
  margin: 30px auto 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.input-group {
  flex-wrap: nowrap;
  min-width: 360px;
}

.input-group input,
.input-group select,
.input-group .btn {
  font-size: 0.9rem;
  height: 38px;
  border-radius: 4px;
  box-sizing: border-box;
}

.input-group input,
.input-group select {
  padding: 6px 10px;
  border: 1px solid #ccc;
}

.input-group input {
  width: 180px;
  flex: 1;
  min-width: 200px;
}

.input-group select {
  width: 120px;
}

.input-group .btn {
  white-space: nowrap;
  padding: 6px 18px;
  font-weight: 600;
}

.input-group .btn {
  padding: 6px 14px;
}

.pagination-area,
.text-center.mt-4 {
  width: 100%;
  max-width: 800px;
  margin: 20px auto;
  text-align: center;
}

.btn-sm {
  font-size: 0.85rem;
  padding: 6px 12px;
  border-radius: 4px;
}

.btn-outline-secondary,
.btn-outline-primary {
  font-weight: 500;
  transition: background-color 0.2s, color 0.2s;
}

.btn-outline-secondary:hover,
.btn-outline-primary:hover {
  opacity: 0.9;
  background-color: #e9ecef;
}
</style>
