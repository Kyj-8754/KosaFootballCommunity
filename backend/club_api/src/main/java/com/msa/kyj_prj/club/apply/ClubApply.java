package com.msa.kyj_prj.club.apply;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ClubApply {

    private int apply_id;             // AUTO_INCREMENT PRIMARY KEY
    private int bno;                  // 모집글 번호 (FK)
    private int club_id;              // 클럽 ID (FK)
    private String appli_user_no;      // 지원자 ID
    private String apply_date; // 지원 날짜 (기본값: 현재 시각)
    private String status;            // 상태 (예: pending, approved, rejected)
}
