package com.msa.kyj_prj.match.log;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "매치 로그 DTO")
public class MatchLog {

    @Schema(description = "로그 ID", example = "10001")
    private Long log_id;

    @Schema(description = "매치 ID", example = "501")
    private Long match_id;

    @Schema(description = "로그 타입", example = "골")
    private String log_type;

    @Schema(description = "소속 클럽 ID", example = "3001")
    private Integer club_id;

    @Schema(description = "사용자 회원 번호", example = "2002")
    private Integer user_no;

    @Schema(description = "로그 생성일시", example = "2025-07-15T14:30:00")
    private LocalDateTime log_created_at;

    @Schema(description = "로그 수정일시", example = "2025-07-15T15:00:00")
    private LocalDateTime log_modified_at;

    @Schema(description = "로그 내용/메모", example = "백태클")
    private String log_memo;

    // 조인 값
    @Schema(description = "클럽 이름", example = "FC 강남")
    private String club_name;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String user_name;
}
