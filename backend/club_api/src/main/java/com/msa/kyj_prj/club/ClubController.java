package com.msa.kyj_prj.club;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; // Spring Framework의 @RequestBody
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// Swagger/OpenAPI 어노테이션 임포트
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag; // io.swagger.v3.oas.annotations.tags.Tag는 별칭 없이 사용합니다.

@RestController
@RequestMapping("/club")
@Tag(name = "Club API", description = "클럽 관련 작업을 위한 API") // 컨트롤러에 대한 태그
public class ClubController {

	@Autowired
	private ClubService clubService;

	// 클럽 생성
	@Operation(summary = "새로운 클럽 생성", description = "클럽 이름, 팀 코드, 사용자 번호를 포함하여 새로운 클럽을 생성합니다.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "클럽이 성공적으로 생성되었습니다.", content = @Content),
			@ApiResponse(responseCode = "400", description = "필수 입력값이 누락되었거나 유효하지 않습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "클럽 생성 중 서버 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	@PostMapping("")
	public ResponseEntity<?> createClub(
			// OpenAPI의 @RequestBody는 전체 경로를 명시합니다.
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "생성할 클럽 정보 (club_name, team_code, user_no 필요)", required = true) @RequestBody Club club) { // Spring
																																											// Framework의
																																											// @RequestBody
		try {
			if (club == null || club.getClub_name() == null || club.getClub_name().trim().isEmpty()
					|| club.getTeam_code() == null || club.getTeam_code().trim().isEmpty() || club.getUser_no() == 0) {
				return ResponseEntity.badRequest().body("❌ 필수 입력값이 누락되었습니다.");
			}
			clubService.insert(club);
			return ResponseEntity.ok().build();
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 생성 실패: " + e.getMessage());
		}
	}

	// 팀 코드로 클럽 조회
	@Operation(summary = "팀 코드로 클럽 조회", description = "주어진 팀 코드를 사용하여 클럽 정보를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 정보를 성공적으로 조회했습니다.", content = @Content(schema = @Schema(implementation = Club.class))),
			@ApiResponse(responseCode = "400", description = "팀 코드가 누락되었습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "404", description = "해당 팀 코드로 클럽을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = String.class))) })
	@GetMapping("/code/{teamCode}")
	public ResponseEntity<?> getClubByTeamCode(
			@Parameter(description = "조회할 클럽의 팀 코드", required = true) @PathVariable String teamCode) {
		if (teamCode == null || teamCode.trim().isEmpty()) {
			return ResponseEntity.badRequest().body("팀 코드가 누락되었습니다.");
		}
		Club club = clubService.findByTeamCode(teamCode);
		if (club == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽을 찾을 수 없습니다.");
		}
		return ResponseEntity.ok(club);
	}

	// 클럽 이름 중복 여부 확인
	@Operation(summary = "클럽 이름 중복 확인", description = "제공된 클럽 이름이 사용 가능한지(중복되지 않는지) 확인합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 이름 사용 가능 여부 반환", content = @Content(schema = @Schema(implementation = Boolean.class))),
			@ApiResponse(responseCode = "400", description = "이름이 누락되었습니다.", content = @Content) })
	@GetMapping("/check-name")
	public ResponseEntity<Boolean> isClubNameAvailable(
			@Parameter(description = "확인할 클럽 이름", required = true) @RequestParam String name) {
		if (name == null || name.trim().isEmpty()) {
			return ResponseEntity.badRequest().body(null);
		}
		boolean available = clubService.findByName(name) == null;
		return ResponseEntity.ok(available);
	}

	// 팀 코드 중복 확인
	@Operation(summary = "팀 코드 중복 확인", description = "제공된 팀 코드가 사용 가능한지(중복되지 않는지) 확인합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "팀 코드 사용 가능 여부 반환", content = @Content(schema = @Schema(implementation = Boolean.class))),
			@ApiResponse(responseCode = "400", description = "팀 코드가 누락되었습니다.", content = @Content) })
	@GetMapping("/check-teamcode")
	public ResponseEntity<Boolean> isTeamCodeAvailable(
			@Parameter(description = "확인할 팀 코드", required = true) @RequestParam(required = false) String teamCode) {
		try {
			if (teamCode == null || teamCode.trim().isEmpty()) {
				throw new IllegalArgumentException("팀 코드는 필수 입력값입니다.");
			}
			boolean isAvailable = clubService.findByTeamCode(teamCode) == null;
			return ResponseEntity.ok(isAvailable);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// 유저 번호로 클럽 보유 여부 확인
	@Operation(summary = "사용자 번호로 클럽 보유 여부 확인", description = "특정 사용자가 클럽을 보유하고 있는지 확인합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 보유 여부 반환", content = @Content(schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "500", description = "클럽 보유 여부 확인 중 서버 오류 발생", content = @Content(schema = @Schema(implementation = Map.class))) })
	@GetMapping("/hasClub/{userNo}")
	public ResponseEntity<Map<String, Boolean>> hasClub(
			@Parameter(description = "클럽 보유 여부를 확인할 사용자 번호", required = true) @PathVariable int userNo) {
		Map<String, Boolean> result = new HashMap<>();
		try {
			boolean exists = clubService.hasClubByUserNo(userNo);
			result.put("result", exists);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			result.put("result", false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	// 여러 클럽 조회
	@Operation(summary = "사용자 번호로 클럽 목록 조회", description = "특정 사용자가 속한 모든 클럽 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 목록을 성공적으로 조회했습니다.", content = @Content(schema = @Schema(implementation = List.class))),
			@ApiResponse(responseCode = "500", description = "클럽 목록 조회 중 서버 오류 발생", content = @Content(schema = @Schema(implementation = String.class))) })
	@GetMapping("/myClubs/{userNo}")
	public ResponseEntity<?> getClubsByUser(
			@Parameter(description = "클럽 목록을 조회할 사용자 번호", required = true) @PathVariable int userNo) {
		try {
			List<Club> clubs = clubService.findClubsByUserNo(userNo);
			return ResponseEntity.ok(clubs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 목록 조회 실패: " + e.getMessage());
		}
	}

	// 단일 클럽 club_id만 반환
	@Operation(summary = "사용자 번호로 단일 클럽 ID 조회", description = "특정 사용자가 보유한 클럽의 ID를 조회합니다. 클럽이 없으면 null을 반환합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 ID를 성공적으로 조회했습니다.", content = @Content(schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "500", description = "클럽 ID 조회 중 서버 오류 발생", content = @Content(schema = @Schema(implementation = Map.class))) })
	@GetMapping("/getSingleClub/{userNo}")
	public ResponseEntity<Map<String, Object>> getSingleClub(
			@Parameter(description = "클럽 ID를 조회할 사용자 번호", required = true) @PathVariable int userNo) {
		Map<String, Object> result = new HashMap<>();
		try {
			Club club = clubService.findClubByUserNo(userNo);
			result.put("club_id", club != null ? club.getClub_id() : null);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			result.put("club_id", null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	// 클럽 목록 조회
	@Operation(summary = "클럽 목록 조회", description = "페이지네이션, 검색, 정렬 옵션을 사용하여 클럽 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 목록을 성공적으로 조회했습니다.", content = @Content(schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "500", description = "클럽 목록 조회 중 서버 오류 발생", content = @Content(schema = @Schema(implementation = String.class))) })
	@GetMapping("/list")
	public ResponseEntity<?> listClubs(
			@Parameter(description = "페이지 번호 (기본값: 1)", example = "1") @RequestParam(defaultValue = "1") int page,
			@Parameter(description = "페이지당 항목 수 (기본값: 10)", example = "10") @RequestParam(defaultValue = "100") int size,
			@Parameter(description = "검색 키워드 (선택 사항)", example = "축구") @RequestParam(required = false) String searchKeyword,
			@Parameter(description = "정렬 기준 컬럼 (기본값: ranking)", example = "club_name") @RequestParam(defaultValue = "ranking") String sortColumn,
			@Parameter(description = "정렬 방향 (ASC 또는 DESC, 기본값: ASC)", example = "DESC") @RequestParam(defaultValue = "ASC") String sortDirection) {
		try {
			int startRow = (page - 1) * size;
			Map<String, Object> params = new HashMap<>();
			params.put("searchKeyword", searchKeyword);
			params.put("startRow", startRow);
			params.put("pageSize", size);
			params.put("sortColumn", sortColumn);
			params.put("sortDirection", sortDirection);

			List<Club> clubList = clubService.list(params);
			int totalCount = clubService.getTotalCount(params);

			Map<String, Object> result = new HashMap<>();
			result.put("data", clubList);
			result.put("total", totalCount);
			result.put("page", page);
			result.put("size", size);

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 목록 조회 실패: " + e.getMessage());
		}
	}

	// 클럽 정보 수정
	@Operation(summary = "클럽 정보 수정", description = "주어진 클럽 ID에 해당하는 클럽의 정보를 수정합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 정보가 성공적으로 수정되었습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "클럽 정보 수정에 실패했습니다.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "500", description = "클럽 정보 수정 중 서버 오류 발생", content = @Content(schema = @Schema(implementation = String.class))) })
	@PutMapping("/{club_id}")
	public ResponseEntity<String> updateClub(
			@Parameter(description = "수정할 클럽의 ID", required = true) @PathVariable int club_id,
			// OpenAPI의 @RequestBody는 전체 경로를 명시합니다.
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "업데이트할 클럽 정보", required = true) @RequestBody Club club) { // Spring
																																			// Framework의
																																			// @RequestBody
		try {
			club.setClub_id(club_id);
			int result = clubService.updateClub(club);
			if (result > 0) {
				return ResponseEntity.ok("클럽 정보가 성공적으로 수정되었습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("클럽 정보 수정에 실패했습니다.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
		}
	}

	// teamCode로 club_id만 반환
	@Operation(summary = "팀 코드로 클럽 ID 조회", description = "주어진 팀 코드를 사용하여 해당 클럽의 ID만 반환합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "클럽 ID를 성공적으로 조회했습니다.", content = @Content(schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "400", description = "팀 코드가 누락되었습니다.", content = @Content),
			@ApiResponse(responseCode = "404", description = "해당 팀 코드로 클럽을 찾을 수 없습니다.", content = @Content),
			@ApiResponse(responseCode = "500", description = "클럽 ID 조회 중 서버 오류 발생", content = @Content) })
	@GetMapping("/idByTeamCode")
	public ResponseEntity<Map<String, Integer>> getClubIdByTeamCode(
			@Parameter(description = "클럽 ID를 조회할 팀 코드", required = true) @RequestParam("teamCode") String teamCode) {
		Map<String, Integer> result = new HashMap<>();
		try {
			if (teamCode == null || teamCode.trim().isEmpty()) {
				return ResponseEntity.badRequest().body(null);
			}
			Club club = clubService.findByTeamCode(teamCode);
			if (club != null) {
				result.put("club_id", club.getClub_id());
				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	
	// 로고 업로드
	@PostMapping("/{club_id}/uploadLogo")
	public ResponseEntity<String> uploadLogoBase64(
			@Parameter(description = "클럽 ID", required = true) @PathVariable int club_id,
			@RequestBody com.msa.kyj_prj.club.dto.Base64ImageDTO dto) {
		try {
			String logoPath = dto.getBase64();

			clubService.updateLogoPath(club_id, logoPath);

			return ResponseEntity.ok(logoPath);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패: " + e.getMessage());
		}
	}
}