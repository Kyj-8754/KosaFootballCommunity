package com.msa.kyj_prj.club.apply;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.msa.kyj_prj.alarm.AlarmMessageDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/club/apply")
@RequiredArgsConstructor
public class ClubApplyController {

    private final ClubApplyService clubApplyService;
    private final RestTemplate restTemplate; // ✅ @Bean 등록된 RestTemplate을 DI로 사용

    @Value("${alarm.api.url}")
    private String alarmApiUrl;  // ✅ 알람 서버 URL (properties에서 주입)

    /**
     * 클럽(모집) 가입 신청 엔드포인트
     * - 1. 클럽 신청 처리 및 알림 메시지 DTO 생성(서비스 레이어)
     * - 2. 알람 서버(8086)로 REST POST 전송
     */
    @PostMapping
    public ResponseEntity<String> applyToRecruit(@RequestBody ClubApply clubApply) {
        // 1. 신청자 user_no 추출 (보통 JSON에서 자동 매핑됨)
        int user_no = clubApply.getAppli_user_no();

        // 2. 서비스 레이어에서 신청 처리 및 알림 DTO 반환
        AlarmMessageDTO alarm = clubApplyService.applyToRecruit(clubApply, user_no);

        if (alarm == null) {
            // 서비스에서 null 반환 == 신청, 팀장 조회, DB저장 등 실패!
            return ResponseEntity.badRequest().body("팀장 정보를 찾을 수 없습니다.");
        }

        // 3. 알람 서버(8086)로 REST POST 전송
        try {
            restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
            // 200 OK 리턴: 신청 성공 + 알림 전송 성공
            return ResponseEntity.ok("클럽 신청 및 알림 전송 완료");
        } catch (Exception e) {
            // 알람 서버 장애, 네트워크 등으로 전송 실패 시
            System.err.println("🔴 알림 전송 실패: " + e.getMessage());
            // 프론트에서는 "신청은 성공했지만 알림만 실패"라는 의미로 메시지 전달
            return ResponseEntity.internalServerError().body("신청 성공, 그러나 알림 전송 실패");
        }
    }

    // 🔽 확장: 추후 모집글별 신청 목록 조회/상태 변경/단건 조회 등 추가 가능
    // @GetMapping, @PatchMapping, @DeleteMapping 등으로 별도 엔드포인트 추가 예정
}
