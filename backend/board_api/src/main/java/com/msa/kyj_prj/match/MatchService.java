package com.msa.kyj_prj.match;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.msa.kyj_prj.match.alarm.MatchAlarmMessageDTO;
import com.msa.kyj_prj.webSocket.Websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

    private final MatchDAO matchDAO;
    private final RestTemplate restTemplate;
    private final Websocket websoket;

    @Value("${alarm.api.url}")
    private String alarmApiUrl;

    // 📌 소셜 매치 목록 조회
    public List<Match> getSocialMatches(Map<String, Object> params) {
        params.put("match_code", "social");
        return matchDAO.selectFilteredMatches(params);
    }

    // 📌 리그 매치 목록 조회
    public List<Match> getLeagueMatches(Map<String, Object> params) {
        params.put("match_code", "league");
        return matchDAO.selectFilteredMatches(params);
    }

    // 📌 특정 매치 상세 조회
    public Match getMatchDetail(Long matchId) {
        return matchDAO.selectMatchDetailById(matchId);
    }

    // 📌 매치 참가 신청 처리
    public void applyToMatch(MatchParticipant participant) {
        Long matchId = participant.getMatch_id();
        Match match = matchDAO.selectMatchDetailById(matchId);
        String matchCode = match.getMatch_code();
        int currentCount = matchDAO.countMatchParticipants(matchId);

        if ("social".equalsIgnoreCase(matchCode) && currentCount >= 18) {
            throw new IllegalStateException("소셜 매치는 최대 18명까지만 참가할 수 있습니다.");
        }
        if ("league".equalsIgnoreCase(matchCode) && currentCount >= 3) {
            throw new IllegalStateException("리그 매치는 최대 3팀까지만 참가할 수 있습니다.");
        }

        log.info("참가자 정보: {}", participant);
        matchDAO.insertMatchParticipant(participant);

        // 알림 전송
        try {
            Integer writerUserNo = match.getUser_no();
            String applicantName = "";
            List<Map<String, Object>> participants = matchDAO.selectParticipantsByMatchId(matchId);
            for (Map<String, Object> p : participants) {
                if (p.get("user_no").equals(participant.getUser_no())) {
                    applicantName = (String) p.get("user_name");
                    break;
                }
            }

            MatchAlarmMessageDTO alarm = new MatchAlarmMessageDTO();
            alarm.setType("MATCH_APPLY");
            alarm.setSenderId(String.valueOf(participant.getUser_no()));
            alarm.setReceiverId(String.valueOf(writerUserNo));
            alarm.setMatchId(matchId);
            alarm.setMatchTitle(match.getMatch_title());
            alarm.setMessage(applicantName + "님이 [" + match.getMatch_title() + "] 경기에 참가 신청했습니다.");
            alarm.setUrl("/match/" + matchId);

            restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
        } catch (Exception e) {
            log.warn("알림 전송 실패: {}", e.getMessage());
        }
    }

    public boolean hasUserApplied(Long matchId, Integer userNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", matchId);
        param.put("user_no", userNo);
        return matchDAO.checkUserApplied(param) > 0;
    }

    public void cancelMatchParticipant(Long matchId, Integer userNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", matchId);
        param.put("user_no", userNo);
        matchDAO.cancelMatchParticipant(param);
    }

    public int getMatchParticipantCount(Long matchId) {
        return matchDAO.countMatchParticipants(matchId);
    }

    public Map<String, Object> getClubByUserNo(Long userNo) {
        return matchDAO.selectClubByUserNo(userNo);
    }

    public List<String> getAllAreanms() {
        return matchDAO.selectDistinctAreanms();
    }

    public List<Map<String, Object>> getMatchParticipantsWithNames(Long matchId) {
        return matchDAO.selectParticipantsByMatchId(matchId);
    }

    public List<Map<String, Object>> selectParticipantsWithClubByMatchId(Long matchId) {
        return matchDAO.selectParticipantsWithClubByMatchId(matchId);
    }

    // 📌 참가 상태 변경 및 알림/마감 상태 갱신
    public int updateMatchParticipantStatus(Map<String, Object> param) {
        int result = matchDAO.updateMatchParticipantStatus(param);

        Long matchId = Long.valueOf(param.get("match_id").toString());
        Integer userNo = Integer.valueOf(param.get("user_no").toString());
        String userStatus = param.get("user_status").toString();

        Match match = matchDAO.selectMatchDetailById(matchId);
        String matchCode = match.getMatch_code();
        int currentCount = matchDAO.countMatchParticipants(matchId);
        String currentClosed = match.getMatch_closed();
        String newClosed = null;

        if ("social".equalsIgnoreCase(matchCode)) {
            newClosed = currentCount >= 18 ? "closed" : "active";
        } else if ("league".equalsIgnoreCase(matchCode)) {
            newClosed = currentCount >= 3 ? "closed" : "active";
        }

        if (newClosed != null && !newClosed.equalsIgnoreCase(currentClosed)) {
            Map<String, Object> closeParam = new HashMap<>();
            closeParam.put("match_id", matchId);
            closeParam.put("match_closed", newClosed);
            matchDAO.updateMatchClosedStatus(closeParam);
        }

        websoket.matchWebsocket(userStatus, match, userNo, matchId);
        return result;
    }

    public void closeMatch(Long matchId) {
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", matchId);
        param.put("match_closed", "closed");
        matchDAO.updateMatchClosedStatus(param);
    }

    @Scheduled(cron = "0 0 */2 * * *", zone = "Asia/Seoul")
    public void activatePastMatches() {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(seoulZone);
        LocalDate today = now.toLocalDate();

        List<Match> allMatches = matchDAO.selectFilteredMatches(new HashMap<>());

        for (Match match : allMatches) {
            LocalDateTime matchDateTime = match.getMatch_date();
            String status = match.getMatch_status();

            if ("waiting".equalsIgnoreCase(status) && !matchDateTime.toLocalDate().isAfter(today)) {
                Map<String, Object> param = new HashMap<>();
                param.put("match_id", match.getMatch_id());
                param.put("match_status", "active");
                matchDAO.updateMatchStatus(param);
            } else if ("active".equalsIgnoreCase(status) && matchDateTime.plusHours(2).isBefore(now)) {
                Map<String, Object> param = new HashMap<>();
                param.put("match_id", match.getMatch_id());
                param.put("match_status", "completed");
                matchDAO.updateMatchStatus(param);
            }
        }
    }

    public Long getReservationIdByBoardId(Long boardId) {
        if (boardId == null) throw new IllegalArgumentException("board_id는 null일 수 없습니다.");
        return matchDAO.getReservationIdByBoardId(boardId);
    }

    public boolean isReservationPaid(Long reservationId) {
        if (reservationId == null) throw new IllegalArgumentException("reservation_id는 null일 수 없습니다.");
        return matchDAO.isReservationPaid(reservationId);
    }

    public void registerMatch(Match match) {
        log.info("💾 매치 등록 요청: {}", match);
        matchDAO.insertMatch(match);
    }

    public List<Map<String, Object>> getFilteredClubMatches(Long clubId) {
        if (clubId == null) throw new IllegalArgumentException("club_id는 null일 수 없습니다.");
        return matchDAO.selectFilteredClubMatches(clubId);
    }

    public void applyAndApproveImmediately(MatchParticipant participant) {
        matchDAO.insertMatchParticipant(participant);
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
        } else if ("payment".equalsIgnoreCase(type)) {
            reservationId = matchDAO.getReservationIdByPaymentId(id);
            if (reservationId == null)
                throw new IllegalArgumentException("해당 payment_id에 대한 예약이 없습니다: " + id);
        } else {
            throw new IllegalArgumentException("유효하지 않은 type 값: " + type);
        }

        List<Match> matches = matchDAO.getMatchesByReservationId(reservationId);

        int updatedCount = 0;
        for (Match match : matches) {
            String status = match.getMatch_status();
            if ("waiting".equalsIgnoreCase(status) || "active".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status)) {
                Map<String, Object> param = new HashMap<>();
                param.put("match_id", match.getMatch_id());
                param.put("match_status", "cancelled");

                int result = matchDAO.updateMatchStatus(param);
                if (result > 0) {
                    updatedCount++;
                }
            }
        }

        log.info("📊 상태 변경된 매치 수: {}", updatedCount);
    }

    public List<Match> getRecentCompletedMatches() {
        return matchDAO.selectRecentCompletedMatches();
    }

    public List<Match> getClosedLeagueMatches() {
        return matchDAO.selectClosedLeagueMatches();
    }
}
