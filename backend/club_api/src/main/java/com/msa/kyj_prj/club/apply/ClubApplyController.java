package com.msa.kyj_prj.club.apply;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.msa.kyj_prj.alarm.AlarmMessageDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/club/apply")
@RequiredArgsConstructor
public class ClubApplyController {

    private final ClubApplyService clubApplyService;
    private final RestTemplate restTemplate;

    @Value("${alarm.api.url}")
    private String alarmApiUrl;

    // 클럽(모집) 가입 신청(POST)
    @PostMapping
    public ResponseEntity<String> applyToRecruit(@RequestBody ClubApply clubApply) {
        int user_no = clubApply.getAppli_user_no();
        if (clubApply.getBno() == 0 || user_no == 0) {
            return ResponseEntity.badRequest().body("모집글 정보 또는 사용자 정보가 없습니다.");
        }
        try {
            AlarmMessageDTO alarm = clubApplyService.applyToRecruit(clubApply, user_no);
            if (alarm == null) {
                return ResponseEntity.badRequest().body("팀장 정보를 찾을 수 없습니다.");
            }
            try {
                restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
                return ResponseEntity.ok("클럽 신청 및 알림 전송 완료");
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("신청 성공, 그러나 알림 전송 실패");
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body("❌ 재가입 신청은 24시간 뒤에 해주세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 클럽 가입 신청 취소(DELETE)
    @DeleteMapping
    public ResponseEntity<String> cancelApply(@RequestBody ClubApply clubApply) {
        int bno = clubApply.getBno();
        int appli_user_no = clubApply.getAppli_user_no();
        if (bno == 0 || appli_user_no == 0) {
            return ResponseEntity.badRequest().body("모집글 정보 또는 사용자 정보가 없습니다.");
        }
        try {
            boolean result = clubApplyService.cancelApply(bno, appli_user_no);
            if (result) {
                return ResponseEntity.ok("가입 신청이 취소되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("취소 처리 실패 (이미 취소했거나 존재하지 않는 신청)");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 가입 신청 상태 조회(GET)
    @GetMapping("/status")
    public ResponseEntity<?> getApplyStatus(@RequestParam("bno") int bno, @RequestParam("user_no") int user_no) {
        if (bno == 0 || user_no == 0) {
            return ResponseEntity.badRequest().body("모집글 정보 또는 사용자 정보가 없습니다.");
        }
        try {
            ClubApply last = clubApplyService.findLastApplyByBnoAndApplicant(bno, user_no);
            return ResponseEntity.ok(Map.of("status", last != null ? last.getStatus() : "none"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("가입 신청 상태 조회 중 오류가 발생했습니다.");
        }
    }

    // 클럽 가입 승인(POST)
    @PostMapping("/approve")
    public ResponseEntity<String> approveApply(@RequestBody Map<String, Object> body) {
        if (body == null || !body.containsKey("apply_id")) {
            return ResponseEntity.badRequest().body("신청 ID가 필요합니다.");
        }
        int apply_id = (int) body.get("apply_id");
        try {
            boolean result = clubApplyService.approveApply(apply_id);
            if (result) {
                return ResponseEntity.ok("승인 완료");
            } else {
                return ResponseEntity.badRequest().body("승인 처리 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 클럽 가입 거절(POST)
    @PostMapping("/reject")
    public ResponseEntity<String> rejectApply(@RequestBody Map<String, Object> body) {
        if (body == null || !body.containsKey("apply_id")) {
            return ResponseEntity.badRequest().body("신청 ID가 필요합니다.");
        }
        int apply_id = (int) body.get("apply_id");
        try {
            boolean result = clubApplyService.rejectApply(apply_id);
            if (result) {
                return ResponseEntity.ok("거절 완료");
            } else {
                return ResponseEntity.badRequest().body("거절 처리 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 클럽 멤버 직접 추가(POST)
    @PostMapping("/member")
    public ResponseEntity<String> addMember(@RequestBody Map<String, Object> body) {
        if (body == null || !body.containsKey("club_id") || !body.containsKey("user_no")) {
            return ResponseEntity.badRequest().body("필수 정보가 누락되었습니다.");
        }
        int club_id = (int) body.get("club_id");
        int user_no = (int) body.get("user_no");
        try {
            int result = clubApplyService.insertClubMember(club_id, user_no);
            if (result > 0) {
                return ResponseEntity.ok("클럽 멤버로 추가되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("이미 가입된 멤버이거나 추가 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 클럽별 전체 가입 신청 목록 조회(GET)
    @GetMapping("/list")
    public ResponseEntity<?> getApplyListByClubId(@RequestParam("club_id") int club_id) {
        if (club_id == 0) {
            return ResponseEntity.badRequest().body("클럽 ID가 필요합니다.");
        }
        try {
            List<ClubApply> list = clubApplyService.findByClubId(club_id);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("신청 목록 조회 중 오류가 발생했습니다.");
        }
    }

    // 클럽별 전체 신청자+이름 목록 조회(GET)
    @GetMapping("/listWithName")
    public ResponseEntity<?> getApplyListByClubIdWithUserName(@RequestParam("club_id") int club_id) {
        if (club_id == 0) {
            return ResponseEntity.badRequest().body("클럽 ID가 필요합니다.");
        }
        try {
            List<Map<String, Object>> list = clubApplyService.findByClubIdWithUserName(club_id);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("신청 목록(이름 포함) 조회 중 오류가 발생했습니다.");
        }
    }

    // 클럽 멤버 자진 탈퇴(POST)
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawMember(@RequestParam("club_id") int club_id,
                                                 @RequestParam("user_no") int user_no) {
        if (club_id == 0 || user_no == 0) {
            return ResponseEntity.badRequest().body("클럽 또는 유저 정보가 없습니다.");
        }
        try {
            boolean result = clubApplyService.withdrawClubMember(club_id, user_no);
            if (result) {
                return ResponseEntity.ok("정상적으로 클럽을 탈퇴하였습니다.");
            } else {
                return ResponseEntity.badRequest().body("탈퇴 처리 실패 (이미 탈퇴했거나 승인 전 상태)");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 클럽 멤버 강퇴(POST)
    @PostMapping("/kick")
    public ResponseEntity<String> kickMember(@RequestParam("club_id") int club_id,
                                             @RequestParam("user_no") int user_no) {
        if (club_id == 0 || user_no == 0) {
            return ResponseEntity.badRequest().body("클럽 또는 유저 정보가 없습니다.");
        }
        try {
            boolean result = clubApplyService.kickClubMember(club_id, user_no);
            if (result) {
                return ResponseEntity.ok("해당 멤버를 강퇴 처리하였습니다.");
            } else {
                return ResponseEntity.badRequest().body("강퇴 처리 실패 (이미 탈퇴/강퇴된 상태거나 승인 전)");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // [1] 클럽 detail에서 직접 신청 (POST)
    @PostMapping("/club")
    public ResponseEntity<?> applyToClub(@RequestBody ClubApply clubApply) {
        int appli_user_no = clubApply.getAppli_user_no();
        int club_id = clubApply.getClub_id();
        if (club_id == 0 || appli_user_no == 0) {
            return ResponseEntity.badRequest().body("클럽 정보 또는 사용자 정보가 없습니다.");
        }
        try {
            AlarmMessageDTO alarm = clubApplyService.applyToClub(clubApply, appli_user_no);
            if (alarm != null) {
                try {
                    restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
                    return ResponseEntity.ok("✅ 클럽 신청 및 알림 전송 완료");
                } catch (Exception e) {
                    return ResponseEntity.internalServerError().body("신청 성공, 그러나 알림 전송 실패");
                }
            } else {
                return ResponseEntity.ok("✅ 클럽 신청 완료 (알림 없음)");
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // [2] 클럽 detail에서 신청 취소 (DELETE)
    @DeleteMapping("/club")
    public ResponseEntity<?> cancelApplyByClubId(@RequestBody ClubApply clubApply) {
        int club_id = clubApply.getClub_id();
        int appli_user_no = clubApply.getAppli_user_no();
        if (club_id == 0 || appli_user_no == 0) {
            return ResponseEntity.badRequest().body("클럽 정보 또는 사용자 정보가 없습니다.");
        }
        try {
            boolean result = clubApplyService.cancelApplyByClubId(club_id, appli_user_no);
            if (result) {
                return ResponseEntity.ok("가입 신청이 취소되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("취소 처리 실패 (이미 취소했거나 존재하지 않는 신청)");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // [3] club_id로 상태 조회 (GET)
    @GetMapping("/club/status")
    public ResponseEntity<?> getApplyStatusByClubId(@RequestParam("club_id") int club_id,
                                                    @RequestParam("user_no") int user_no) {
        if (club_id == 0 || user_no == 0) {
            return ResponseEntity.badRequest().body("클럽 또는 사용자 정보가 없습니다.");
        }
        try {
            ClubApply last = clubApplyService.findLastApplyByClubIdAndApplicant(club_id, user_no);
            return ResponseEntity.ok(Map.of("status", last != null ? last.getStatus() : "none"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("가입 신청 상태 조회 중 오류가 발생했습니다.");
        }
    }
}
