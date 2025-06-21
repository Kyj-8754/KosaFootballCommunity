package com.msa.kyj_prj.club;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
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

    @JsonProperty("club_id")
    private int clubId;

    @JsonProperty("team_code") 
    private String teamCode;

    @JsonProperty("club_name")
    private String clubName;

    @JsonProperty("leader_user_id")
    private String leaderUserId;

    @JsonProperty("logo_path")
    private String logoPath;

    @JsonProperty("rating")
    private int rating;

    @JsonProperty("ranking")
    private int ranking;

    @JsonProperty("win_count")
    private int winCount;

    @JsonProperty("draw_count")
    private int drawCount;

    @JsonProperty("loss_count")
    private int lossCount;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    // 수동 setter 예시 (Lombok 대비용)
    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }
}
