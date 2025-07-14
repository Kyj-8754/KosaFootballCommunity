package com.msa.kyj_prj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class Comment {
	@Schema(description = "댓글 번호", example = "SVC123")
	private int comment_no;			//댓글 번호
	@Schema(description = "댓글 내용", example = "SVC123")
	private String content;			//댓글 내용
	@Schema(description = "글 번호", example = "SVC123")
	private String svcid;			//글 번호
	@Schema(description = "글 작성자", example = "SVC123")
	private String userid;			//글 작성자
	@Schema(description = "글 등록일", example = "SVC123")
	private String regist_date;		//글 등록일
	@Schema(description = "글 수정일", example = "SVC123")
	private String modified_date;	//글 수정일
	@Schema(description = "글 삭제 여부용", example = "SVC123")
	private String status;			//글 상태
	@Schema(description = "별점", example = "SVC123")
	private Double rating;
}
