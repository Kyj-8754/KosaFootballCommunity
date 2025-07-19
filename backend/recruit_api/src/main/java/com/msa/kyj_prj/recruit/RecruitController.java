package com.msa.kyj_prj.recruit;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import com.msa.kyj_prj.recruit.dto.PageRequestDTO;
import com.msa.kyj_prj.recruit.dto.PageResponseDTO;

@RestController
@RequestMapping("/recruits")
@RequiredArgsConstructor
public class RecruitController {

	private final RecruitService recruit_service;

	// 전체 모집글 목록 조회 (+ 인기순 정렬 지원)
	@GetMapping
	@Operation(summary = "모집글 페이징 목록 조회", description = "페이징 + 정렬 + 키워드 검색을 지원합니다.")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "조회 성공"),
	    @ApiResponse(responseCode = "500", description = "서버 오류")
	})
	public ResponseEntity<PageResponseDTO<RecruitBoard>> getPagedRecruits(
	    @RequestParam(defaultValue = "1") int page,
	    @RequestParam(defaultValue = "10") int size,
	    @RequestParam(required = false) String sort,
	    @RequestParam(required = false) String keyword
	) {
	    try {
	        // ✅ PageRequestDTO 구성
	        PageRequestDTO request = new PageRequestDTO();
	        request.setPage(page);
	        request.setSize(size);
	        request.setKeyword((keyword != null && !keyword.trim().isEmpty()) ? keyword : null); // ✅

	        // ✅ 페이징 결과 가져오기
	        PageResponseDTO<RecruitBoard> response = recruit_service.get_paginated_recruits(request, sort);

	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}


	// 모집글 단건 조회 + 조회수 증가
	@GetMapping("/{bno}")
	@Operation(summary = "모집글 상세 조회", description = "bno(모집글 번호)로 모집글 상세 정보를 조회합니다. 조회수도 1 증가합니다.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공"),
			@ApiResponse(responseCode = "404", description = "모집글이 존재하지 않음") })
	public ResponseEntity<?> get_recruit(@Parameter(description = "모집글 번호", example = "16") @PathVariable int bno) {
		recruit_service.increase_view_count(bno);
		RecruitBoard board = recruit_service.get_recruit(bno);
		if (board == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 모집글이 존재하지 않습니다.");
		}
		return ResponseEntity.ok(board);
	}

	// 모집글 등록
	@PostMapping("/regist")
	@Operation(summary = "모집글 등록", description = "모집글을 등록합니다. 필수 정보: user_no, club_id, title, content")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "등록 성공"),
			@ApiResponse(responseCode = "400", description = "필수 정보 누락") })
	public ResponseEntity<?> create_recruit(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "모집글 정보(RecruitBoard)") @RequestBody RecruitBoard board) {
		if (board == null || board.getUser_no() == 0 || board.getClub_id() == 0 || board.getTitle() == null
				|| board.getTitle().trim().isEmpty() || board.getContent() == null
				|| board.getContent().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("❌ 필수 정보가 누락되었습니다.");
		}
		recruit_service.create_recruit(board);
		return ResponseEntity.ok("등록 완료");
	}

	// 모집글 수정 - 작성자만 가능
	@PutMapping("/{bno}")
	@Operation(summary = "모집글 수정", description = "bno(모집글 번호)에 해당하는 모집글을 수정합니다. 작성자(user_no)만 수정 가능.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "수정 완료"),
			@ApiResponse(responseCode = "403", description = "작성자만 수정 가능"),
			@ApiResponse(responseCode = "404", description = "모집글이 존재하지 않음") })
	public ResponseEntity<String> update_recruit(
			@Parameter(description = "모집글 번호", example = "2") @PathVariable int bno,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 모집글 정보") @RequestBody RecruitBoard board,
			@Parameter(description = "작성자 유저 번호", example = "3") @RequestParam("user_no") int user_no) {

		RecruitBoard original = recruit_service.get_recruit(bno);
		if (original == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 모집글이 존재하지 않습니다.");
		}
		if (original.getUser_no() != user_no) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ 작성자만 수정할 수 있습니다.");
		}
		board.setBno(bno);
		recruit_service.update_recruit(board);
		return ResponseEntity.ok("수정 완료");
	}

	// 모집글 마감 - 작성자(user_no)만 가능
	@PutMapping("/{bno}/close")
	@Operation(summary = "모집글 마감", description = "bno(모집글 번호)에 해당하는 모집글을 마감합니다. 작성자(user_no)만 가능.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "모집 마감 성공"),
			@ApiResponse(responseCode = "400", description = "이미 마감된 모집글"),
			@ApiResponse(responseCode = "403", description = "작성자만 마감 가능"),
			@ApiResponse(responseCode = "404", description = "모집글이 존재하지 않음") })
	public ResponseEntity<String> close_recruit(@Parameter(description = "모집글 번호", example = "1") @PathVariable int bno,
			@Parameter(description = "작성자 유저 번호", example = "2") @RequestParam("user_no") int user_no) {

		RecruitBoard original = recruit_service.get_recruit(bno);
		if (original == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 모집글이 존재하지 않습니다.");
		}
		if (original.getUser_no() != user_no) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ 작성자만 모집글을 마감할 수 있습니다.");
		}
		if (original.getIs_closed() == 1) {
			return ResponseEntity.badRequest().body("이미 마감된 모집글입니다.");
		}

		recruit_service.close_recruit(bno);
		return ResponseEntity.ok("✅ 모집이 마감되었습니다.");
	}

	// 모집글 삭제 - 작성자(user_no)만 가능
	@DeleteMapping("/{bno}")
	@Operation(summary = "모집글 삭제", description = "bno(모집글 번호)에 해당하는 모집글을 삭제합니다. 작성자(user_no)만 삭제 가능.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "삭제 성공"),
			@ApiResponse(responseCode = "403", description = "작성자만 삭제 가능"),
			@ApiResponse(responseCode = "404", description = "모집글이 존재하지 않음") })
	public ResponseEntity<String> delete_recruit(
			@Parameter(description = "모집글 번호", example = "1") @PathVariable int bno,
			@Parameter(description = "작성자 유저 번호", example = "2") @RequestParam("user_no") int user_no) {

		RecruitBoard original = recruit_service.get_recruit(bno);
		if (original == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 모집글이 존재하지 않습니다.");
		}
		if (original.getUser_no() != user_no) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ 작성자만 모집글을 삭제할 수 있습니다.");
		}
		recruit_service.delete_recruit(bno);
		return ResponseEntity.ok("✅ 모집글이 삭제되었습니다.");
	}
}
