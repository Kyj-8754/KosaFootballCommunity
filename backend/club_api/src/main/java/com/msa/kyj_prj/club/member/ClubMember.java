package com.msa.kyj_prj.club.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클럽 멤버 정보를 담는 DTO 클래스 (tbl_club_member 테이블과 매핑됨)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMember {

    private int id;             // PK (AUTO_INCREMENT)
    private int club_id;        // 클럽 ID (FK)
    private int user_no;        // 사용자 번호 (FK: user 테이블의 user_no)
    private String role;        // 역할 (LEADER / MEMBER)
    private String joined_at;   // 가입일자 (문자열 처리)
}
