package com.msa.kyj_prj.club;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/club") // ✅ club_api 프록시와 매핑되도록 루트 수정
public class ClubController {

	@Autowired
	private ClubService clubService;

	// ✅ 클럽 생성 - JWT 없이 단순 등록
	@PostMapping("")
	public void createClub(@RequestBody Club club) {
		clubService.insert(club); // ✅ create → insert 로 변경
	}

	// ✅ [단건 조회] 팀 코드로 클럽 조회 - /club/code/{teamCode}
	@GetMapping("/code/{teamCode}")
	public Club getClubByTeamCode(@PathVariable String teamCode) {
		return clubService.findByTeamCode(teamCode);
	}

	// ✅ [중복 체크] 클럽 이름 중복 여부 확인 - /club/check-name?name=...
	@GetMapping("/check-name")
	public boolean isClubNameAvailable(@RequestParam String name) {
		return clubService.findByName(name) == null;
	}

	// ✅ 팀 코드 중복 확인 API - 예외 처리 포함
	@GetMapping("/check-teamcode")
	public ResponseEntity<Boolean> isTeamCodeAvailable(@RequestParam(required = false) String teamCode) {
		try {
			// 빈 문자열 또는 null이면 잘못된 요청 처리
			if (teamCode == null || teamCode.trim().isEmpty()) {
				throw new IllegalArgumentException("팀 코드는 필수 입력값입니다.");
			}

			boolean isAvailable = clubService.findByTeamCode(teamCode) == null;
			return ResponseEntity.ok(isAvailable); // 200 OK + true/false

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null); // 400 Bad Request
		} catch (Exception e) {
			// 예기치 못한 서버 오류 처리 (500)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// ✅ 유저 번호(userNo)로 클럽 보유 여부 확인
	@GetMapping("/hasClub/{userNo}")
	public ResponseEntity<Map<String, Boolean>> hasClub(@PathVariable int userNo) {
		boolean exists = clubService.hasClubByUserNo(userNo); // 서비스 로직 호출
		Map<String, Boolean> result = new HashMap<>();
		result.put("result", exists); // JSON: { "result": true }
		return ResponseEntity.ok(result);
	}

	// 게시글 등록 화면에서 여러 클럽 조회
	@GetMapping("/myClubs/{userNo}")
	public ResponseEntity<List<Club>> getClubsByUser(@PathVariable int userNo) {
		List<Club> clubs = clubService.findClubsByUserNo(userNo);
		return ResponseEntity.ok(clubs);
	}

	@GetMapping("/getSingleClub/{userNo}")
	public ResponseEntity<Map<String, Object>> getSingleClub(@PathVariable int userNo) {
		Club club = clubService.findClubByUserNo(userNo); // LIMIT 1
		Map<String, Object> result = new HashMap<>();

		// ✅ 필드명이 club_id이므로 getClub_id() 메서드 사용
		result.put("club_id", club != null ? club.getClub_id() : null);

		return ResponseEntity.ok(result);
	}

	// ✅ [클럽 목록 조회] - /club/list?page=1&size=10 ...
	// 🔧 이 메서드는 clubs_api 에 대응되므로 URL 수정하거나 컨트롤러를 분리해도 됨
	@GetMapping("/list")
	public Map<String, Object> listClubs(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String searchKeyword,
			@RequestParam(defaultValue = "ranking") String sortColumn,
			@RequestParam(defaultValue = "ASC") String sortDirection) {
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

		return result;
	}

	// ✅ 클럽 정보 수정 - club_id 기준으로 클럽 정보 전체(또는 일부) 수정
	@PutMapping("/{club_id}")
	public ResponseEntity<String> updateClub(@PathVariable int club_id, @RequestBody Club club) {
		club.setClub_id(club_id); // PathVariable에서 받은 club_id를 club 객체에 세팅

		int result = clubService.updateClub(club); // 서비스 레이어 호출

		if (result > 0) {
			// 성공 시 메시지 반환
			return ResponseEntity.ok("클럽 정보가 성공적으로 수정되었습니다.");
		} else {
			// 실패 시 메시지 반환
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("클럽 정보 수정에 실패했습니다.");
		}
	}

	// ✅ teamCode로 club_id만 반환하는 엔드포인트 (프론트 연동용)
	@GetMapping("/idByTeamCode")
	public ResponseEntity<Map<String, Integer>> getClubIdByTeamCode(@RequestParam("teamCode") String teamCode) {
		Club club = clubService.findByTeamCode(teamCode);
		Map<String, Integer> result = new HashMap<>();
		if (club != null) {
			result.put("club_id", club.getClub_id());
			return ResponseEntity.ok(result); // { "club_id": 24 }
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// ✅ 클럽 로고 이미지 업로드 엔드포인트
	@PostMapping("/{club_id}/uploadLogo")
	public ResponseEntity<String> uploadLogo(@PathVariable int club_id, @RequestParam("file") MultipartFile file) {
	    try {
	        // 1. 파일 저장 (프로젝트 내 uploads/club_logos 폴더 경로로 지정)
	        String uploadDir = "C:/workspace-sts4/MsaTeamProject/backend/club_api/uploads/club_logos/";
	        // 혹시 상대경로 쓸거면: "./uploads/club_logos/"
	        
	        // 폴더 없으면 자동 생성 (안전)
	        File dir = new File(uploadDir);
	        if (!dir.exists()) dir.mkdirs();

	        // 파일명 중복 방지 (club_id_타임스탬프_원본파일명)
	        String filename = club_id + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
	        File dest = new File(uploadDir + filename);
	        file.transferTo(dest);

	        // 2. 프론트에서 접근 가능한 경로로 저장 (WebConfig 설정과 연동!)
	        String logoPath = "/uploads/club_logos/" + filename;
	        clubService.updateLogoPath(club_id, logoPath);

	        // 3. 성공시 경로 반환
	        return ResponseEntity.ok(logoPath);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패: " + e.getMessage());
	    }
	}


}
