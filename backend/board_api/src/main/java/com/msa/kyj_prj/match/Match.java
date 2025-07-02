package com.msa.kyj_prj.match;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Match {
    private Long match_id;                   // 매치 ID
    private String match_title;              // 매치 제목
    private LocalDateTime match_created_at;  // 매치 등록일
    private LocalDateTime match_modified_at; // 매치 수정일
    private Integer user_no;                 // 주최자 ID
    private Integer manager_no;                 // 매니저 ID
    private LocalDateTime match_date;        // 경기일
    private String SVCID;                    // 구장 ID
    private String match_closed;             // 마감 여부
    private String gender_condition;         // 성별 조건
    private String match_description;        // 부가 설명
    private String match_status;             // 매치 상태
    private String match_board_status;       // 게시글 상태
    private String match_code;               // 경기 종류
    
    // 스타디움 테이블 조인
    private String SVCNM;
    private String PLACENM;
    private String X;
    private String Y;
    private String IMG_PATH;
    private String TELNO;
    private String NOTICE;
    private String SUBPLACENM;
    private String ORGNM;
    private String SVCENDTELNO;
    private String AREANM;
    private String ADRES;
    private String DTLCONT;
    
    // 유저 테이블 조인
    private String manager_name;

}
