package com.msa.kyj_prj.match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "매치 참가자 DTO")
public class MatchParticipant {

    @Schema(description = "매치 ID", example = "501")
    private Long match_id;

    @Schema(description = "소속 클럽 ID", example = "3001")
    private Integer club_id;

    @Schema(description = "회원 번호", example = "102")
    private Integer user_no;

    @Schema(description = "사용자 역할", example = "member")
    private String user_role;

    @Schema(description = "참가 상태", example = "apply")
    private String user_status;
}
