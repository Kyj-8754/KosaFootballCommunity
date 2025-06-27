package com.msa.kyj_prj.club.apply;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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


	@PostMapping
	public ResponseEntity<String> applyToRecruit(@RequestBody ClubApply clubApply) {
	    // ✅ appli_user_no 필드에서 사용자 번호(user_no) 추출
		int user_no = clubApply.getAppli_user_no();  // 이미 JSON 안에 포함되어 있음

	    // ✅ 클럽 신청 처리 서비스 호출
	    AlarmMessageDTO alarm = clubApplyService.applyToRecruit(clubApply, user_no);

	    if (alarm == null) {
	        // ❌ 팀장 정보를 찾을 수 없거나 클럽 ID 조회 실패 시
	        return ResponseEntity.badRequest().body("팀장 정보를 찾을 수 없습니다.");
	    }

	    // ✅ 알림 서버 (알람 API)로 전송
	    try {
	        restTemplate.postForEntity(alarmApiUrl + "/api/alarm/send", alarm, Void.class);
	        return ResponseEntity.ok("클럽 신청 및 알림 전송 완료");
	    } catch (Exception e) {
	        System.err.println("🔴 알림 전송 실패: " + e.getMessage());
	        return ResponseEntity.internalServerError().body("신청 성공, 그러나 알림 전송 실패");
	    }
	}


	// 📌 추후 확장 기능 예정:
	// - 모집글별 신청 목록 조회
	// - 신청 상태 변경 (승인/거절)
	// - 특정 신청 내역 조회
}
