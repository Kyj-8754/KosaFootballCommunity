package com.msa.kyj_prj.club;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클럽 정보를 담는 DTO 클래스 (tbl_club 테이블과 매핑됨)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Club {

    private int club_id;             // 클럽 ID (PK)
    private String team_code;        // 팀 고유 코드
    private String club_name;        // 클럽 이름
    private int user_no;   		 // 팀장의 user_no
    private String logo_path;        // 클럽 로고 이미지 경로

    private String club_level;              // 팀 평점
    private int ranking;             // 팀 순위
    private int win_count;           // 승
    private int draw_count;          // 무
    private int loss_count;          // 패

    private String description;      // 클럽 소개
    private String created_at;       // 생성일 (문자열 처리: yyyy-MM-dd HH:mm:ss 등)
}
