package com.msa.kyj_prj.match.log;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchLog {
    private Long log_id;
    private Long match_id;
    private String log_type;
    private Integer club_id;
    private Integer user_no;
    private LocalDateTime log_created_at;
    private LocalDateTime log_modified_at;
    private String log_memo;
}
