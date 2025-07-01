package com.msa.kyj_prj.club.apply;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import com.msa.kyj_prj.alarm.AlarmMessageDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/club/apply")
@RequiredArgsConstructor
public class ClubApplyController {

    // (1) DI (서비스/알람) 선언
    private final ClubApplyService clubApplyService;
    private final RestTemplate restTemplate; // ✅ @Bean 등록된 RestTemplate을 DI로 사용

    @Value("${alarm.api.url}")
    private String alarmApiUrl;  // ✅ 알람 서버 URL (properties에서 주입)

    
    /**
     * (2) 클럽(모집) 가입 신청 엔드포인트 (POST)
     */
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
            // 24시간 재신청 금지, 중복신청 등 모든 예외를 같은 메시지로 안내
            return ResponseEntity.status(409).body("❌ 재가입 신청은 24시간 뒤에 해주세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    /**
     * (3) 클럽 가입 신청 취소 엔드포인트 (DELETE)
     */
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

    /**
     * (4) [중요 추가] 클럽 가입 신청 상태 조회 (GET)
     * - 프론트에서 새로고침 시, 내가 신청했는지 여부를 확인하기 위한 엔드포인트
     * - /club/apply/status?bno=8&user_no=1 형식으로 호출됨
     * - 반환: { is_applied: true/false }
     */
    @GetMapping("/status")
    public ResponseEntity<?> getApplyStatus(@RequestParam("bno") int bno, @RequestParam("user_no") int user_no) {
        try {
            // (4-1) 최신 이력(상태 관계없이) 조회
            ClubApply last = clubApplyService.findLastApplyByBnoAndApplicant(bno, user_no);
            boolean isApplied = last != null && "pending".equals(last.getStatus());
            // (4-2) 결과를 json 형식으로 반환
            return ResponseEntity.ok(java.util.Map.of("is_applied", isApplied));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("가입 신청 상태 조회 중 오류가 발생했습니다.");
        }
    }
    
    // 클럽 가입 승인 거절 코드
    @PostMapping("/approve")
    public ResponseEntity<String> approveApply(@RequestBody java.util.Map<String, Object> body) {
        int apply_id = (int) body.get("apply_id");

        boolean result = clubApplyService.approveApply(apply_id);

        if (result) {
            return ResponseEntity.ok("승인 완료");
        } else {
            return ResponseEntity.badRequest().body("승인 처리 실패");
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<String> rejectApply(@RequestBody java.util.Map<String, Object> body) {
        int apply_id = (int) body.get("apply_id");

        boolean result = clubApplyService.rejectApply(apply_id);

        if (result) {
            return ResponseEntity.ok("거절 완료");
        } else {
            return ResponseEntity.badRequest().body("거절 처리 실패");
        }
    }
    
    
    @PostMapping("/member")
    public ResponseEntity<String> addMember(@RequestBody java.util.Map<String, Object> body) {
        int club_id = (int) body.get("club_id");
        int user_no = (int) body.get("user_no");

        int result = clubApplyService.insertClubMember(club_id, user_no);

        if (result > 0) {
            return ResponseEntity.ok("클럽 멤버로 추가되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("이미 가입된 멤버이거나 추가 실패");
        }
    }
    
    @GetMapping("/list")
    public List<ClubApply> getApplyListByClubId(@RequestParam("club_id") int club_id) {
        System.out.println("club_id 파라미터 도착: " + club_id);  // ★ 여기!
        return clubApplyService.findByClubId(club_id);
    }

    
    
}
