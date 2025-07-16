package com.msa.kyj_prj.club.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Swagger/OpenAPI 어노테이션 임포트
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "클럽 멤버 정보를 나타내는 모델") // 클래스에 대한 설명
public class ClubMember {

//	@Schema(description = "클럽 멤버의 고유 ID (자동 생성)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
//	private int id; // PK (AUTO_INCREMENT, 테이블에 있으면 사용)

	@Schema(description = "클럽의 ID (외래 키)", example = "101")
	private int club_id; // 클럽 ID (FK)

	@Schema(description = "사용자 번호 (user 테이블의 user_no)", example = "777")
	private int user_no; // 사용자 번호 (FK: user 테이블의 user_no)

	@Schema(description = "클럽 내 역할 (LEADER 또는 MEMBER)", example = "MEMBER")
	private String role; // 역할 (LEADER / MEMBER)

	@Schema(description = "클럽 가입일자 (YYYY-MM-DD HH:MM:SS 형식)", example = "2023-01-15 10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private String joined_at; // 가입일자 (문자열 처리)

	@Schema(description = "클럽 탈퇴일자 (YYYY-MM-DD HH:MM:SS 형식, 탈퇴하지 않았으면 null)", example = "2024-03-20 14:00:00", nullable = true, accessMode = Schema.AccessMode.READ_ONLY)
	private String left_at; // 탈퇴일자 (문자열 처리, 탈퇴하지 않았으면 null)

	@Schema(description = "클럽 멤버의 사용자 이름", example = "홍길동")
	private String user_name; // 사용자 이름

	@Schema(description = "경기 참가 횟수", example = "15")
	private String match_count; // 경기 참가수

	@Schema(description = "경기 최우수 선수 (Player of the Match) 횟수", example = "3")
	private String pom; // Player of the Match
}
