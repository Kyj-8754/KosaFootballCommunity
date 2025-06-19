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

    private int bno;          // 게시물 번호 (PK)
    private int clubId;       // 모집글이 속한 클럽 ID (FK)

    private String title;     // 제목
    private String content;   // 내용
    private String writer;    // 작성자 (user_id)

    private String regDate;       // 등록일
    private int viewCount;        // 조회수
    private String modifiedDate;  // 수정일

    private boolean closed;   // 모집 마감 여부 (false = 모집중, true = 마감됨)

    private String clubName;  // 출력용 클럽명 (JOIN)
}
