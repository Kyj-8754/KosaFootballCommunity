package com.msa.kyj_prj.alarm.controller;

import com.msa.kyj_prj.alarm.dto.Alarm;
import com.msa.kyj_prj.alarm.service.AlarmHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alarm/history")
@RequiredArgsConstructor
@Tag(name = "Alarm History", description = "알림함(알림 내역) API")
public class AlarmHistoryController {

    private final AlarmHistoryService alarmHistoryService;

    // 1. 알림함(알림 리스트) 조회 (페이징)
    @Operation(
        summary = "알림 리스트 조회",
        description = "receiver_id로 알림 리스트를 페이징 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "파라미터 오류")
    })
    @GetMapping
    public List<Alarm> getAlarmList(
            @Parameter(description = "알림 수신자(유저) 번호", example = "3") @RequestParam("receiver_id") int receiverId,
            @Parameter(description = "페이지 번호(1부터 시작)", example = "1") @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "페이지당 개수", example = "20") @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return alarmHistoryService.getAlarmList(receiverId, page, size);
    }

    // 2. 알림 전체 개수 조회 (페이징용)
    @Operation(
        summary = "알림 개수 조회",
        description = "receiver_id로 전체 알림 개수를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/count")
    public int getAlarmCount(
            @Parameter(description = "알림 수신자(유저) 번호", example = "3") @RequestParam("receiver_id") int receiverId
    ) {
        return alarmHistoryService.getAlarmCount(receiverId);
    }

    // 3. 알림 등록 (테스트용)
    @Operation(
        summary = "알림 등록(테스트용)",
        description = "알림 객체(Alarm)를 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public int registerAlarm(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "알림 등록용 Alarm 객체 예시"
            )
            @RequestBody Alarm alarm
    ) {
        return alarmHistoryService.registerAlarm(alarm);
    }

    // 4. 단일 알림 읽음 처리
    @Operation(
        summary = "단일 알림 읽음 처리",
        description = "alarm_id로 단일 알림을 읽음 처리합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "읽음 처리 성공"),
        @ApiResponse(responseCode = "404", description = "알림이 존재하지 않음")
    })
    @PutMapping("/read/{alarm_id}")
    public int readAlarm(
            @Parameter(description = "알림 ID", example = "15") @PathVariable("alarm_id") int alarmId
    ) {
        return alarmHistoryService.readAlarm(alarmId);
    }

    // 5. 전체 알림 읽음 처리
    @Operation(
        summary = "전체 알림 읽음 처리",
        description = "receiver_id로 전체 알림을 읽음 처리합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "전체 읽음 처리 성공")
    })
    @PutMapping("/read/all")
    public int readAllAlarms(
            @Parameter(description = "알림 수신자(유저) 번호", example = "3") @RequestParam("receiver_id") int receiverId
    ) {
        return alarmHistoryService.readAllAlarms(receiverId);
    }

    // 6. 단일 알림 삭제
    @Operation(
        summary = "단일 알림 삭제",
        description = "alarm_id로 단일 알림을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "알림이 존재하지 않음")
    })
    @DeleteMapping("/{alarm_id}")
    public int deleteAlarm(
            @Parameter(description = "알림 ID", example = "15") @PathVariable("alarm_id") int alarmId
    ) {
        return alarmHistoryService.deleteAlarm(alarmId);
    }

    // 7. 전체 알림 삭제
    @Operation(
        summary = "전체 알림 삭제",
        description = "receiver_id로 전체 알림을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "전체 삭제 성공")
    })
    @DeleteMapping("/all")
    public int deleteAllAlarms(
            @Parameter(description = "알림 수신자(유저) 번호", example = "3") @RequestParam("receiver_id") int receiverId
    ) {
        return alarmHistoryService.deleteAllAlarms(receiverId);
    }
}
