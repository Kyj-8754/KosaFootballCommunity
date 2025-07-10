package com.msa.kyj_prj.alarm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.msa.kyj_prj.alarm.dto.Alarm;

@Mapper
public interface AlarmDAO {

    // 1. 알림 등록
    int insertAlarm(Alarm alarm);

    // 2. 알림 리스트 조회 (페이징)
    List<Alarm> selectAlarmList(
        @Param("receiver_id") int receiver_id,
        @Param("pageStart") int pageStart,
        @Param("pageSize") int pageSize
    );

    // 3. 알림 읽음 처리 (단일)
    int updateAlarmRead(@Param("alarm_id") int alarm_id);

    // 4. 알림 전체 읽음 처리 (수신자 전체)
    int updateAllAlarmRead(@Param("receiver_id") int receiver_id);

    // 5. 알림 전체 개수 (페이징 total count용)
    int countAlarm(@Param("receiver_id") int receiver_id);

    // 6. 알림 단일 삭제
    int deleteAlarm(@Param("alarm_id") int alarm_id);

    // 7. 알림 전체 삭제 (수신자 전체)
    int deleteAllAlarm(@Param("receiver_id") int receiver_id);
}
