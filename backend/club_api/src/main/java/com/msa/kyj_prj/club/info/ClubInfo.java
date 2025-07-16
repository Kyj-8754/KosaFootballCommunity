package com.msa.kyj_prj.club.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클럽 상세 정보 DTO (tbl_club_info 테이블 매핑)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "클럽 상세 정보 DTO (tbl_club_info 테이블 매핑)")
public class ClubInfo {

    @Schema(description = "클럽 ID (PK/FK)", example = "12")
    private int club_id;

    @Schema(description = "성별 ('남성', '여성', '혼성')", example = "혼성")
    private String gender;

    @Schema(description = "나이대 (예: '10~20', '20~30', ...)", example = "20~30")
    private String age_group;

    @Schema(description = "활동 요일(여러 요일은 CSV, 예: '월,수,금')", example = "월,수,금")
    private String active_days;

    @Schema(description = "활동 시간대(여러 시간대는 CSV, 예: '아침,저녁')", example = "아침,저녁")
    private String active_times;
}
