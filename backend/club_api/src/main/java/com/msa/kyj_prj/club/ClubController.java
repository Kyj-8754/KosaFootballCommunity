package com.msa.kyj_prj.club;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clubs") // 클럽 관련 REST API의 기본 URL
public class ClubController {

	@Autowired
	private ClubService clubService;

	// 클럽 단건 조회
	@GetMapping("/{clubId}")
	public Club getClub(@PathVariable Integer clubId) {
		return clubService.getClub(clubId);
	}

	// 클럽 등록
	@PostMapping
	public int insertClub(@RequestBody Club club) {
		return clubService.insert(club);
	}

	// 클럽 수정
	@PutMapping("/{clubId}")
	public int updateClub(@PathVariable int clubId, @RequestBody Club club) {
		club.setClubId(clubId); // 경로 파라미터로 받은 ID를 객체에 세팅
		return clubService.update(club);
	}

	// 클럽 삭제
	@DeleteMapping("/{clubId}")
	public int deleteClub(@PathVariable int clubId) {
		return clubService.delete(clubId);
	}

	// 클럽 리스트 조회 (검색 + 페이징 + 정렬)
	@GetMapping
	public Map<String, Object> listClubs(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String searchKeyword,
			@RequestParam(defaultValue = "ranking") String sortColumn,
			@RequestParam(defaultValue = "ASC") String sortDirection) {
		// 페이징 계산
		int startRow = (page - 1) * size;

		// 파라미터 구성
		Map<String, Object> params = new HashMap<>();
		params.put("searchKeyword", searchKeyword);
		params.put("startRow", startRow);
		params.put("pageSize", size);
		params.put("sortColumn", sortColumn);
		params.put("sortDirection", sortDirection);

		// 데이터 조회
		List<Club> clubList = clubService.list(params);
		int totalCount = clubService.getTotalCount(params);

		// 결과 리턴
		Map<String, Object> result = new HashMap<>();
		result.put("data", clubList);
		result.put("total", totalCount);
		result.put("page", page);
		result.put("size", size);

		return result;
	}
}
