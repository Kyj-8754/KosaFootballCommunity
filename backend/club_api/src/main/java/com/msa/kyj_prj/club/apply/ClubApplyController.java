package com.msa.kyj_prj.club.apply;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
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

    // 클럽(모집) 가입 신청(POST) - 사용자가 클럽 가입 신청을 한다
    @PostMapping
    public ResponseEntity<String> applyToRecruit(@RequestBody ClubApply clubApply) {
        int user_no = clubApply.getAppli_user_no();
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

    // 클럽 가입 신청 취소(DELETE) - 사용자가 본인 가입 신청을 취소한다(pending 상태만)
    @DeleteMapping
    public ResponseEntity<String> cancelApply(@RequestBody ClubApply clubApply) {
        int bno = clubApply.getBno();
        int appli_user_no = clubApply.getAppli_user_no();
        if (bno == 0 || appli_user_no == 0) {
            return ResponseEntity.badRequest().body("모집글 정보 또는 사용자 정보가 없습니다.");
        }
        boolean result = clubApplyService.cancelApply(bno, appli_user_no);
        if (result) {
            return ResponseEntity.ok("가입 신청이 취소되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("취소 처리 실패 (이미 취소했거나 존재하지 않는 신청)");
        }
    }

    // 클럽 가입 신청 상태 조회(GET) - 사용자가 해당 모집글에 신청 내역/상태를 조회
    // ex) /club/apply/status?bno=8&user_no=1
    @GetMapping("/status")
    public ResponseEntity<?> getApplyStatus(@RequestParam("bno") int bno, @RequestParam("user_no") int user_no) {
        try {
            ClubApply last = clubApplyService.findLastApplyByBnoAndApplicant(bno, user_no);
            return ResponseEntity.ok(Map.of("status", last != null ? last.getStatus() : "none"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("가입 신청 상태 조회 중 오류가 발생했습니다.");
        }
    }

    // 클럽 가입 승인(POST) - 팀장이 지원자의 신청을 승인한다
    @PostMapping("/approve")
    public ResponseEntity<String> approveApply(@RequestBody Map<String, Object> body) {
        int apply_id = (int) body.get("apply_id");
        boolean result = clubApplyService.approveApply(apply_id);
        if (result) {
            return ResponseEntity.ok("승인 완료");
        } else {
            return ResponseEntity.badRequest().body("승인 처리 실패");
        }
    }

    // 클럽 가입 거절(POST) - 팀장이 지원자의 신청을 거절한다
    @PostMapping("/reject")
    public ResponseEntity<String> rejectApply(@RequestBody Map<String, Object> body) {
        int apply_id = (int) body.get("apply_id");
        boolean result = clubApplyService.rejectApply(apply_id);
        if (result) {
            return ResponseEntity.ok("거절 완료");
        } else {
            return ResponseEntity.badRequest().body("거절 처리 실패");
        }
    }

    // 클럽 멤버 직접 추가(POST) - 별도 승인 없이 특정 유저를 멤버로 추가한다
    @PostMapping("/member")
    public ResponseEntity<String> addMember(@RequestBody Map<String, Object> body) {
        int club_id = (int) body.get("club_id");
        int user_no = (int) body.get("user_no");
        int result = clubApplyService.insertClubMember(club_id, user_no);
        if (result > 0) {
            return ResponseEntity.ok("클럽 멤버로 추가되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("이미 가입된 멤버이거나 추가 실패");
        }
    }

    // 클럽별 전체 가입 신청 목록 조회(GET) - 특정 클럽의 모든 신청 내역 조회
    @GetMapping("/list")
    public List<ClubApply> getApplyListByClubId(@RequestParam("club_id") int club_id) {
        return clubApplyService.findByClubId(club_id);
    }

    // 클럽별 전체 신청자+이름 목록 조회(GET) - 신청 내역과 사용자 이름을 함께 조회
    @GetMapping("/listWithName")
    public List<Map<String, Object>> getApplyListByClubIdWithUserName(@RequestParam("club_id") int club_id) {
        return clubApplyService.findByClubIdWithUserName(club_id);
    }

    // 클럽 멤버 자진 탈퇴(POST) - 승인된 멤버가 직접 탈퇴, status를 withdrawn으로 변경
    // ex) /club/apply/withdraw?club_id=1&user_no=10
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawMember(@RequestParam("club_id") int club_id,
                                                 @RequestParam("user_no") int user_no) {
        boolean result = clubApplyService.withdrawClubMember(club_id, user_no);
        if (result) {
            return ResponseEntity.ok("정상적으로 클럽을 탈퇴하였습니다.");
        } else {
            return ResponseEntity.badRequest().body("탈퇴 처리 실패 (이미 탈퇴했거나 승인 전 상태)");
        }
    }

    // 클럽 멤버 강퇴(POST) - 팀장/관리자가 멤버를 강퇴, status를 kicked로 변경
    // ex) /club/apply/kick?club_id=1&user_no=10
    @PostMapping("/kick")
    public ResponseEntity<String> kickMember(@RequestParam("club_id") int club_id,
                                             @RequestParam("user_no") int user_no) {
        boolean result = clubApplyService.kickClubMember(club_id, user_no);
        if (result) {
            return ResponseEntity.ok("해당 멤버를 강퇴 처리하였습니다.");
        } else {
            return ResponseEntity.badRequest().body("강퇴 처리 실패 (이미 탈퇴/강퇴된 상태거나 승인 전)");
        }
    }
}
