package com.msa.kyj_prj.club.apply;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.msa.kyj_prj.alarm.AlarmMessageDTO; // AlarmMessageDTO는 별도 파일에 정의되어 있다고 가정

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.Data; // DTO 정의를 위해 추가
import lombok.NoArgsConstructor; // DTO 생성자 추가를 위해 추가
import lombok.AllArgsConstructor; // DTO 생성자 추가를 위해 추가

// Swagger/OpenAPI 어노테이션 임포트
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema; // ArraySchema 임포트 추가
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.msa.kyj_prj.club.apply.ClubApply;

@Slf4j
@RestController
@RequestMapping("/club/apply")
@RequiredArgsConstructor
@Tag(name = "Club Apply API", description = "클럽 가입 신청 및 멤버 관리 관련 API") // 컨트롤러에 대한 태그
public class ClubApplyController {

	private final ClubApplyService clubApplyService;
	private final RestTemplate restTemplate;

	@Value("${alarm.api.url}")
	private String alarmApiUrl;

	// 클럽(모집) 가입 신청(POST)
	@Operation(summary = "클럽 모집글에 가입 신청", description = "모집글 번호(bno)와 지원자 사용자 번호(appli_user_no)를 사용하여 클럽 가입을 신청합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 신청 및 알림 전송 완료", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "모집글 정보 또는 사용자 정보가 없거나 재가입 신청 제한", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409", description = "재가입 신청은 24시간 뒤에 해주세요.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류 또는 알림 전송 실패", content = @Content(schema = @Schema(implementation = String.class))) })
	@PostMapping
	public ResponseEntity<String> applyToRecruit(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "클럽 가입 신청 정보 (bno, appli_user_no 필요)", required = true, content = @Content(schema = @Schema(implementation = ClubApply.class))) @RequestBody ClubApply clubApply) {
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
	@Operation(summary = "클럽 가입 신청 취소", description = "모집글 번호(bno)와 지원자 사용자 번호(appli_user_no)를 사용하여 클럽 가입 신청을 취소합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "가입 신청이 취소되었습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "모집글 정보 또는 사용자 정보가 없거나 취소 처리 실패", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	@DeleteMapping
	public ResponseEntity<String> cancelApply(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "취소할 클럽 가입 신청 정보 (bno, appli_user_no 필요)", required = true, content = @Content(schema = @Schema(implementation = ClubApply.class))) @RequestBody ClubApply clubApply) {
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
	@Operation(summary = "가입 신청 상태 조회", description = "모집글 번호(bno)와 사용자 번호(user_no)를 사용하여 해당 사용자의 가입 신청 상태를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "가입 신청 상태 반환", content = @Content(schema = @Schema(implementation = ApplyStatusResponse.class))), // 명시적
																																								// DTO
																																								// 사용
			@ApiResponse(responseCode = "400", description = "모집글 정보 또는 사용자 정보가 없습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "가입 신청 상태 조회 중 오류 발생", content = @Content(schema = @Schema(implementation = String.class))) })
	@GetMapping("/status")
	public ResponseEntity<?> getApplyStatus(
			@Parameter(description = "모집글 번호", required = true, example = "123") @RequestParam("bno") int bno,
			@Parameter(description = "사용자 번호", required = true, example = "777") @RequestParam("user_no") int user_no) {
		if (bno == 0 || user_no == 0) {
			return ResponseEntity.badRequest().body("모집글 정보 또는 사용자 정보가 없습니다.");
		}
		try {
			ClubApply last = clubApplyService.findLastApplyByBnoAndApplicant(bno, user_no);
			// DTO로 변환하여 반환
			return ResponseEntity.ok(new ApplyStatusResponse(last != null ? last.getStatus() : "none"));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("가입 신청 상태 조회 중 오류가 발생했습니다.");
		}
	}

	// 클럽 가입 승인(POST)
	@Operation(summary = "클럽 가입 승인", description = "주어진 신청 ID(apply_id)에 해당하는 클럽 가입 신청을 승인합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "승인 완료", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "신청 ID가 없거나 승인 처리 실패", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	@PostMapping("/approve")
	public ResponseEntity<String> approveApply(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "승인할 신청 ID", required = true, content = @Content(schema = @Schema(implementation = ApplyIdRequest.class))) @RequestBody Map<String, Object> body) {
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
	@Operation(summary = "클럽 가입 거절", description = "주어진 신청 ID(apply_id)에 해당하는 클럽 가입 신청을 거절합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "거절 완료", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "신청 ID가 없거나 거절 처리 실패", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	public ResponseEntity<String> rejectApply(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "거절할 신청 ID", required = true, content = @Content(schema = @Schema(implementation = ApplyIdRequest.class))) @RequestBody Map<String, Object> body) {
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
	@Operation(summary = "클럽 멤버 직접 추가", description = "클럽 ID와 사용자 번호를 사용하여 클럽에 멤버를 직접 추가합니다. (승인 과정 없이 즉시 추가)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 멤버로 추가되었습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "필수 정보가 누락되었거나 이미 가입된 멤버이거나 추가 실패", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	@PostMapping("/member")
	public ResponseEntity<String> addMember(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "추가할 클럽 멤버 정보 (club_id, user_no 필요)", required = true, content = @Content(schema = @Schema(implementation = ClubMemberAddRequest.class))) @RequestBody Map<String, Object> body) {
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
	@Operation(summary = "클럽별 전체 가입 신청 목록 조회", description = "주어진 클럽 ID에 대한 모든 클럽 가입 신청 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "신청 목록을 성공적으로 조회했습니다.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClubApply.class)))), // List<ClubApply>
																																													// 반환
			@ApiResponse(responseCode = "400", description = "클럽 ID가 필요합니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "신청 목록 조회 중 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	@GetMapping("/list")
	public ResponseEntity<?> getApplyListByClubId(
			@Parameter(description = "신청 목록을 조회할 클럽의 ID", required = true, example = "101") @RequestParam("club_id") int club_id) {
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
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "신청 목록 조회 성공"),
			@ApiResponse(responseCode = "400", description = "클럽 ID가 필요합니다", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = String.class)))
		})
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
	@Operation(summary = "클럽 멤버 자진 탈퇴", description = "클럽 멤버가 본인의 의사로 클럽을 탈퇴합니다. (논리 삭제 처리)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "정상적으로 클럽을 탈퇴하였습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "클럽 또는 유저 정보가 없거나 탈퇴 처리 실패", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	@PostMapping("/withdraw")
	public ResponseEntity<String> withdrawMember(
			@Parameter(description = "탈퇴할 클럽의 ID", required = true, example = "101") @RequestParam("club_id") int club_id,
			@Parameter(description = "탈퇴할 사용자의 번호", required = true, example = "777") @RequestParam("user_no") int user_no) {
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
	@Operation(summary = "클럽 멤버 강퇴", description = "클럽 리더가 특정 멤버를 클럽에서 강제로 탈퇴시킵니다. (논리 삭제 처리)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "해당 멤버를 강퇴 처리하였습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "클럽 또는 유저 정보가 없거나 강퇴 처리 실패", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	@PostMapping("/kick")
	public ResponseEntity<String> kickMember(
			@Parameter(description = "강퇴할 클럽의 ID", required = true, example = "101") @RequestParam("club_id") int club_id,
			@Parameter(description = "강퇴할 사용자의 번호", required = true, example = "777") @RequestParam("user_no") int user_no) {
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
	@Operation(summary = "클럽 상세 페이지에서 직접 가입 신청", description = "클럽 상세 페이지에서 클럽 ID와 지원자 사용자 번호를 사용하여 가입을 신청합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 신청 및 알림 전송 완료 또는 알림 없음", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "클럽 정보 또는 사용자 정보가 없거나 재가입 신청 제한", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류 또는 알림 전송 실패", content = @Content(schema = @Schema(implementation = String.class))) })
	@PostMapping("/club")
	public ResponseEntity<?> applyToClub(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "클럽 가입 신청 정보 (club_id, appli_user_no 필요)", required = true, content = @Content(schema = @Schema(implementation = ClubApply.class))) @RequestBody ClubApply clubApply) {
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
	@Operation(summary = "클럽 상세 페이지에서 가입 신청 취소", description = "클럽 상세 페이지에서 클럽 ID와 지원자 사용자 번호를 사용하여 가입 신청을 취소합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "가입 신청이 취소되었습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "클럽 정보 또는 사용자 정보가 없거나 취소 처리 실패", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	@DeleteMapping("/club")
	public ResponseEntity<?> cancelApplyByClubId(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "취소할 클럽 가입 신청 정보 (club_id, appli_user_no 필요)", required = true, content = @Content(schema = @Schema(implementation = ClubApply.class))) @RequestBody ClubApply clubApply) {
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
	@Operation(summary = "클럽 상세 페이지에서 가입 신청 상태 조회", description = "클럽 ID와 사용자 번호를 사용하여 해당 클럽에 대한 사용자의 가입 신청 상태를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "가입 신청 상태 반환", content = @Content(schema = @Schema(implementation = ApplyStatusResponse.class))), // 명시적
																																								// DTO
																																								// 사용
			@ApiResponse(responseCode = "400", description = "클럽 또는 사용자 정보가 없습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "가입 신청 상태 조회 중 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	@GetMapping("/club/status")
	public ResponseEntity<?> getApplyStatusByClubId(
			@Parameter(description = "상태를 조회할 클럽의 ID", required = true, example = "101") @RequestParam("club_id") int club_id,
			@Parameter(description = "상태를 조회할 사용자의 번호", required = true, example = "777") @RequestParam("user_no") int user_no) {
		if (club_id == 0 || user_no == 0) {
			return ResponseEntity.badRequest().body("클럽 또는 사용자 정보가 없습니다.");
		}
		try {
			ClubApply last = clubApplyService.findLastApplyByClubIdAndApplicant(club_id, user_no);
			// DTO로 변환하여 반환
			return ResponseEntity.ok(new ApplyStatusResponse(last != null ? last.getStatus() : "none"));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("가입 신청 상태 조회 중 오류가 발생했습니다.");
		}
	}

	// --- 응답 및 요청을 위한 내부 DTO 정의 ---

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "신청 ID를 포함하는 요청 DTO")
	public static class ApplyIdRequest {
		@Schema(description = "신청 ID", example = "1")
		private int apply_id;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "클럽 멤버 추가 요청을 위한 DTO")
	public static class ClubMemberAddRequest {
		@Schema(description = "클럽 ID", example = "101")
		private int club_id;
		@Schema(description = "추가할 사용자의 번호", example = "777")
		private int user_no;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "가입 신청 상태 응답 DTO")
	public static class ApplyStatusResponse {
		@Schema(description = "신청 상태 (pending, approved, rejected, none)", example = "pending")
		private String status;
	}

}