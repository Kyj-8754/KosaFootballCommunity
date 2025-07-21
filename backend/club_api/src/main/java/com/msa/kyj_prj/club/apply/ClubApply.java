package com.msa.kyj_prj.club.apply;

import java.time.LocalDateTime;

import lombok.Data;

// Swagger/OpenAPI 어노테이션 임포트
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "클럽 가입 신청 정보를 나타내는 모델") // Class description for Swagger
public class ClubApply {

    @Schema(description = "가입 신청의 고유 ID (자동 생성)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private int apply_id; // AUTO_INCREMENT PRIMARY KEY

    @Schema(description = "모집글 번호 (외래 키)", example = "123")
    private int bno; // 모집글 번호 (FK)

    @Schema(description = "클럽 ID (외래 키)", example = "101")
    private int club_id; // 클럽 ID (FK)

    @Schema(description = "가입을 신청한 사용자의 번호", example = "777")
    private int appli_user_no; // 지원자 ID

    @Schema(description = "가입 신청 날짜 (YYYY-MM-DD HH:MM:SS 형식)", example = "2023-07-16 15:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private String apply_date; // 지원 날짜 (기본값: 현재 시각)

    @Schema(description = "신청 상태 (예: pending, approved, rejected)", example = "pending")
    private String status; // 상태 (예: pending, approved, rejected)
}
