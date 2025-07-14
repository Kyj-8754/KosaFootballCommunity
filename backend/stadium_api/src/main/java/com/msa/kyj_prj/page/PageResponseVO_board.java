package com.msa.kyj_prj.page;


import java.util.List;

import com.msa.kyj_prj.stadium.Stadium;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//VO : Value Object
@Data @AllArgsConstructor @NoArgsConstructor
@Schema(description = "게시판 목록 및 페이징 응답 DTO")
public class PageResponseVO_board {
	// 목록
	@Schema(description = "게시판 목록", example = "[{...}, {...}]")
	private List<Stadium> list;
	// 전체 글 갯수
	@Schema(description = "전체 글 개수", example = "120")
	private int totalCount = 0;
	// 전체 페이지수
	@Schema(description = "전체 페이지 수", example = "13")
	private int totalPage = 0;
	@Schema(description = "페이지 네비게이션 바 시작 번호", example = "1")
	// 페이지 네비게이션 바의 시작 페이지 번호
	private int startPage= 0;
	@Schema(description = "페이지 네비게이션 바 끝 번호", example = "9")
	// 페이지 네비게이션 바의 끝 페이지 번호
	private int endPage = 0;
	@Schema(description = "현재 페이지 번호", example = "1")
	// 현재 페이지 번호
	private int pageNo = 0;
	@Schema(description = "페이지 당 자료 수", example = "9")
	//한 페이지 출력되는 자료의 건수
	private int size = 9; 
	@Schema(description = "검색어", example = "서울")
	// 서칭
	private String searchValue;
	 @Schema(description = "검색 타입", example = "title")
	private String searchType;
	
	
	public PageResponseVO_board(List<Stadium> list, int totalPage, int pageNo, String searchType, String searchValue) {
		this.list = list;
		this.totalCount = totalPage;
		this.totalPage = (int)Math.ceil((double)totalPage / size);
		this.pageNo = pageNo;
		this.searchValue = searchValue;
		this.searchType = searchType;
		startPage = ((pageNo-1)/9)*9 + 1;
		endPage = ((pageNo-1)/9)*9 + 9;
		if (endPage > this.totalPage) endPage = this.totalPage;
		
	}
	
	public boolean isPrev(){
		return startPage != 1;
	}
	
	public boolean isNext(){
		return totalPage != endPage;
	}

}
