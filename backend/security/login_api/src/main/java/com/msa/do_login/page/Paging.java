package com.msa.do_login.page;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Paging {
	// 전체 게시글 개수
	  private int totalData;
	  // 전체 페이지 개수
	  private int totalPage;

	  // 페이징바 구성 요소들
	  // 페이징 바의 크기
	  private int pageBarSize = 10;
	  // 페이징바 페이지 시작, 끝
	  private int pageBarStart;
	  private int pageBarEnd;

	  // 현재 페이지
	  private int nowPage = 1;  
	  // 목록에 보여질 게시글 개수 설정
	  private int numPerPage = 10;
	  // 쿼리에 사용할 Limit 값
	  // Limit 10,10-> 10개건너뛰고 11페이지부터 10개까지 -> 다음페이지
	  private int limitPageNo;

	  // 이전, 다음 여부
	  private boolean prev = true;
	  private boolean next = true;

	  // 전체 게시글 개수를 set 해줬을때 동작할 메소드
	  public void calcPaging() {
	    // 2번 페이지(11~20) 조회를 원한다 -> limit 10,10 -> 10개뛰어넘고 10개본다
	    // 3번 페이지(21~30) -> limit 20,10 -> 20개뛰어넘고 10개 본다
	    limitPageNo = (nowPage - 1) * numPerPage;

	    // 전체 페이지 개수(26 -> 3개페이지)
	    // 전체 데이터/페이지당게시글개수
	    // 더블로 계산하면 소수점으로 결과가 나오고 이를 올림
	    totalPage = (int) Math.ceil((double) totalData / numPerPage);

	    pageBarStart = ((nowPage - 1) / pageBarSize) * pageBarSize + 1;
	    pageBarEnd = pageBarStart + pageBarSize - 1;
	    if (pageBarEnd > totalPage)
	      pageBarEnd = totalPage;

	    // 이전(1번페이지에서만 없고 전부 다 있음)
	    if (pageBarStart == 1)
	      prev = false;
	    // 다음(페이지개수와 지금보고있는 페이지가 같을경우 다음은 없다)
	    if (pageBarEnd >= totalPage)
	      next = false;
	  }
}
