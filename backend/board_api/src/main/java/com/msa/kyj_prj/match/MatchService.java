package com.msa.kyj_prj.match;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.msa.kyj_prj.match.alarm.MatchAlarmMessageDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchDAO matchDAO;
    private final RestTemplate restTemplate;

    @Value("${alarm.api.url}")
    private String alarmApiUrl;

    // 소셜 매치 목록 조회
    public List<Match> getSocialMatches(Map<String, Object> params) {
        params.put("match_code", "social");
        return matchDAO.selectFilteredMatches(params);
    }

    // 리그 매치 목록 조회
    public List<Match> getLeagueMatches(Map<String, Object> params) {
        params.put("match_code", "league");
        return matchDAO.selectFilteredMatches(params);
    }

    // 매치 상세 조회
    public Match getMatchDetail(Long matchId) {
        return matchDAO.selectMatchDetailById(matchId);
    }

    // 매치 참가 신청
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

        matchDAO.insertMatchParticipant(participant);

        // 알림 전송
        try {
            Integer managerUserNo = match.getUser_no();
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
            alarm.setReceiverId(String.valueOf(managerUserNo));
            alarm.setMatchId(matchId);
            alarm.setMatchTitle(match.getMatch_title());
            alarm.setMessage(applicantName + "님이 [" + match.getMatch_title() + "] 경기에 참가 신청했습니다.");
            alarm.setUrl("/match/" + matchId);

            restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
        } catch (Exception e) {
            // 실패 시 무시
        }
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

    public int updateMatchParticipantStatus(Map<String, Object> param) {
        int result = matchDAO.updateMatchParticipantStatus(param);

        Long matchId = Long.valueOf(param.get("match_id").toString());
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

        return result;
    }

    public void closeMatch(Long matchId) {
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", matchId);
        param.put("match_closed", "closed");
        matchDAO.updateMatchClosedStatus(param);
    }

    @Scheduled(cron = "0 0 */2 * * *") // 매 2시간마다 실행
    public void activatePastMatches() {
        List<Match> allMatches = matchDAO.selectFilteredMatches(new HashMap<>());
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

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
}
