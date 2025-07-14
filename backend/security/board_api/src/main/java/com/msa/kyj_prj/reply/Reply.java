package com.msa.kyj_prj.reply;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reply {
    private Long reply_id;                 // 댓글 ID (PK, auto_increment)
    private Long board_id;                // 게시글 ID (FK)
    private Long parent_reply_id;         // 부모 댓글 ID (nullable)
    private Integer user_no;              // 작성자 회원번호 (FK)
    private String user_name;             // 작성자 이름
    private String reply_content;         // 댓글 내용
    private LocalDateTime reply_created_at;   // 작성일 (DEFAULT CURRENT_TIMESTAMP)
    private LocalDateTime reply_modified_at;  // 수정일 (ON UPDATE CURRENT_TIMESTAMP)
    private String reply_status;          // 상태값 (ex: active, deleted)
}
