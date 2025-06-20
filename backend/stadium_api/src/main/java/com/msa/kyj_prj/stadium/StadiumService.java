package com.msa.kyj_prj.stadium;

import java.util.HashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.msa.kyj_prj.page.PageResponseVO_board;

@Service
public class StadiumService{
	// 인터페이스 자동 상속
	@Autowired
	private StadiumDAO stadiumDAO;

	
	// 보드 디테일 가져오기
	public Stadium getStadium(String SVCID) {
		
		return stadiumDAO.getStadium(SVCID);
	}
	
	// 조회수 1 증가하기
//	@Transactional
//	public int increseView(int bno) {
//		return boardDAO.increseView(bno);
//	};
	
	// 보드 등록하기
//	@Transactional
//	public int registForm(Board board) {
//		return boardDAO.regist(board);
//	}
	
	// 보드 삭제 명령문
//	@Transactional
//	public int delete(int bno) {
//		return boardDAO.delete(bno);
//	}
	
	// 보드 수정 명령문
//	@Transactional
//	public void update(Board board) {
//		boardDAO.update(board);
//	}

		
	public PageResponseVO_board list(String searchType, String searchValue, int pageNumber) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start", (pageNumber-1)*9);
		map.put("end", 9);
		map.put("searchValue", searchValue);
		map.put("searchType", searchType);
		return new PageResponseVO_board(
				stadiumDAO.list(map),
				stadiumDAO.getTotalCount(map),
				pageNumber,
				searchType,
				searchValue);
	}
	
}


