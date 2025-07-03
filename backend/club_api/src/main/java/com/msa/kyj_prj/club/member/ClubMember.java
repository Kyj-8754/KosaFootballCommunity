package com.msa.kyj_prj.club.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMember {

	private int id; // PK (AUTO_INCREMENT, 테이블에 있으면 사용)
	private int club_id; // 클럽 ID (FK)
	private int user_no; // 사용자 번호 (FK: user 테이블의 user_no)
	private String role; // 역할 (LEADER / MEMBER)
	private String joined_at; // 가입일자 (문자열 처리)
	private String left_at; // 탈퇴일자 (문자열 처리, 탈퇴하지 않았으면 null)
	private String user_name; // 사용자 이름
	private String match_count; // 경기 참가수
	private String POM; // 
}
