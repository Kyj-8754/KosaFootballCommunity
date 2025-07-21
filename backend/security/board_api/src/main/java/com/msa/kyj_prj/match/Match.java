package com.msa.kyj_prj.match;

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
@Schema(description = "매치 정보 DTO")
public class Match {

    @Schema(description = "매치 ID", example = "501")
    private Long match_id;

    @Schema(description = "매치 제목", example = "7:7 풋살 정기 매치")
    private String match_title;

    @Schema(description = "매치 등록일", example = "2025-07-10T09:00:00")
    private LocalDateTime match_created_at;

    @Schema(description = "매치 수정일", example = "2025-07-12T12:30:00")
    private LocalDateTime match_modified_at;

    @Schema(description = "주최자 회원 번호", example = "102")
    private Integer user_no;

    @Schema(description = "매니저 회원 번호", example = "203")
    private Integer manager_no;

    @Schema(description = "경기 예정일", example = "2025-07-20T16:00:00")
    private LocalDateTime match_date;

    @Schema(description = "구장 서비스 ID", example = "SVC123456")
    private String SVCID;

    @Schema(description = "매치 마감 여부", example = "active")
    private String match_closed;

    @Schema(description = "성별 조건", example = "all")  // 예: all, male, female
    private String gender_condition;

    @Schema(description = "매치 부가 설명", example = "비 오는 경우 취소될 수 있습니다.")
    private String match_description;

    @Schema(description = "매치 상태", example = "waiting") // 예: waiting, ongoing, done
    private String match_status;

    @Schema(description = "매치 게시글 상태", example = "active") // 예: active, hidden, deleted
    private String match_board_status;

    @Schema(description = "경기 종류", example = "social") // 예: social, league
    private String match_code;

    @Schema(description = "예약 ID", example = "9001")
    private Long reservation_id;

    // ⛔ 스타디움 테이블 조인 필드: Swagger 주석 생략 (요청대로)
    @Schema(description = "구장 id", example = "SV123456")
    private String SVCNM;
    
    @Schema(description = "구장명", example = "구장이름")
    private String PLACENM;
    
    @Schema(description = "X좌표", example = "12")
    private String X;
    
    @Schema(description = "Y좌표", example = "12")
    private String Y;
    
    @Schema(description = "구장 이미지")
    private String IMG_PATH;
    
    @Schema(description = "전화번호", example = "000-0000-0000")
    private String TELNO;
    
    @Schema(description = "공지사항")
    private String NOTICE;
    
    @Schema(description = "서브 구장명", example = "서브 구장이름")
    private String SUBPLACENM;
    
    @Schema(description = "관리 단체명", example = "서울시")
    private String ORGNM;
    
    @Schema(description = "운영 전화번호", example = "000-0000-0000")
    private String SVCENDTELNO;
    
    @Schema(description = "지역명", example = "송파구")
    private String AREANM;
    
    @Schema(description = "주소", example = "송파구~")
    private String ADRES;
    
    @Schema(description = "안내사항")
    private String DTLCONT;
    
    @Schema(description = "가격", example = "100000")
    private Integer price;

    // 유저 테이블 조인
    @Schema(description = "매니저 이름", example = "김매니저")
    private String manager_name;
}
