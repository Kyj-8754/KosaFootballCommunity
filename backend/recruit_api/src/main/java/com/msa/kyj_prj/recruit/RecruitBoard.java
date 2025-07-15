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
    private int club_id;       // ✅ nullable 처리 (서버에서 자동 주입)
    private String club_name;  // 클럽 이름
    
    private String title;          // 제목
    private String content;        // 내용
    private int user_no;         // 작성자 (user_id)

    private String reg_date;       // 등록일
    private String modified_date;  // 수정일
    private int view_count;        // 조회수

    private int is_closed;// 모집 마감 여부 기본값
    private int apply_count; // getter, setter 꼭 만들기!
    
}
