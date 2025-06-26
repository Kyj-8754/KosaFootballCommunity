package com.msa.kyj_prj.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitBoard {

    private int bno;               // 게시물 번호 (PK)
    private Integer club_id;       // ✅ nullable 처리 (서버에서 자동 주입)

    private String title;          // 제목
    private String content;        // 내용
    private String writer;         // 작성자 (user_id)

    private String reg_date;       // 등록일
    private int view_count;        // 조회수
    private String modified_date;  // 수정일

    private boolean closed = false;        // 모집 마감 여부 기본값
    private String club_name;              // 출력용 클럽명 (JOIN)
    private int user_no;                   // 팀장 여부 확인용
}
