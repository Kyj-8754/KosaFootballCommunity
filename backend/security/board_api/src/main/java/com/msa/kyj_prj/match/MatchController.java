package com.msa.kyj_prj.match;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/social")
    public ResponseEntity<?> getSocialMatches(@RequestParam Map<String, Object> params) {
        try {
            return ResponseEntity.ok(matchService.getSocialMatches(params));
        } catch (Exception e) {
            log.error("소셜 매치 목록 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("소셜 매치 목록 조회 실패");
        }
    }

    @GetMapping("/league")
    public ResponseEntity<?> getLeagueMatches(@RequestParam Map<String, Object> params) {
        try {
            return ResponseEntity.ok(matchService.getLeagueMatches(params));
        } catch (Exception e) {
            log.error("리그 매치 목록 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리그 매치 목록 조회 실패");
        }
    }

    @GetMapping("/{id}")
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
    public ResponseEntity<?> checkUserApplied(@RequestParam Long matchId, @RequestParam Integer userNo) {
        try {
            return ResponseEntity.ok(matchService.hasUserApplied(matchId, userNo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("참가 여부 조회 실패");
        }
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<?> cancelMatchParticipant(@RequestParam Long matchId, @RequestParam Integer userNo) {
        try {
            matchService.cancelMatchParticipant(matchId, userNo);
            return ResponseEntity.ok("참가 신청 취소 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("참가 취소 실패");
        }
    }

    @GetMapping("/participants/count")
    public ResponseEntity<?> getMatchParticipantCount(@RequestParam Long matchId) {
        try {
            return ResponseEntity.ok(matchService.getMatchParticipantCount(matchId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인원 수 조회 실패");
        }
    }

    @GetMapping("/club")
    public ResponseEntity<?> getClubByUserNo(@RequestParam Long userNo) {
        try {
            return ResponseEntity.ok(matchService.getClubByUserNo(userNo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 정보 조회 실패");
        }
    }

    @GetMapping("/areas")
    public ResponseEntity<?> getAllAreanms() {
        try {
            return ResponseEntity.ok(matchService.getAllAreanms());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("지역명 조회 실패");
        }
    }

    @GetMapping("/participants")
    public ResponseEntity<?> getMatchParticipants(@RequestParam Long matchId) {
        try {
            return ResponseEntity.ok(matchService.getMatchParticipantsWithNames(matchId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("참가자 조회 실패");
        }
    }

    @GetMapping("/participantswithclub")
    public ResponseEntity<?> selectParticipantsWithClubByMatchId(@RequestParam Long matchId) {
        try {
            return ResponseEntity.ok(matchService.selectParticipantsWithClubByMatchId(matchId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("참가자/클럽 조회 실패");
        }
    }

    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody Map<String, Object> param) {
        try {
            matchService.updateMatchParticipantStatus(param);
            return ResponseEntity.ok("상태 업데이트 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("상태 업데이트 실패");
        }
    }

    @GetMapping("/reservation-id")
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
    public ResponseEntity<?> getFilteredClubMatches(@RequestParam Long clubId) {
        try {
            return ResponseEntity.ok(matchService.getFilteredClubMatches(clubId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 매치 필터 조회 실패");
        }
    }

    @PostMapping("/apply/approve")
    public ResponseEntity<?> applyAndApproveImmediately(@RequestBody MatchParticipant participant) {
        try {
            matchService.applyAndApproveImmediately(participant);
            return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "참가와 승인 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("res_code", "500", "res_msg", "참가 승인 중 오류: " + e.getMessage()));
        }
    }

    @PostMapping("/matches/cancel")
    public ResponseEntity<String> cancelMatches(@RequestParam String type, @RequestParam Long id) {
        try {
            matchService.cancelMatchesByTypeAndId(type, id);
            return ResponseEntity.ok("✅ 매치 상태 변경 완료 (type=" + type + ", id=" + id + ")");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ 잘못된 요청: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ 매치 상태 변경 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }

    @GetMapping("/recent-completed")
    public ResponseEntity<?> getRecentCompletedMatches() {
        try {
            return ResponseEntity.ok(matchService.getRecentCompletedMatches());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("최근 완료된 매치 조회 실패");
        }
    }
}
