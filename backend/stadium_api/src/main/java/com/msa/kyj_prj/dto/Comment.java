package com.msa.kyj_prj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class Comment {
	@Schema(description = "구장 리뷰 ID", example = "1")
	private int comment_no;			//댓글 번호
	@Schema(description = "내용", example = "글 내용")
	private String content;			//댓글 내용
	@Schema(description = "구장 ID", example = "S190522144836251426")
	private String svcid;			//글 번호
	@Schema(description = "글 작성자", example = "글작성자 이름")
	private String username;			//글 작성자
	@Schema(description = "등록일", example = "2025-07-16 08:45:28.000")
	private String regist_date;		//글 등록일
	@Schema(description = "수정일", example = "2025-07-16 9:45:28.000")
	private String modified_date;	//글 수정일
	@Schema(description = "삭제 상태", example = "true")
	private String status;			//글 상태
	@Schema(description = "별점", example = "2.5")
	private Double rating;
}
