package com.msa.kyj_prj.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchParticipant {
    private Long match_id;
    private Integer club_id; // 팀이 없는 개인 참가자는 null 가능
    private Integer user_no;
    private String user_role;    // 기본값: "member"
    private String user_status;  // 기본값: "apply"
}
