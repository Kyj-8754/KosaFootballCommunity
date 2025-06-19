package com.msa.kyj_prj.club;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클럽 정보를 담는 DTO 클래스 MyBatis에서 tbl_club 테이블과 매핑됨
 */
@Data // Getter, Setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 전체 필드를 사용하는 생성자
@Builder // 객체 생성 시 Builder 패턴 사용 가능
public class Club {

    // 클럽 ID (AUTO_INCREMENT)
    private int clubId;

    // 클럽 이름
    private String clubName;

    // 클럽 리더의 회원 ID
    private String leaderUserId;

    // 클럽 로고 이미지 경로
    private String logoPath;

    // 클럽 레이팅 점수
    private int rating;

    // 클럽 순위
    private int ranking;

    // 클럽 승리 횟수
    private int winCount;

    // 클럽 무승부 횟수
    private int drawCount;

    // 클럽 패배 횟수
    private int lossCount;

    // 클럽 소개 설명
    private String description;

    // 클럽 생성일시 (기본값: 현재 시간)
    private LocalDateTime createdAt;

    // 수동 추가: Lombok이 작동하지 않을 경우를 대비한 setter
    public void setClubId(int clubId) {
        this.clubId = clubId;
    }
}
