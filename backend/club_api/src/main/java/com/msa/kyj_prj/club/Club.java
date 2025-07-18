package com.msa.kyj_prj.club;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "클럽 정보를 담는 DTO 클래스 (tbl_club 테이블 매핑)")
public class Club {

	@Schema(description = "클럽 ID (PK)", example = "11")
	private int club_id;

	@Schema(description = "팀 고유 코드 (url 주소)", example = "test11")
	private String team_code;

	@Schema(description = "클럽 이름", example = "테스트11")
	private String club_name;

	@Schema(description = "팀장 user_no", example = "12")
	private int user_no;

	@Schema(description = "클럽 로고 이미지 경로", example = "/images/club/11_logo.png")
	private String logo_path;

	@Schema(description = "클럽 레벨(승률에 따라 레벨이 측정됨)", example = "브론즈")
	private String club_level;

	@Schema(description = "클럽 순위(승률에 따라 순위가 측정됨)", example = "6")
	private int ranking;

	@Schema(description = "승리 횟수", example = "8")
	private int win_count;

	@Schema(description = "무승부 횟수", example = "2")
	private int draw_count;

	@Schema(description = "패배 횟수", example = "1")
	private int loss_count;

	@Schema(description = "클럽 소개", example = "즐겁게 운동하는 GEN입니다!")
	private String description;

	@Schema(description = "생성일 (yyyy-MM-dd HH:mm:ss 등)", example = "2024-07-17 12:12:00")
	private String created_at;
	
}
