<template>
  <div style="position:relative;">
    <button @click="toggleList" class="btn btn-outline-primary btn-sm" style="margin-left: 16px;">🔔 알림</button>
    <div v-if="showList" class="alarm-list-popup">
      <div style="text-align:right; margin-bottom: 8px;">
        <!-- ✅ 전체읽음/전체삭제 버튼 추가 -->
        <button @click="readAll" class="btn btn-link btn-xs">전체읽음</button>
        <button @click="deleteAll" class="btn btn-link btn-xs text-danger">전체삭제</button>
      </div>
      <ul style="padding:0;">
        <li v-for="alarm in alarms" :key="alarm.alarm_id" style="border-bottom:1px solid #eee; padding:8px 0;">
          <div>
            <span :style="{fontWeight: alarm.read_yn==='N' ? 'bold':'normal'}">
              {{ alarm.message }}
            </span>
            <small style="color: #888; font-size:0.85em;">({{ alarm.created_at }})</small>
          </div>
          <button v-if="alarm.read_yn==='N'" @click="readAlarm(alarm.alarm_id)" class="btn btn-outline-success btn-xs">읽음</button>
          <button @click="deleteAlarm(alarm.alarm_id)" class="btn btn-outline-danger btn-xs">삭제</button>
        </li>
        <li v-if="alarms.length === 0" style="color: #aaa; padding: 18px 0;">알림이 없습니다.</li>
      </ul>
      <!-- ✅ 전체 알림 개수 표시 -->
      <div style="text-align:right; font-size:12px; color:#888;">
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
  methods: {
    toggleList() {
      this.showList = !this.showList;
      if (this.showList) this.fetchAlarms();
    },
    fetchAlarms() {
      if (!this.userNo) return;
      // ✅ 프록시 주소로 변경!
      axios.get('/alarm_api/history', {
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
      axios.get('/alarm_api/history/count', {
        params: { receiver_id: this.userNo }
      }).then(res => {
        this.alarmCount = res.data;
      });
    },
    readAlarm(alarmId) {
      // ✅ 프록시 주소로 변경!
      axios.put(`/alarm_api/history/read/${alarmId}`).then(() => {
        this.fetchAlarms();
      });
    },
    readAll() {
      // ✅ 프록시 주소로 변경!
      axios.put('/alarm_api/history/read/all', {
        params: { receiver_id: this.userNo }
      }).then(() => {
        this.fetchAlarms();
      });
    },
    deleteAlarm(alarmId) {
      // ✅ 프록시 주소로 변경!
      axios.delete(`/alarm_api/history/${alarmId}`).then(() => {
        this.fetchAlarms();
      });
    },
    deleteAll() {
      // ✅ 프록시 주소로 변경!
      axios.delete('/alarm_api/history/all', {
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
.alarm-list-popup {
  position: absolute;
  top: 40px;
  right: 0;
  width: 320px;
  background: #fff;
  border: 1px solid #ddd;
  max-height: 350px;
  overflow-y: auto;
  z-index: 10;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  padding: 1rem;
}
.btn-xs {
  font-size: 11px;
  padding: 2px 8px;
  margin-left: 5px;
}
</style>
