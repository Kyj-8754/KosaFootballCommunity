package com.msa.kyj_prj.match;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/match")
@Tag(name = "매치 API", description = "매치 게시글과 매치를 다루는 API입니다.")
public class MatchController {

	@Autowired
	private MatchService matchService;

    @GetMapping("/social")
    @Operation(summary = "소셜매치 리스트 조회", description = "소셜매치의 리스트를 조회합니다.")
    public ResponseEntity<?> getSocialMatches(@RequestParam Map<String, Object> params) {
        try {
            return ResponseEntity.ok(matchService.getSocialMatches(params));
        } catch (Exception e) {
            log.error("소셜 매치 목록 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("소셜 매치 목록 조회 실패");
        }
    }

    @GetMapping("/league")
    @Operation(summary = "리그매치 리스트 조회", description = "리그매치의 리스트를 조회합니다.")
    public ResponseEntity<?> getLeagueMatches(@RequestParam Map<String, Object> params) {
        try {
            return ResponseEntity.ok(matchService.getLeagueMatches(params));
        } catch (Exception e) {
            log.error("리그 매치 목록 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리그 매치 목록 조회 실패");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "매치 상세조회", description = "해당 매치의 상세정보를 조회합니다.")
    public ResponseEntity<?> getMatchDetail(@PathVariable("id") Long matchId) {
        try {
            Match match = matchService.getMatchDetail(matchId);
            if (match == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 매치를 찾을 수 없습니다.");
            return ResponseEntity.ok(match);
        } catch (Exception e) {
            log.error("매치 상세 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("매치 상세 조회 실패");
        }
    }

    @PostMapping("/apply")
    @Operation(summary = "매치 참가 신청", description = "해당 매치에 참가 신청을 넣습니다.")
    public ResponseEntity<?> applyToMatch(@RequestBody MatchParticipant participant) {
        try {
            matchService.applyToMatch(participant);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "참가 신청 중 문제가 발생했습니다."));
        }
    }

    @GetMapping("/applied")
    @Operation(summary = "매치 참가신청 여부 확인", description = "신청 여부를 확인합니다.")
    public ResponseEntity<?> checkUserApplied(@RequestParam Long matchId, @RequestParam Integer userNo) {
        try {
            return ResponseEntity.ok(matchService.hasUserApplied(matchId, userNo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("참가 여부 조회 실패");
        }
    }

    @DeleteMapping("/cancel")
    @Operation(summary = "매치 참가 신청 취소", description = "해당 매치에 참가 신청을 취소합니다.")
    public ResponseEntity<?> cancelMatchParticipant(@RequestParam Long matchId, @RequestParam Integer userNo) {
        try {
            matchService.cancelMatchParticipant(matchId, userNo);
            return ResponseEntity.ok("참가 신청 취소 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("참가 취소 실패");
        }
    }

    @GetMapping("/participants/count")
    @Operation(summary = "매치 참가자 인원수 조회", description = "해당 매치의 참가자 인원수를 조회합니다.")
    public ResponseEntity<?> getMatchParticipantCount(@RequestParam Long matchId) {
        try {
            return ResponseEntity.ok(matchService.getMatchParticipantCount(matchId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인원 수 조회 실패");
        }
    }

    @GetMapping("/club")
    @Operation(summary = "소속팀 정보 조회", description = "해당 참가자의 소속팀을 조회합니다.")
    public ResponseEntity<?> getClubByUserNo(@RequestParam Long userNo) {
        try {
            return ResponseEntity.ok(matchService.getClubByUserNo(userNo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 정보 조회 실패");
        }
    }

    @GetMapping("/areas")
    @Operation(summary = "지역명 조회", description = "현재 매치가 이뤄지고 있는 지역명을 조회합니다.")
    public ResponseEntity<?> getAllAreanms() {
        try {
            return ResponseEntity.ok(matchService.getAllAreanms());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("지역명 조회 실패");
        }
    }

    @GetMapping("/participants")
    @Operation(summary = "매치 참가자 조회(소셜)", description = "해당 매치 참가자의 간략한 정보를 조회합니다.")
    public ResponseEntity<?> getMatchParticipants(@RequestParam Long matchId) {
        try {
            return ResponseEntity.ok(matchService.getMatchParticipantsWithNames(matchId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("참가자 조회 실패");
        }
    }

    @GetMapping("/participantswithclub")
    @Operation(summary = "매치 참가자 조회(리그)", description = "해당 매치 참가자의 간략한 정보를 조회합니다.")
    public ResponseEntity<?> selectParticipantsWithClubByMatchId(@RequestParam Long matchId) {
        try {
            return ResponseEntity.ok(matchService.selectParticipantsWithClubByMatchId(matchId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("참가자/클럽 조회 실패");
        }
    }

    @PostMapping("/status")
    @Operation(summary = "매치 신청자 상태변경", description = "매치 신청자의 상태를 승인/거절로 변경합니다.")
    public ResponseEntity<?> updateStatus(@RequestBody Map<String, Object> param) {
        try {
            matchService.updateMatchParticipantStatus(param);
            return ResponseEntity.ok("상태 업데이트 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("상태 업데이트 실패");
        }
    }

    @GetMapping("/reservation-id")
    @Operation(summary = "예약 ID 조회", description = "해당 게시글에 대한 예약 정보를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getReservationIdByBoardId(@RequestParam Long boardId) {
        try {
            Long reservationId = matchService.getReservationIdByBoardId(boardId);
            if (reservationId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("res_code", "404", "res_msg", "해당 게시글에 대한 예약이 존재하지 않습니다."));
            }
            return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "예약 ID 조회 성공", "reservation_id", reservationId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("res_code", "500", "res_msg", "서버 오류 발생: " + e.getMessage()));
        }
    }

    @GetMapping("/reservation-paid")
    @Operation(summary = "결제 상태 조회", description = "해당 예약에 대한 결제 상태를 조회합니다.")
    public ResponseEntity<Map<String, Object>> isReservationPaid(@RequestParam Long reservationId) {
        try {
            boolean paid = matchService.isReservationPaid(reservationId);
            return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "결제 상태 조회 성공", "paid", paid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("res_code", "400", "res_msg", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("res_code", "500", "res_msg", "서버 오류 발생: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "매치 등록", description = "매치를 등록합니다.")
    public ResponseEntity<?> registerMatch(@RequestBody Match match) {
        try {
            matchService.registerMatch(match);
            return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "매치 등록 성공"));
        } catch (Exception e) {
            log.error("❌ 매치 등록 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("res_code", "500", "res_msg", "매치 등록 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/club/matches")
    @Operation(summary = "클럽 매치 조회", description = "해당 클럽의 매치를 조회합니다.")
    public ResponseEntity<?> getFilteredClubMatches(@RequestParam Long clubId) {
        try {
            return ResponseEntity.ok(matchService.getFilteredClubMatches(clubId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 매치 필터 조회 실패");
        }
    }

    @PostMapping("/apply/approve")
    @Operation(summary = "클럽 매치 신청", description = "해당 매치의 참가와 승인을 진행합니다.")
    public ResponseEntity<?> applyAndApproveImmediately(@RequestBody MatchParticipant participant) {
        try {
            matchService.applyAndApproveImmediately(participant);
            return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "참가와 승인 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("res_code", "500", "res_msg", "참가 승인 중 오류: " + e.getMessage()));
        }
    }

    @PostMapping("/matches/cancel")
    @Operation(summary = "매치 취소", description = "예약/결제가 취소된 매치를 취소처리합니다.")
    public ResponseEntity<String> cancelMatches(@RequestParam String type, @RequestParam Long id) {
        try {
            matchService.cancelMatchesByTypeAndId(type, id);
            return ResponseEntity.ok("✅ 매치 상태 변경 완료 (type=" + type + ", id=" + id + ")");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ 잘못된 요청: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ 매치 상태 변경 중 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }

    @GetMapping("/recent-completed")
    @Operation(summary = "최근 완료된 매치 조회", description = "최근 완료된 매치를 조회합니다.")
    public ResponseEntity<?> getRecentCompletedMatches() {
        try {
            return ResponseEntity.ok(matchService.getRecentCompletedMatches());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("최근 완료된 매치 조회 실패");
        }
    }

	// //// 리그 매치 목록 조회
    @GetMapping("/league/closed")
    @Operation(summary = "리그 매치 조회", description = "리그 매치를 조회합니다.")
    public ResponseEntity<?> getClosedLeagueMatches() {
        try {
            List<Match> matches = matchService.getClosedLeagueMatches();
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            log.error("리그 매치 조회 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리그 매치 조회 실패");
        }
    }

}
