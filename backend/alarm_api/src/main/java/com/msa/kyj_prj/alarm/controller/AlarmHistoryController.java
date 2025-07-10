package com.msa.kyj_prj.alarm.controller;

import com.msa.kyj_prj.alarm.dto.Alarm;
import com.msa.kyj_prj.alarm.service.AlarmHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alarm/history")
@RequiredArgsConstructor
public class AlarmHistoryController {

    private final AlarmHistoryService alarmHistoryService;

    // 1. 알림함(알림 리스트) 조회 (페이징)
    @GetMapping
    public List<Alarm> getAlarmList(
            @RequestParam("receiver_id") int receiverId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return alarmHistoryService.getAlarmList(receiverId, page, size);
    }

    // 2. 알림 전체 개수 조회 (페이징용)
    @GetMapping("/count")
    public int getAlarmCount(@RequestParam("receiver_id") int receiverId) {
        return alarmHistoryService.getAlarmCount(receiverId);
    }

    // 3. 알림 등록 (테스트용)
    @PostMapping
    public int registerAlarm(@RequestBody Alarm alarm) {
        return alarmHistoryService.registerAlarm(alarm);
    }

    // 4. 단일 알림 읽음 처리
    @PutMapping("/read/{alarm_id}")
    public int readAlarm(@PathVariable("alarm_id") int alarmId) {
        return alarmHistoryService.readAlarm(alarmId);
    }

    // 5. 전체 알림 읽음 처리
    @PutMapping("/read/all")
    public int readAllAlarms(@RequestParam("receiver_id") int receiverId) {
        return alarmHistoryService.readAllAlarms(receiverId);
    }

    // 6. 단일 알림 삭제
    @DeleteMapping("/{alarm_id}")
    public int deleteAlarm(@PathVariable("alarm_id") int alarmId) {
        return alarmHistoryService.deleteAlarm(alarmId);
    }

    // 7. 전체 알림 삭제
    @DeleteMapping("/all")
    public int deleteAllAlarms(@RequestParam("receiver_id") int receiverId) {
        return alarmHistoryService.deleteAllAlarms(receiverId);
    }
    
   

    
}
