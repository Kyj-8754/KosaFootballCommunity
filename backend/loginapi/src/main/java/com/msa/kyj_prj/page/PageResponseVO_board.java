package com.msa.kyj_prj.page;


import java.util.List;

import com.msa.kyj_prj.board.Board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//VO : Value Object
@Data @AllArgsConstructor @NoArgsConstructor
public class PageResponseVO_board {
	// 목록
	private List<Board> list;
	// 전체 글 갯수
	private int totalCount = 0;
	// 전체 페이지수
	private int totalPage = 0;
	// 페이지 네비게이션 바의 시작 페이지 번호
	private int startPage= 0;
	// 페이지 네비게이션 바의 끝 페이지 번호
	private int endPage = 0;
	// 현재 페이지 번호
	private int pageNo = 0;
	//한 페이지 출력되는 자료의 건수
	private int size = 10; 
	// 서칭
	private String searchValue;
	
	public PageResponseVO_board(List<Board> list, int totalPage, int pageNo, int size, String searchValue) {
		this.list = list;
		this.totalCount = totalPage;
		this.totalPage = (int)Math.ceil((double)totalPage / size);
		this.pageNo = pageNo;
		this.size = size;
		this.searchValue = searchValue;
		
		startPage = ((pageNo-1)/10)*10 + 1;
		endPage = ((pageNo-1)/10)*10 + 10;
		if (endPage > this.totalPage) endPage = this.totalPage;
		
	}
	
	public boolean isPrev(){
		return startPage != 1;
	}
	
	public boolean isNext(){
		return totalPage != endPage;
	}

}
