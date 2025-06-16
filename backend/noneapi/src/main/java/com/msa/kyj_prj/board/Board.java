package com.msa.kyj_prj.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class Board {
	private int bno;				// 게시물 번호
	private String title;			// 제목
	private String content;			// 내용
	private String writer;			// 글 작성자
	private String passwd;			// 비밀번호
	private String reg_date;		// 등록일
	private int view_count;			// 조회수
	private String is_deleted;		// 삭제일
	private String deleted_at;		// 삭제 여부
	private String modified_date;	// 글 수정일
	
	
//	public boolean isValid() {
//		if (userid == null || userid.length() == 0) return false;
//		if (passwd == null || passwd.length() == 0) return false;
//		if (name == null || name.length() == 0) return false;
//		return true;
//	}
	
}
