<template>
  <div style="position:relative;">
    <!-- 종 아이콘 버튼 -->
<button @click="toggleList" class="alarm-btn">
  <i class="bi bi-bell"></i>
  <span class="alarm-label">알림</span>
  <span v-if="unreadCount > 0" class="alarm-badge"></span>
</button>

    <!-- 알림 리스트 팝업 -->
    <div v-if="showList" class="alarm-list-popup">
      <div class="alarm-list-header">
        <span class="alarm-title">알림</span>
        <div class="alarm-actions">
          <button @click="readAll" class="btn-link-xs">전체읽음</button>
          <button @click="deleteAll" class="btn-link-xs text-danger">전체삭제</button>
        </div>
      </div>
      <ul class="alarm-list">
        <li
          v-for="alarm in alarms"
          :key="alarm.alarm_id"
          :class="['alarm-item', { unread: alarm.read_yn === 'N' }]"
        >
          <div class="alarm-message">
            <router-link :to="`/${alarm.url}`">
              <div class="alarm-msg-text">
                <span>{{ alarm.message }}</span>
                <span class="alarm-time">{{ alarm.created_at }}</span>
              </div>
            </router-link>
            <div>
              <button v-if="alarm.read_yn === 'N'" @click="readAlarm(alarm.alarm_id)" class="btn-xs btn-outline-success">읽음</button>
              <button @click="deleteAlarm(alarm.alarm_id)" class="btn-xs btn-outline-danger">삭제</button>
            </div>
          </div>
        </li>
        <li v-if="alarms.length === 0" class="alarm-empty">알림이 없습니다.</li>
      </ul>
      <div class="alarm-list-footer">
        전체 {{ alarmCount }}건
      </div>
    </div>
  </div>
</template>


<script>
import axios from "axios";
export default {
  name: "AlarmDropdown",
  props: {
    userNo: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      showList: false,
      alarms: [],
      alarmCount: 0
    };
  },

  computed: {
    // ✅ [여기에 추가]
    unreadCount() {
      // alarms 배열 중 read_yn이 'N'인 것만 개수 세기
      return this.alarms.filter(alarm => alarm.read_yn === 'N').length;
    }
  },
  methods: {
    toggleList() {
      this.showList = !this.showList;
      if (this.showList) this.fetchAlarms();
    },
    fetchAlarms() {
      if (!this.userNo) return;
      // ✅ 프록시 주소로 변경!
      axios.get('/alarm_api/alarm/history', {
        params: { receiver_id: this.userNo, page: 1, size: 10 }
      }).then(res => {
        this.alarms = res.data;
        this.fetchCount();
      }).catch(() => {
        this.alarms = [];
        this.alarmCount = 0;
      });
    },
    fetchCount() {
      // ✅ 프록시 주소로 변경!
      axios.get('/alarm_api/alarm/history/count', {
        params: { receiver_id: this.userNo }
      }).then(res => {
        this.alarmCount = res.data;
      });
    },
    readAlarm(alarmId) {
      // ✅ 프록시 주소로 변경!
      axios.put(`/alarm_api/alarm/history/read/${alarmId}`).then(() => {
        this.fetchAlarms();
      });
    },
  readAll() {
  axios.put('/alarm_api/alarm/history/read/all', null, {
  params: { receiver_id: this.userNo }
  }).then(() => {
  this.alarms.forEach(alarm => alarm.read_yn = 'Y');
  // 또는 this.fetchAlarms();
  });

}

,
    deleteAlarm(alarmId) {
      // ✅ 프록시 주소로 변경!
      axios.delete(`/alarm_api/alarm/history/${alarmId}`).then(() => {
        this.fetchAlarms();
      });
    },
    deleteAll() {
      // ✅ 프록시 주소로 변경!
      axios.delete('/alarm_api/alarm/history/all', {
        params: { receiver_id: this.userNo }
      }).then(() => {
        this.fetchAlarms();
      });
    }
  },
  watch: {
    userNo() {
      this.fetchAlarms();
    }
  },
  mounted() {
    this.fetchAlarms();
  }
};
</script>

<style scoped>
.alarm-btn {
  background: none;
  border: none;
  position: relative;
  font-size: 1.5rem;
  margin-left: 14px;
  cursor: pointer;
  color: #222;
  display: flex;
  align-items: center;
  font-weight: 500;
}
.alarm-btn .bi-bell {
  font-size: 1.15rem;   /* ✅ ← 아이콘 크기 줄이기 */
  margin-right: 6px;    /* ✅ ← 알림 글씨랑 간격 */
  vertical-align: middle;
}
.alarm-label {
  font-size: 1rem;      /* 알림 텍스트 크기 */
  font-weight: 500;
}
.alarm-badge {
  position: absolute;
  top: 4px;
  right: 2px;
  width: 10px;
  height: 10px;
  background: #e74c3c;
  border-radius: 50%;
  border: 2px solid #fff;
}

.alarm-list-popup {
  position: absolute;
  top: 38px;
  right: 0;
  width: 350px;
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  z-index: 200;
  max-height: 400px;
  overflow-y: auto;
  min-width: 320px;
  padding: 0;
}
.alarm-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px 8px 20px;
  border-bottom: 1px solid #f0f0f0;
}
.alarm-title {
  font-weight: bold;
  font-size: 1.1rem;
}
.alarm-actions button {
  font-size: 11px;
  background: none;
  border: none;
  color: #1976d2;
  margin-left: 8px;
  cursor: pointer;
}
.alarm-actions .text-danger {
  color: #e74c3c !important;
}
.alarm-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.alarm-item {
  padding: 14px 20px;
  border-bottom: 1px solid #f4f4f4;
  transition: background 0.12s;
  font-size: 1rem;
}
.alarm-item.unread {
  background: #f9fcff;
  font-weight: bold;
}
.alarm-message {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.alarm-msg-text {
  display: flex;
  flex-direction: column;
}
.alarm-time {
  font-size: 12px;
  color: #aaa;
  margin-top: 2px;
}
.alarm-empty {
  text-align: center;
  color: #bbb;
  padding: 28px 0 18px 0;
}
.alarm-list-footer {
  padding: 9px 20px 7px 20px;
  text-align: right;
  font-size: 12px;
  color: #888;
  background: #f8f9fa;
  border-top: 1px solid #f0f0f0;
  border-radius: 0 0 12px 12px;
}
.btn-xs {
  font-size: 11px;
  padding: 2px 9px;
  margin-left: 4px;
  border-radius: 5px;
  cursor: pointer;
  border: 1px solid #dedede;
  background: #fff;
  transition: background 0.15s;
}
.btn-xs:hover {
  background: #f2f2f2;
}
.btn-outline-success {
  color: #2ecc40;
  border-color: #b5e6c9;
}
.btn-outline-danger {
  color: #e74c3c;
  border-color: #ffcccc;
}
</style>
