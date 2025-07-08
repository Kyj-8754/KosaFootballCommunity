package com.msa.kyj_prj.alarm.service;

import com.msa.kyj_prj.alarm.dao.AlarmDAO;
import com.msa.kyj_prj.alarm.dto.Alarm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmHistoryService {

    private final AlarmDAO alarmDAO;

    // 1. 알림 저장(등록)
    @Transactional
    public int registerAlarm(Alarm alarm) {
        return alarmDAO.insertAlarm(alarm);
    }

    // 2. 알림 리스트 조회 (페이징)
    public List<Alarm> getAlarmList(int receiver_id, int page, int size) {
        int pageStart = (page - 1) * size;
        return alarmDAO.selectAlarmList(receiver_id, pageStart, size);
    }

    // 3. 전체 알림 개수 (페이징용)
    public int getAlarmCount(int receiver_id) {
        return alarmDAO.countAlarm(receiver_id);
    }

    // 4. 단일 알림 읽음 처리
    @Transactional
    public int readAlarm(int alarm_id) {
        return alarmDAO.updateAlarmRead(alarm_id);
    }

    // 5. 전체 알림 읽음 처리
    @Transactional
    public int readAllAlarms(int receiver_id) {
        return alarmDAO.updateAllAlarmRead(receiver_id);
    }

    // 6. 단일 알림 삭제
    @Transactional
    public int deleteAlarm(int alarm_id) {
        return alarmDAO.deleteAlarm(alarm_id);
    }

    // 7. 전체 알림 삭제
    @Transactional
    public int deleteAllAlarms(int receiver_id) {
        return alarmDAO.deleteAllAlarm(receiver_id);
    }
}
