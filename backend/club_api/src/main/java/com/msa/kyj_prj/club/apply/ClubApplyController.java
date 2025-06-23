package com.msa.kyj_prj.club.apply;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;


import com.msa.kyj_prj.alarm.AlarmMessageDTO;

@RestController
@RequestMapping("/api/club/apply")
@RequiredArgsConstructor
public class ClubApplyController {

    private final ClubApplyService clubApplyService;
    private final RestTemplate restTemplate;
    
    @Value("${alarm.api.url}")
    private String alarmApiUrl;

    // 지원서 등록
    @PostMapping
    public ResponseEntity<String> applyToRecruit(@RequestBody ClubApply clubApply, HttpSession session) {
        // ✅ 세션에서 로그인한 사용자 ID 가져오기
        String loginUserId = (String) session.getAttribute("loginUserid");
        if (loginUserId == null || loginUserId.isEmpty()) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // ✅ applicant_id를 세션 값으로 설정
        clubApply.setApplicant_id(loginUserId);

        // ✅ 클럽 신청 처리
        AlarmMessageDTO alarm = clubApplyService.applyToRecruit(clubApply);
        
        if (alarm == null) {
            return ResponseEntity.badRequest().body("팀장 정보를 찾을 수 없습니다.");
        }

        // ✅ 알림 서버로 전송
        try {
            restTemplate.postForEntity(alarmApiUrl + "/api/alarm/send", alarm, Void.class);
            return ResponseEntity.ok("클럽 신청 및 알림 전송 완료");
        } catch (Exception e) {
            System.err.println("🔴 알림 전송 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body("신청 성공, 그러나 알림 전송 실패");
        }
    }

    // 추후 기능:
    // - 모집글별 신청 목록 조회
    // - 신청 상태 변경 (승인/거절)
    // - 특정 신청 내역 조회
}
