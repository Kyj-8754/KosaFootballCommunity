package com.msa.kyj_prj.match.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/match-log")
@Tag(name = "매치 로그 API", description = "매치 로그를 다루는 API")
public class MatchLogController {

    @Autowired
    private MatchLogService matchLogService;

    // 특정 매치의 로그 전체 조회
    @GetMapping("/{match_id}")
    @Operation(summary = "로그 조회", description = "해당 매치의 로그를 조회합니다.")
    public ResponseEntity<?> getLogsByMatchId(@PathVariable("match_id") Long match_id) {
        try {
            return ResponseEntity.ok(matchLogService.getLogsByMatchId(match_id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "로그 조회 실패", "message", e.getMessage()));
        }
    }

    // 로그 등록
    @PostMapping("/add")
    @Operation(summary = "로그 등록", description = "해당 매치에 로그를 등록합니다.")
    public ResponseEntity<?> addMatchLog(@RequestBody MatchLog log) {
        try {
            int result = matchLogService.addMatchLog(log);
            return ResponseEntity.ok(Map.of("result", "inserted", "rowsAffected", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "로그 등록 실패", "message", e.getMessage()));
        }
    }

    // 로그 수정
    @PutMapping("/update")
    @Operation(summary = "로그 수정", description = "해당 매치의 로그를 수정합니다.")
    public ResponseEntity<?> updateMatchLog(@RequestBody MatchLog log) {
        try {
            int result = matchLogService.updateMatchLog(log);
            return ResponseEntity.ok(Map.of("result", "updated", "rowsAffected", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "로그 수정 실패", "message", e.getMessage()));
        }
    }

    // 로그 삭제
    @DeleteMapping("/delete/{log_id}")
    @Operation(summary = "로그 삭제", description = "해당 매치의 로그를 삭제합니다.")
    public ResponseEntity<?> deleteMatchLog(@PathVariable("log_id") Long log_id) {
        try {
            int result = matchLogService.deleteMatchLog(log_id);
            return ResponseEntity.ok(Map.of("result", "deleted", "rowsAffected", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "로그 삭제 실패", "message", e.getMessage()));
        }
    }

    // 승인된 참가자 목록 (user_no + user_name)
    @GetMapping("/approved-users/{match_id}")
    @Operation(summary = "참가자 조회", description = "해당 매치의 승인된 참가자 목록을 조회합니다.")
    public ResponseEntity<?> getApprovedUsers(@PathVariable("match_id") Long match_id) {
        try {
            return ResponseEntity.ok(matchLogService.getApprovedUsers(match_id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "승인된 사용자 목록 조회 실패", "message", e.getMessage()));
        }
    }

    // 승인된 팀 목록
    @GetMapping("/approved-teams/{match_id}")
    @Operation(summary = "팀 조회", description = "해당 매치의 승인된 팀 목록을 조회합니다.")
    public ResponseEntity<?> getApprovedTeams(@PathVariable("match_id") Long match_id) {
        try {
            return ResponseEntity.ok(matchLogService.getApprovedTeamsByMatchId(match_id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "승인된 팀 목록 조회 실패", "message", e.getMessage()));
        }
    }

    // 세트별 로그 리스트
    @GetMapping("/sets/{match_id}")
    @Operation(summary = "세트별 로그 조회", description = "해당 매치의 로그를 세트별로 나누어서 조회합니다.")
    public ResponseEntity<?> getMatchLogSets(@PathVariable("match_id") Long match_id) {
        try {
            List<MatchLog> allLogs = matchLogService.getLogsByMatchId(match_id);
            return ResponseEntity.ok(matchLogService.splitLogsByMatchSet(allLogs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "세트 분리 실패", "message", e.getMessage()));
        }
    }

    // log_type이 'POM'인 로그만 조회
    @GetMapping("/pom/{match_id}")
    @Operation(summary = "POM 조회", description = "해당 매치의 POM 로그를 조회합니다.")
    public ResponseEntity<?> getPomLogsByMatchId(@PathVariable("match_id") Long match_id) {
        try {
            return ResponseEntity.ok(matchLogService.getPomLogsByMatchId(match_id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "POM 로그 조회 실패", "message", e.getMessage()));
        }
    }
}
