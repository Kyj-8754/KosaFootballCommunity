package com.msa.kyj_prj.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MatchService {

    @Autowired
    private MatchDAO matchDAO;

    // 소셜매치 목록 호출
    public List<Match> getSocialMatches(Map<String, Object> params) {
        params.put("match_code", "social");
        return matchDAO.selectFilteredMatches(params);
    }

    // 리그매치 목록 호출
    public List<Match> getLeagueMatches(Map<String, Object> params) {
        params.put("match_code", "league");
        return matchDAO.selectFilteredMatches(params);
    }

    // 매치 상세보기
    public Match getMatchDetail(Long matchId) {
        return matchDAO.selectMatchDetailById(matchId);
    }
    
    // 매치 참가 신청
    public void applyToMatch(MatchParticipant participant) {
        Long matchId = participant.getMatch_id();

        // 매치 정보 조회
        Match match = matchDAO.selectMatchDetailById(matchId);
        String matchCode = match.getMatch_code();

        // 현재 참가 인원(소셜) 또는 클럽 수(리그)
        int currentCount = matchDAO.countMatchParticipants(matchId);

        // 조건 체크
        if ("social".equalsIgnoreCase(matchCode) && currentCount >= 18) {
            throw new IllegalStateException("소셜 매치는 최대 18명까지만 참가할 수 있습니다.");
        }

        if ("league".equalsIgnoreCase(matchCode) && currentCount >= 3) {
            throw new IllegalStateException("리그 매치는 최대 3팀까지만 참가할 수 있습니다.");
        }

        // 조건 통과 시 참가 신청
        matchDAO.insertMatchParticipant(participant);
    }

    // 매치 참가 여부 확인
    public boolean hasUserApplied(Long matchId, Integer userNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", matchId);
        param.put("user_no", userNo);
        return matchDAO.checkUserApplied(param) > 0;
    }
    
    // 매치 신청 취소
    public void cancelMatchParticipant(Long matchId, Integer userNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", matchId);
        param.put("user_no", userNo);
        matchDAO.cancelMatchParticipant(param);
    }

    // 매치 인원 수 조회
    public int getMatchParticipantCount(Long matchId) {
        return matchDAO.countMatchParticipants(matchId);
    }
    
    // 유저 번호로 클럽 정보 조회
    public Map<String, Object> getClubByUserNo(Long userNo) {
        return matchDAO.selectClubByUserNo(userNo);
    }
    
    // 지역명 리스트 조회
    public List<String> getAllAreanms() {
        return matchDAO.selectDistinctAreanms();
    }
    
    // 특정 매치의 참가자 + 사용자 이름 조회
    public List<Map<String, Object>> getMatchParticipantsWithNames(Long matchId) {
        return matchDAO.selectParticipantsByMatchId(matchId);
    }
    
    // 특정 매치의 참가자 + 사용자 이름 + 클럽 명 조회
    public List<Map<String, Object>> selectParticipantsWithClubByMatchId(Long matchId) {
        return matchDAO.selectParticipantsWithClubByMatchId(matchId);
    }
    
    // 매치 참가자 상태 업데이트
    public int updateMatchParticipantStatus(Map<String, Object> param) {
        int result = matchDAO.updateMatchParticipantStatus(param);

        Long matchId = Long.valueOf(param.get("match_id").toString());

        // 1. 매치 정보 조회
        Match match = matchDAO.selectMatchDetailById(matchId);
        String matchCode = match.getMatch_code();

        // 2. 현재 참가 인원/팀 수 조회
        int currentCount = matchDAO.countMatchParticipants(matchId);

        // 3. 마감 처리 or 마감 해제
        String currentClosed = match.getMatch_closed();
        String newClosed = null;

        if ("social".equalsIgnoreCase(matchCode)) {
            if (currentCount >= 18 && !"closed".equalsIgnoreCase(currentClosed)) {
                newClosed = "closed";
            } else if (currentCount < 18 && !"active".equalsIgnoreCase(currentClosed)) {
                newClosed = "active";
            }
        }

        else if ("league".equalsIgnoreCase(matchCode)) {
            if (currentCount >= 3 && !"closed".equalsIgnoreCase(currentClosed)) {
                newClosed = "closed";
            } else if (currentCount < 3 && !"active".equalsIgnoreCase(currentClosed)) {
                newClosed = "active";
            }
        }

        // 4. 상태가 바뀌는 경우에만 업데이트
        if (newClosed != null) {
            Map<String, Object> closeParam = new HashMap<>();
            closeParam.put("match_id", matchId);
            closeParam.put("match_closed", newClosed);
            matchDAO.updateMatchClosedStatus(closeParam);
        }

        return result;
    }
    
    // 마감 처리
    public void closeMatch(Long matchId) {
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", matchId);
        param.put("match_closed", "closed");
        matchDAO.updateMatchClosedStatus(param);
    }
    
    @Scheduled(cron = "0 0 */2 * * *", zone = "Asia/Seoul") // 매 2시간마다 실행
    public void activatePastMatches() {
    	System.out.println("예약 실행");
        List<Match> allMatches = matchDAO.selectAllMatches(new HashMap<>());
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        for (Match match : allMatches) {
        	LocalDateTime matchDateTime = match.getMatch_date(); // OK
            String status = match.getMatch_status();

            // 1단계: 'waiting' + 오늘이거나 이전 날짜 → 'active'
            if ("waiting".equalsIgnoreCase(status) && !matchDateTime.toLocalDate().isAfter(today)) {
                Map<String, Object> param = new HashMap<>();
                param.put("match_id", match.getMatch_id());
                param.put("match_status", "active");
                matchDAO.updateMatchStatus(param);
            }

            // 2단계: 'active' + 2시간 경과 → 'completed'
            else if ("active".equalsIgnoreCase(status) && matchDateTime.plusHours(2).isBefore(now)) {
                Map<String, Object> param = new HashMap<>();
                param.put("match_id", match.getMatch_id());
                param.put("match_status", "completed");
                matchDAO.updateMatchStatus(param);
            }
        }
    }
    
    // 연동된 예약 id 찾기
    public Long getReservationIdByBoardId(Long boardId) {
        if (boardId == null) {
            throw new IllegalArgumentException("board_id는 null일 수 없습니다.");
        }
        return matchDAO.getReservationIdByBoardId(boardId);
    }
    
    // 예약 ID 기준 결제 완료 여부 확인
    public boolean isReservationPaid(Long reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("reservation_id는 null일 수 없습니다.");
        }
        return matchDAO.isReservationPaid(reservationId);
    }
    
    // 매치 등록
    public void registerMatch(Match match) {
        log.info("💾 매치 등록 요청: {}", match);

        // 유효성 로그 추가
        log.info("match_date = {}", match.getMatch_date());
        log.info("user_no = {}", match.getUser_no());
        log.info("svcid = {}", match.getSVCID());
        matchDAO.insertMatch(match);
    }
    
    // 클럽 ID 기준 필터링된 매치 목록 조회 (active, completed 제외)
    public List<Map<String, Object>> getFilteredClubMatches(Long clubId) {
        if (clubId == null) {
            throw new IllegalArgumentException("club_id는 null일 수 없습니다.");
        }
        return matchDAO.selectFilteredClubMatches(clubId);
    }
    
    public void applyAndApproveImmediately(MatchParticipant participant) {
        // 1. 먼저 기본 신청
        matchDAO.insertMatchParticipant(participant);

        // 2. 바로 상태를 'approve'로 업데이트
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", participant.getMatch_id());
        param.put("user_no", participant.getUser_no());
        param.put("user_status", "approve");
        matchDAO.updateMatchParticipantStatus(param);
    }
    
    public void cancelMatchesByTypeAndId(String type, Long id) {
        Long reservationId = null;

        if ("reservation".equalsIgnoreCase(type)) {
            reservationId = id;
            log.info("📝 [예약기반] reservation_id: {}", reservationId);
        } else if ("payment".equalsIgnoreCase(type)) {
            reservationId = matchDAO.getReservationIdByPaymentId(id); // DAO 필요
            log.info("💰 [결제기반] payment_id: {} → reservation_id: {}", id, reservationId);
            if (reservationId == null) {
                throw new IllegalArgumentException("해당 payment_id에 대한 예약이 없습니다: " + id);
            }
        } else {
            throw new IllegalArgumentException("유효하지 않은 type 값: " + type);
        }

        List<Match> matches = matchDAO.getMatchesByReservationId(reservationId);
        log.info("📌 reservation_id {} → 연동된 매치 수: {}", reservationId, matches.size());

        int updatedCount = 0;
        for (Match match : matches) {
            Long matchId = match.getMatch_id();
            String currentStatus = match.getMatch_status();

            if ("waiting".equalsIgnoreCase(currentStatus)
             || "active".equalsIgnoreCase(currentStatus)
             || "completed".equalsIgnoreCase(currentStatus)) {

                Map<String, Object> param = new HashMap<>();
                param.put("match_id", matchId);
                param.put("match_status", "cancelled");

                int result = matchDAO.updateMatchStatus(param);
                if (result > 0) {
                    log.info("✅ 매치 ID {} 상태 변경 완료 → cancelled", matchId);
                    updatedCount++;
                } else {
                    log.warn("⚠ 매치 ID {} 상태 변경 실패", matchId);
                }
            } else {
                log.debug("⏭ 매치 ID {} 상태가 waiting/active/completed 아님 → 건너뜀", matchId);
            }
        }

        log.info("📊 총 상태 변경된 매치 수: {}", updatedCount);
    }

    // 과거 매치 5개 호출
    public List<Match> getRecentCompletedMatches() {
        return matchDAO.selectRecentCompletedMatches();
    }
}
