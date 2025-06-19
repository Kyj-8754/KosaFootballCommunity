package com.msa.kyj_prj.board;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board {
    private Long board_id;          // 게시글 ID (PK, auto_increment)
    private Integer user_no;        // 작성자 회원번호 (FK)
    private String user_name;
    private String board_title;     // 제목
    private String board_content;      // 내용 (MEDIUMTEXT)
    private String board_category;      // 카테고리 코드
    private LocalDateTime board_created_at;  // 작성일 (DEFAULT CURRENT_TIMESTAMP)
    private LocalDateTime board_modified_at;   // 수정일 (ON UPDATE CURRENT_TIMESTAMP)
    private String board_status;      // 게시글 상태
    private Integer board_viewcount;
}
