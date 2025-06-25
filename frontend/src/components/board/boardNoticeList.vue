<template>
  <div class="notice-list">
    <table class="board-table">
        <colgroup>
          <!--<col style="width: 8%" />-->
          <col style="width: 12%" />
          <col style="width: 12%" />
          <col style="width: 28%" />
          <col style="width: 15%" />
          <col style="width: 10%" />
          <col style="width: 10%" />
        </colgroup>
        <thead>
            <tr>
            <!--<th>글 번호</th>-->
            <th>카테고리</th>
            <th>작성자</th>
            <th>제목</th>
            <th>등록일</th>
            <th>조회수</th>
            <th>추천수</th>
            </tr>
        </thead>
        <tbody v-if="visibleNotices.length > 0">
          <tr v-for="(notice, idx) in visibleNotices" :key="notice.board_id">
            <td>{{ notice.board_category }}</td>
            <td>{{ notice.user_name }}</td>
            <td @click="$emit('view', notice.board_id)" style="cursor: pointer; color: blue;">
              {{ notice.board_title }}
            </td>
            <td>
              <span>{{ notice.board_modified_at ? formatDate(notice.board_modified_at) : formatDate(notice.board_created_at) }}</span>
            </td>
            <td>{{ notice.board_viewcount }}</td>
            <td>{{ notice.board_likecount }}</td>
          </tr>

          <!-- 🔽 더보기 / 간략히 row -->
          <tr v-if="notices.length > 3">
            <td colspan="7" class="toggle-row" @click="expanded = !expanded">
              {{ expanded ? '간략히' : '더보기' }}
            </td>
          </tr>
        </tbody>

        <!-- 🔻 공지사항 없음 표시 -->
        <tbody v-else>
          <tr>
            <td colspan="7" style="padding: 20px; text-align: center; color: #777;">
              공지사항이 없습니다.
            </td>
          </tr>
        </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const notices = ref([])
const expanded = ref(false)

const emit = defineEmits(['view'])

function formatDate(dateStr) {
  return dateStr ? dateStr.replace('T', ' ') : ''
}

const fetchNotices = async () => {
  try {
    const response = await axios.get('/board_api/board/list', {
      params: {
        category: '공지사항'
      }
    })
    notices.value = response.data
  } catch (error) {
    console.error('공지사항 불러오기 실패:', error)
  }
}

const visibleNotices = computed(() => {
  if (expanded.value) {
    return notices.value.slice(0, 10); // 최대 10개까지만
  } else {
    return notices.value.slice(0, 3); // 기본 3개
  }
})


onMounted(fetchNotices)
</script>

<style scoped>
.notice-list {
  margin-bottom: 0;
}

.board-table {
  width: 100%;
  border-collapse: collapse;
  background-color: #f9f9f9;
}

.board-table th,
.board-table td {
  padding: 10px;
  text-align: center;
  border-bottom: 1px solid #ccc;
  font-size: 0.95rem;
}

.board-table thead {
  background-color: #f1f1f1;
  border-top: 2px solid #888;
}

.board-table tbody tr:hover {
  background-color: #f5f5f5;
}

.toggle-row {
  cursor: pointer;
  background-color: #f1f1f1;
  font-size: 0.9rem;
  padding: 0.5rem 0;
  text-align: center;
  border-bottom: 1px solid #ccc;
}
.toggle-row:hover {
  text-decoration: underline;
}

</style>
