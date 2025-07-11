package com.msa.kyj_prj.club.info;

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
public class ClubInfo {

    private int club_id;             // 클럽 ID (PK/FK)
    private String gender;           // 성별 (남성/여성/혼성)
    private String age_group;        // 나이대 ("10~20", "20~30" ...)
    private String active_days;      // 활동 요일 ("월,수,금" 등 CSV)
    private String active_times;     // 활동 시간대 ("아침,저녁" 등 CSV)
}
