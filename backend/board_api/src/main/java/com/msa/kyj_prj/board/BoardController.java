package com.msa.kyj_prj.board;

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
@RequestMapping("/api/board")
public class BoardController {
	
	@Autowired
    private BoardService boardService;
	
	// 게시글 리스트 및 검색
	@GetMapping("/list")
	public List<Board> getBoardList(@RequestParam Map<String, String> params) {
	    return boardService.findBoards(params);
	}

    // 게시글 상세 조회
    @GetMapping("/{board_id}")
    public Board getBoard(@PathVariable Long board_id) {
        return boardService.findBoardByBno(board_id);
    }
    
    // 게시글 작성
    @PostMapping
    public Map<String, Object> createBoard(@RequestBody Board board) {
        boardService.insertBoard(board);
        return Map.of("result", "created", "board_id", board.getBoard_id());
    }
    
    // 게시글 수정
    @PutMapping("/{board_id}")
    public Map<String, Object> updateBoard(@PathVariable Long board_id, @RequestBody Board board) {
        // TODO: 로그인 사용자와 작성자 일치 여부 검사 필요
        board.setBoard_id(board_id);
        boardService.updateBoard(board);
        return Map.of("result", "updated");
    }

    // 게시글 삭제
    @DeleteMapping("/{board_id}")
    public Map<String, Object> deleteBoard(@PathVariable Long board_id) {
        // TODO: 작성자 본인 확인 필요
        boardService.deleteBoard(board_id);
        return Map.of("result", "deleted");
    }
    
    // 게시글 조회수 증가
    @PostMapping("/{board_id}/increaseViewcount")
    public Map<String, Object> increaseViewCount(@PathVariable Long board_id) {
        boardService.increaseViewCount(board_id);
        return Map.of("result", "increased");
    }
}
