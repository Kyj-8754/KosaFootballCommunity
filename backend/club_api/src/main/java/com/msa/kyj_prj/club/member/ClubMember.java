package com.msa.kyj_prj.club.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클럽 멤버+회원 정보를 담는 DTO 클래스
 * (tbl_club_member + user 테이블 일부 컬럼)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMember {

    // tbl_club_member 테이블 컬럼
    private int id;              // PK (AUTO_INCREMENT, 테이블에 있으면 사용)
    private int club_id;         // 클럽 ID (FK)
    private int user_no;         // 사용자 번호 (FK: user 테이블의 user_no)
    private String role;         // 역할 (LEADER / MEMBER)
    private String joined_at;    // 가입일자 (문자열 처리)
    private String left_at;      // 탈퇴일자 (문자열 처리, 탈퇴하지 않았으면 null)

    // user 테이블 컬럼 (리스트에 표시용, 추가정보)
    private String user_name;    // 사용자 이름
    private String user_birth;   // 생년월일
    private String user_phone;   // 연락처

}
