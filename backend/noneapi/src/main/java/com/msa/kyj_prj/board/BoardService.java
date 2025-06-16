package com.msa.kyj_prj.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.msa.kyj_prj.page.PageResponseVO_board;

@Service
public class BoardService{
	// 인터페이스 자동 상속
	@Autowired
	private BoardDAO boardDAO;

	
	// 보드 디테일 가져오기
	public Board getBoard(int bno) {
		
		return boardDAO.getBoard(bno);
	}
	
	// 조회수 1 증가하기
	@Transactional
	public int increseView(int bno) {
		return boardDAO.increseView(bno);
	};
	
	// 보드 등록하기
	@Transactional
	public int registForm(Board board) {
		return boardDAO.regist(board);
	}
	
	// 보드 삭제 명령문
	@Transactional
	public int delete(int bno) {
		return boardDAO.delete(bno);
	}
	
	// 보드 수정 명령문
	@Transactional
	public void update(Board board) {
		boardDAO.update(board);
	}

		
	public PageResponseVO_board list(String supervisor, String searchValue, int pageNumber, int size) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start", (pageNumber-1)*size+1);
		map.put("end", pageNumber * size);
		map.put("searchValue", searchValue);
		return new PageResponseVO_board(
				boardDAO.list(map),
				boardDAO.getTotalCount(map),
				pageNumber,
				size,
				searchValue);
	}

	public PageResponseVO_board list_admin(String supervisor, String searchValue, int pageNumber, int size) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("supervisor", supervisor);
		map.put("start", (pageNumber-1)*size+1);
		map.put("end", pageNumber * size);
		map.put("searchValue", searchValue);
		return new PageResponseVO_board(
				boardDAO.list_admin(map),
				boardDAO.getTotalCount_admin(map),
				pageNumber,
				size,
				searchValue);
	}
	
}
