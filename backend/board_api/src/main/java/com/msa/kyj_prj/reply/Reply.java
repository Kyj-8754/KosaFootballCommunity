package com.msa.kyj_prj.reply;

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
@Schema(description = "댓글 DTO")
public class Reply {

    @Schema(description = "댓글 ID", example = "1001")
    private Long reply_id;

    @Schema(description = "게시글 ID", example = "101")
    private Long board_id;

    @Schema(description = "부모 댓글 ID (대댓글용, null 가능)", example = "1000")
    private Long parent_reply_id;

    @Schema(description = "작성자 회원 번호", example = "23")
    private Integer user_no;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String user_name;

    @Schema(description = "댓글 내용", example = "좋은 정보 감사합니다!")
    private String reply_content;

    @Schema(description = "댓글 작성일시", example = "2025-07-15T14:10:00")
    private LocalDateTime reply_created_at;

    @Schema(description = "댓글 수정일시", example = "2025-07-15T15:00:00")
    private LocalDateTime reply_modified_at;

    @Schema(description = "댓글 상태 (예: active, deleted)", example = "active")
    private String reply_status;
}
