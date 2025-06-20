package com.msa.kyj_prj.board;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.msa.kyj_prj.member.Member;
import com.msa.kyj_prj.util.Util;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/board")
public class BoardController {
	@Autowired
	BoardService boardService;

	// 게시판 등록하기
	@PostMapping("regist")
	public ResponseEntity<Object> regist(@RequestBody Board board) {
		Map<String, Object> result = new HashMap<String, Object>();
		boardService.registForm(board);

		result.put("error", false);
		result.put("message", "게시물 등록을 완료했습니다.");
		return ResponseEntity.ok(result);
	}

	// 게시물 삭제
	@PostMapping("delete")
	public Map<String, Object> delete(@ModelAttribute Board board) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		Board boardDB = boardService.getBoard(board.getBno());
		
		// 만약 회원일 경우 DB에 비밀번호가 누락되어 있기에 구분짓기위해 비교
		String dbPasswd = boardDB.getPasswd();
	    String inputPasswd = board.getPasswd();
		
		if (!Objects.equals(dbPasswd, inputPasswd)) {
			result.put("error", true);
			result.put("message", "게시물 비밀번호가 다름니다.");
			return result;
		}

		boardService.delete(board.getBno());
		result.put("error", false);
		result.put("message", "게시물 삭제 완료했습니다.");

		return result;
	}

	// 게시판 목록 진입
	@GetMapping("list")
	public Map<String, Object> list(String pageNo, String size, String searchValue,  @RequestHeader(value = "supervisor", defaultValue = "N") String supervisor) {
		
		Map<String, Object> result = new HashMap<>();
		//로그인 되어있고, 관리자 일 경우
		if(supervisor.equals("Y")) {
			result.put("pageResponse",boardService.list_admin(supervisor,
					searchValue,
					Util.parseInt(pageNo, 1),
					Util.parseInt(size, 10)
					));
			return result;
		}
		
	
	result.put("pageResponse",boardService.list(supervisor,
			searchValue,
			Util.parseInt(pageNo, 1),
			Util.parseInt(size, 10)
			));
		
		return result;
	}

	// 게시판 상세 디테일
	@RequestMapping("detailView")
	public Map<String, Object> detailView(@RequestParam int bno) {
		Map<String, Object> result = new HashMap<>();
		boardService.increseView(bno);
		Board boardDB = boardService.getBoard(bno);
		if (boardDB == null) {
			return result;
		}
		result.put("boardDB", boardDB);
		return result;
	}

	// 게시판 수정하기폼
	@RequestMapping("updateForm")
	public  Map<String, Object>  updateForm(@RequestParam int bno) {
		Map<String, Object> result = new HashMap<>();
		Board boardDB = boardService.getBoard(bno);
		if (boardDB == null) {
			return result;
		}
		result.put("boardDB", boardDB);

		return result;
	}

	// 게시판 수정
	@PostMapping("update")
	public Map<String, Object> update(@ModelAttribute Board board) {
		Map<String, Object> result = new HashMap<String, Object>();
		Board boardDB = boardService.getBoard(board.getBno());

		// 만약 회원일 경우 DB에 비밀번호가 누락되어 있기에 구분짓기위해 비교
		String dbPasswd = boardDB.getPasswd();
	    String inputPasswd = board.getPasswd();
		
		if (!Objects.equals(dbPasswd, inputPasswd)) {
			result.put("error", true);
			result.put("message", "게시물 비밀번호가 다름니다.");
			return result;
		}

		boardService.update(board);
		result.put("error", false);
		result.put("message", "게시물 수정 완료했습니다.");

		return result;
	}
}
