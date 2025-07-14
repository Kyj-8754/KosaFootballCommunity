package com.msa.kyj_prj.recruit;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recruits")
@RequiredArgsConstructor
public class RecruitController {

	private final RecruitService recruit_service;

	// 전체 모집글 목록 조회 (+ 인기순 정렬 지원)
	@GetMapping
	public ResponseEntity<List<RecruitBoard>> get_all_recruits(
	    @RequestParam(required = false) String sort,
	    @RequestParam(required = false) String keyword    // <-- 이 부분을 추가!
	) {
	    List<RecruitBoard> list;
	    if ("popular".equalsIgnoreCase(sort)) {
	        list = recruit_service.get_recruits_order_by_view_count(keyword); // <-- 여기서도 keyword 넘겨줌
	    } else {
	        list = recruit_service.get_all_recruits(keyword); // <-- 여기서도 keyword 넘겨줌
	    }
	    return ResponseEntity.ok(list);
	}


	// 클럽별 모집글 목록 조회
	@GetMapping("/club/{club_id}")
	public ResponseEntity<List<RecruitBoard>> get_recruits_by_club(@PathVariable int club_id) {
		List<RecruitBoard> list = recruit_service.get_recruits_by_club(club_id);
		return ResponseEntity.ok(list);
	}

	// 모집글 단건 조회 + 조회수 증가
	@GetMapping("/{bno}")
	public ResponseEntity<RecruitBoard> get_recruit(@PathVariable int bno) {
		recruit_service.increase_view_count(bno); // 조회수 증가
		RecruitBoard board = recruit_service.get_recruit(bno);
		return ResponseEntity.ok(board);
	}
	
	// 모집글 등록
	@PostMapping("/regist")
	public ResponseEntity<?> create_recruit(@RequestBody RecruitBoard board) {
	    // 전제: 프론트에서 writer, user_no, club_id 모두 보내줌
	    recruit_service.create_recruit(board);
	    return ResponseEntity.ok("등록 완료");
	}

	
    // 모집글 수정 - 작성자만 가능
    @PutMapping("/{bno}")
    public ResponseEntity<String> update_recruit(@PathVariable int bno,
                                                 @RequestBody RecruitBoard board,
                                                 @RequestParam String user_id) {
        RecruitBoard original = recruit_service.get_recruit(bno);
        board.setBno(bno);
        recruit_service.update_recruit(board);
        return ResponseEntity.ok("수정 완료");
    }
}
