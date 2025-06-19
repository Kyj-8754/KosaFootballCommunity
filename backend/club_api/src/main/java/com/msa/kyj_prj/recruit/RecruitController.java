package com.msa.kyj_prj.recruit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruits")
@RequiredArgsConstructor
public class RecruitController {

    private final RecruitService recruitService;

    // ✅ 전체 모집글 목록 조회 (+ 인기순 정렬 지원) // 나중에 성별, 요일, 시간대별 정렬을 추가 할 예정
    @GetMapping
    public ResponseEntity<List<RecruitBoard>> getAllRecruits(@RequestParam(required = false) String sort) {
        List<RecruitBoard> list;

        // 인기순 정렬 (조회수 기준)
        if ("popular".equalsIgnoreCase(sort)) {
            list = recruitService.getRecruitsOrderByViews();
        } else {
            list = recruitService.getAllRecruits(); // 기본 최신순
        }

        return ResponseEntity.ok(list);
    }

    // ✅ 클럽별 모집글 목록 조회
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<RecruitBoard>> getRecruitsByClub(@PathVariable int clubId) {
        List<RecruitBoard> list = recruitService.getRecruitsByClub(clubId);
        return ResponseEntity.ok(list);
    }

    // ✅ 모집글 단건 조회
    @GetMapping("/{bno}")
    public ResponseEntity<RecruitBoard> getRecruit(@PathVariable int bno) {
        RecruitBoard board = recruitService.getRecruit(bno);
        return ResponseEntity.ok(board);
    }

    // ✅ 모집글 등록 (팀장만 가능)
    @PostMapping
    public ResponseEntity<?> createRecruit(@RequestBody RecruitBoard board, @RequestParam String userId) {
        if (!recruitService.isClubLeader(userId, board.getClubId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("팀장만 글 작성이 가능합니다.");
        }
        recruitService.createRecruit(board);
        return ResponseEntity.ok("등록 완료");
    }

    // ✅ 모집글 수정 (팀장만 가능)
    @PutMapping("/{bno}")
    public ResponseEntity<?> updateRecruit(@PathVariable int bno,
                                           @RequestBody RecruitBoard board,
                                           @RequestParam String userId) {
        if (!recruitService.isClubLeader(userId, board.getClubId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("팀장만 글 수정이 가능합니다.");
        }
        board.setBno(bno); // path값을 DTO에 주입
        recruitService.updateRecruit(board);
        return ResponseEntity.ok("수정 완료");
    }

    // ✅ 모집글 삭제 (팀장만 가능)
    @DeleteMapping("/{bno}")
    public ResponseEntity<?> deleteRecruit(@PathVariable int bno,
                                           @RequestParam int clubId,
                                           @RequestParam String userId) {
        if (!recruitService.isClubLeader(userId, clubId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("팀장만 삭제할 수 있습니다.");
        }
        recruitService.deleteRecruit(bno);
        return ResponseEntity.ok("삭제 완료");
    }
}
