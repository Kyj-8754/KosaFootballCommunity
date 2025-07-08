package com.msa.kyj_prj.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;


    // 소셜 매치 목록 조회
    @GetMapping("/social")
    public List<Match> getSocialMatches(@RequestParam Map<String, Object> params) {
        return matchService.getSocialMatches(params);
    }


    // 리그 매치 목록 조회
    @GetMapping("/league")
    public List<Match> getLeagueMatches(@RequestParam Map<String, Object> params) {
        return matchService.getLeagueMatches(params);
    }

    // 매치 상세 조회
    @GetMapping("/{id}")
    public Match getMatchDetail(@PathVariable("id") Long matchId) {
        return matchService.getMatchDetail(matchId);
    }
    
    // 매치 참가 신청
    @PostMapping("/apply")
    public ResponseEntity<?> applyToMatch(@RequestBody MatchParticipant participant) {
        try {
            matchService.applyToMatch(participant);
            return ResponseEntity.ok().build(); // 성공 시 200 OK
        } catch (IllegalStateException e) {
            // 소셜/리그 인원 초과 예외
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409 Conflict
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "참가 신청 중 문제가 발생했습니다."));
        }
    }
    
    // 매치 참가 신청 여부 확인
    @GetMapping("/applied")
    public boolean checkUserApplied(@RequestParam Long matchId, @RequestParam Integer userNo) {
        return matchService.hasUserApplied(matchId, userNo);
    }
    
    // 매치 신청 취소
    @DeleteMapping("/cancel")
    public void cancelMatchParticipant(@RequestParam Long matchId, @RequestParam Integer userNo) {
        matchService.cancelMatchParticipant(matchId, userNo);
    }

    // 매치 인원 수 조회
    @GetMapping("/participants/count")
    public int getMatchParticipantCount(@RequestParam Long matchId) {
        return matchService.getMatchParticipantCount(matchId);
    }
    
    // 유저 번호로 클럽 정보 조회
    @GetMapping("/club")
    public Map<String, Object> getClubByUserNo(@RequestParam Long userNo) {
        return matchService.getClubByUserNo(userNo);
    }
    
    // 지역명 리스트 조회
    @GetMapping("/areas")
    public List<String> getAllAreanms() {
        return matchService.getAllAreanms();
    }
    
    // 특정 매치의 참가자 + 사용자 이름 조회
    @GetMapping("/participants")
    public List<Map<String, Object>> getMatchParticipants(@RequestParam Long matchId) {
        return matchService.getMatchParticipantsWithNames(matchId);
    }
    
    // 특정 매치의 참가자 + 사용자 + 클럽 명 이름 조회
    @GetMapping("/participantswithclub")
    public List<Map<String, Object>> selectParticipantsWithClubByMatchId(@RequestParam Long matchId) {
        return matchService.selectParticipantsWithClubByMatchId(matchId);
    }

    // 매치 참가자 상태 업데이트
    @PostMapping("/status")
    public void updateStatus(@RequestBody Map<String, Object> param) {
        matchService.updateMatchParticipantStatus(param);
    }
    
    // board_id로 reservation_id 조회
    @GetMapping("/reservation-id")
    public ResponseEntity<Map<String, Object>> getReservationIdByBoardId(@RequestParam Long boardId) {
        try {
            Long reservationId = matchService.getReservationIdByBoardId(boardId);  // MatchService 통해 호출

            if (reservationId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "res_code", "404",
                        "res_msg", "해당 게시글에 대한 예약이 존재하지 않습니다."
                    ));
            }

            return ResponseEntity.ok(Map.of(
                "res_code", "200",
                "res_msg", "예약 ID 조회 성공",
                "reservation_id", reservationId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "res_code", "500",
                    "res_msg", "서버 오류 발생: " + e.getMessage()
                ));
        }
    }
    
    // reservation_id로 결제 완료 여부 확인
    @GetMapping("/reservation-paid")
    public ResponseEntity<Map<String, Object>> isReservationPaid(@RequestParam Long reservationId) {
        try {
            boolean paid = matchService.isReservationPaid(reservationId);

            return ResponseEntity.ok(Map.of(
                "res_code", "200",
                "res_msg", "결제 상태 조회 성공",
                "paid", paid
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "res_code", "400",
                "res_msg", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "res_code", "500",
                "res_msg", "서버 오류 발생: " + e.getMessage()
            ));
        }
    }
}