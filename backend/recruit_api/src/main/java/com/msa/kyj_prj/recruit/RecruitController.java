package com.msa.kyj_prj.recruit;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
            @RequestParam(required = false) String keyword
    ) {
        List<RecruitBoard> list;
        if ("popular".equalsIgnoreCase(sort)) {
            list = recruit_service.get_recruits_order_by_view_count(keyword);
        } else {
            list = recruit_service.get_all_recruits(keyword);
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
    public ResponseEntity<?> get_recruit(@PathVariable int bno) {
        recruit_service.increase_view_count(bno);
        RecruitBoard board = recruit_service.get_recruit(bno);
        if (board == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 모집글이 존재하지 않습니다.");
        }
        return ResponseEntity.ok(board);
    }

    // 모집글 등록
    @PostMapping("/regist")
    public ResponseEntity<?> create_recruit(@RequestBody RecruitBoard board) {
        if (board == null
                || board.getUser_no() == 0
                || board.getClub_id() == 0
                || board.getTitle() == null
                || board.getTitle().trim().isEmpty()
                || board.getContent() == null
                || board.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 필수 정보가 누락되었습니다.");
        }
        recruit_service.create_recruit(board);
        return ResponseEntity.ok("등록 완료");
    }

    // 모집글 수정 - 작성자만 가능
    @PutMapping("/{bno}")
    public ResponseEntity<String> update_recruit(
            @PathVariable int bno,
            @RequestBody RecruitBoard board,
            @RequestParam("user_no") int user_no) {

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
    public ResponseEntity<String> close_recruit(
            @PathVariable int bno,
            @RequestParam("user_no") int user_no) {

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
    public ResponseEntity<String> delete_recruit(
            @PathVariable int bno,
            @RequestParam("user_no") int user_no) {

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
