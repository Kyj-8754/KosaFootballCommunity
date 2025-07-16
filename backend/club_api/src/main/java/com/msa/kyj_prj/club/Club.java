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

    @Schema(description = "클럽 ID (PK)", example = "5")
    private int club_id;

    @Schema(description = "팀 고유 코드 (url 주소)", example = "GEN")
    private String team_code;

    @Schema(description = "클럽 이름", example = "GEN")
    private String club_name;

    @Schema(description = "팀장 user_no", example = "4")
    private int user_no;

    @Schema(description = "클럽 로고 이미지 경로", example = "/images/club/10_logo.png")
    private String logo_path;

    @Schema(description = "팀 평점(클럽 레벨)", example = "골드")
    private String club_level;

    @Schema(description = "팀 순위", example = "3")
    private int ranking;

    @Schema(description = "승리 횟수", example = "8")
    private int win_count;

    @Schema(description = "무승부 횟수", example = "2")
    private int draw_count;

    @Schema(description = "패배 횟수", example = "1")
    private int loss_count;

    @Schema(description = "클럽 소개", example = "즐겁게 운동하는 FC MSA입니다!")
    private String description;

    @Schema(description = "생성일 (yyyy-MM-dd HH:mm:ss 등)", example = "2024-07-17 12:12:00")
    private String created_at;
}
