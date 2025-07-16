package com.msa.kyj_prj.recruit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "모집 게시글(RecruitBoard) DTO")
public class RecruitBoard {

    @Schema(description = "게시글 번호(PK)", example = "12")
    private int bno;

    @Schema(description = "소속 클럽 ID", example = "7")
    private int club_id;

    @Schema(description = "클럽명", example = "GEN")
    private String club_name;

    @Schema(description = "모집글 제목", example = "일요일 오전 10시 경기 멤버 모집")
    private String title;

    @Schema(description = "모집글 내용", example = "남녀노소 누구나 환영! 경기 후 번개 회식도 있어요.")
    private String content;

    @Schema(description = "작성자(회원번호)", example = "3")
    private int user_no;

    @Schema(description = "등록일", example = "2024-07-16")
    private String reg_date;

    @Schema(description = "수정일", example = "2024-07-16")
    private String modified_date;

    @Schema(description = "조회수", example = "9")
    private int view_count;

    @Schema(description = "모집 마감 여부(0:모집중, 1:마감)", example = "0")
    private int is_closed;

    @Schema(description = "신청 인원수", example = "4")
    private int apply_count;
}
