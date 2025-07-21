package com.msa.kyj_prj.page;


import java.util.List;

import com.msa.kyj_prj.dto.Stadium;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//VO : Value Object
@Data @AllArgsConstructor @NoArgsConstructor
@Schema(description = "댓글 페이징 응답 DTO")
public class PageResponseVO_Comment {
	// 목록
	@Schema(description = "댓글 목록", example = "[{...}, {...}]")
	private List<Stadium> list;
	// 전체 글 갯수
	@Schema(description = "전체 글 개수", example = "120")
	private int totalCount = 0;
	// 전체 페이지수
	@Schema(description = "전체 페이지 수", example = "13")
	private int totalPage = 0;
	// 페이지 네비게이션 바의 시작 페이지 번호
	@Schema(description = "페이지 네비게이션 바 시작 번호", example = "1")
	private int startPage= 0;
	// 페이지 네비게이션 바의 끝 페이지 번호
	@Schema(description = "페이지 네비게이션 바 끝 번호", example = "9")
	private int endPage = 0;
	// 현재 페이지 번호
	@Schema(description = "현재 페이지 번호", example = "1")
	private int pageNo = 0;
	//한 페이지 출력되는 자료의 건수
	@Schema(description = "페이지 당 자료 수", example = "9")
	private int size = 10; 
	// 서칭
	
	
	public PageResponseVO_Comment(List<Stadium> list, int totalPage, int pageNo) {
		this.list = list;
		this.totalCount = totalPage;
		this.totalPage = (int)Math.ceil((double)totalPage / size);
		this.pageNo = pageNo;
		startPage = ((pageNo-1)/10)*10 + 1;
		endPage = ((pageNo-1)/10)*10 + 9;
		if (endPage > this.totalPage) endPage = this.totalPage;
		
	}
	
	public boolean isPrev(){
		return startPage != 1;
	}
	
	public boolean isNext(){
		return totalPage != endPage;
	}

}
