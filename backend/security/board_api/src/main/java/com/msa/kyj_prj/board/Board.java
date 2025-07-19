package com.msa.kyj_prj.board;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "게시글 DTO")
public class Board {
    @Schema(description = "게시글 ID (PK)", example = "101")
    private Long board_id;

    @Schema(description = "작성자 회원 번호 (FK)", example = "12")
    private Integer user_no;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String user_name;

    @Schema(description = "게시글 제목", example = "7월 풋살 매치 모집합니다!")
    private String board_title;

    @Schema(description = "게시글 내용", example = "일시: 7/20 토요일 오후 4시\n장소: 잠실 보조경기장\n신청은 댓글로 남겨주세요.")
    private String board_content;

    @Schema(description = "게시글 카테고리", example = "모집게시판")
    private String board_category;

    @Schema(description = "게시글 작성일", example = "2025-07-15T10:15:30")
    private LocalDateTime board_created_at;

    @Schema(description = "게시글 수정일", example = "2025-07-15T11:30:00")
    private LocalDateTime board_modified_at;

    @Schema(description = "게시글 상태", example = "active")  // 예: active, deleted, hidden 등
    private String board_status;

    @Schema(description = "게시글 조회수", example = "154")
    private Integer board_viewcount;

    @Schema(description = "게시글 좋아요 수", example = "23")
    private Integer board_likecount;
}
