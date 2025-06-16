package com.msa.kyj_prj.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class Comment {
	private int comment_no;			//댓글 번호
	private String content;			//댓글 내용
	private int bno;				//글 번호
	private String userid;			//글 작성자
	private String regist_date;		//글 등록일
	private String modified_date;	//글 수정일
}
