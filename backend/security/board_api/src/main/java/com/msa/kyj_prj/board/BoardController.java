package com.msa.kyj_prj.board;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    // 게시글 리스트 및 검색
    @GetMapping("/list")
    public ResponseEntity<?> getBoardList(@RequestParam Map<String, String> params) {
        try {
            return ResponseEntity.ok(boardService.findBoards(params));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 목록 조회 실패: " + e.getMessage());
        }
    }

    // 게시글 상세 조회
    @GetMapping("/{board_id}")
    public ResponseEntity<?> getBoard(@PathVariable Long board_id) {
        try {
            Board board = boardService.findBoardByBno(board_id);
            if (board == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 존재하지 않습니다.");
            return ResponseEntity.ok(board);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 조회 실패: " + e.getMessage());
        }
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody Board board) {
        try {
            boardService.insertBoard(board);
            return ResponseEntity.ok(Map.of("result", "created", "board_id", board.getBoard_id()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 작성 실패: " + e.getMessage());
        }
    }

    // 게시글 수정
    @PutMapping("/{board_id}")
    public ResponseEntity<?> updateBoard(@PathVariable Long board_id, @RequestBody Board board) {
        try {
            board.setBoard_id(board_id);
            boardService.updateBoard(board);
            return ResponseEntity.ok(Map.of("result", "updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정 실패: " + e.getMessage());
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{board_id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long board_id) {
        try {
            boardService.deleteBoard(board_id);
            return ResponseEntity.ok(Map.of("result", "deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 실패: " + e.getMessage());
        }
    }

    // 게시글 조회수 증가
    @PostMapping("/{board_id}/increaseViewcount")
    public ResponseEntity<?> increaseViewCount(@PathVariable Long board_id) {
        try {
            boardService.increaseViewCount(board_id);
            return ResponseEntity.ok(Map.of("result", "increased"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("조회수 증가 실패: " + e.getMessage());
        }
    }

    // 게시글 좋아요
    @PostMapping("/like")
    public ResponseEntity<?> likeBoard(@RequestParam("board_id") Long board_id,
                                       @RequestParam("user_no") int user_no) {
        try {
            boardService.insertBoardLike(board_id, user_no);
            return ResponseEntity.ok(Map.of("result", "liked"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("좋아요 실패: " + e.getMessage());
        }
    }

    // 게시글 좋아요 취소
    @DeleteMapping("/like")
    public ResponseEntity<?> unlikeBoard(@RequestParam("board_id") Long board_id,
                                         @RequestParam("user_no") int user_no) {
        try {
            boardService.deleteBoardLike(board_id, user_no);
            return ResponseEntity.ok(Map.of("result", "unliked"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("좋아요 취소 실패: " + e.getMessage());
        }
    }

    // 게시글 좋아요 개수
    @GetMapping("/like/count")
    public ResponseEntity<?> countBoardLike(@RequestParam("board_id") Long board_id) {
        try {
            int count = boardService.countBoardLike(board_id);
            return ResponseEntity.ok(Map.of("result", "success", "likeCount", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("좋아요 개수 조회 실패: " + e.getMessage());
        }
    }

    // 게시글 좋아요 여부 체크
    @PostMapping("/like/check")
    public ResponseEntity<?> checkUserLiked(@RequestBody Map<String, Object> data) {
        try {
            Long board_id = Long.valueOf(data.get("board_id").toString());
            int user_no = Integer.parseInt(data.get("user_no").toString());
            boolean liked = boardService.hasUserLikedBoard(board_id, user_no);
            return ResponseEntity.ok(Map.of("result", "success", "liked", liked));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("좋아요 여부 확인 실패: " + e.getMessage());
        }
    }

    // 모집게시판 게시글 목록
    @GetMapping("/recruitlist")
    public ResponseEntity<?> getRecruitBoards(
        @RequestParam(required = false) Integer userNo,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        try {
            List<Board> result = boardService.findRecruitBoards(userNo, keyword, sortDirection);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("모집게시판 목록 조회 실패: " + e.getMessage());
        }
    }
}

