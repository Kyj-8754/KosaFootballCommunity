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

    // 📌 소셜 매치 목록 조회 (match_code = "social")
    public List<Match> getSocialMatches(Map<String, Object> params) {
        params.put("match_code", "social");
        return matchDAO.selectFilteredMatches(params);
    }

    // 📌 리그 매치 목록 조회 (match_code = "league")
    public List<Match> getLeagueMatches(Map<String, Object> params) {
        params.put("match_code", "league");
        return matchDAO.selectFilteredMatches(params);
    }

    // 📌 특정 매치의 상세 정보 조회
    public Match getMatchDetail(Long matchId) {
        return matchDAO.selectMatchDetailById(matchId);
    }

    // 📌 매치 참가 신청 처리 (정원 초과 방지 + 알림 전송 포함)
    public void applyToMatch(MatchParticipant participant) {
    	
    	
        Long matchId = participant.getMatch_id();

        // 무슨 매치인지 가져오는 로직
        Match match = matchDAO.selectMatchDetailById(matchId);
        String matchCode = match.getMatch_code();
        
        // 참가자가 지금 몇명인지 확인
        int currentCount = matchDAO.countMatchParticipants(matchId);

        // 정원 초과 체크
        if ("social".equalsIgnoreCase(matchCode) && currentCount >= 18) {
            throw new IllegalStateException("소셜 매치는 최대 18명까지만 참가할 수 있습니다.");
        }
        if ("league".equalsIgnoreCase(matchCode) && currentCount >= 3) {
            throw new IllegalStateException("리그 매치는 최대 3팀까지만 참가할 수 있습니다.");
        }

        log.info("참가자 정보" + participant.toString());
        // 참가자 등록
        matchDAO.insertMatchParticipant(participant);

        // 글 작성자에게 참가 신청 알림 전송
        try {
            Integer WriterUserNo = match.getUser_no();
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
            alarm.setReceiverId(String.valueOf(WriterUserNo));
            alarm.setMatchId(matchId);
            alarm.setMatchTitle(match.getMatch_title());
            alarm.setMessage(applicantName + "님이 [" + match.getMatch_title() + "] 경기에 참가 신청했습니다.");
            alarm.setUrl("/match/" + matchId);

            restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
        } catch (Exception e) {
            // 알림 실패 시 무시
        }
    }

    // 📌 해당 매치의 현재 참가자 수 조회
    public int getMatchParticipantCount(Long matchId) {
        return matchDAO.countMatchParticipants(matchId);
    }

    // 📌 특정 유저의 소속 클럽 정보 조회
    public Map<String, Object> getClubByUserNo(Long userNo) {
        return matchDAO.selectClubByUserNo(userNo);
    }

    // 📌 전체 지역명 목록(중복 제거) 조회
    public List<String> getAllAreanms() {
        return matchDAO.selectDistinctAreanms();
    }

    // 📌 매치 참가자 목록 + 이름 포함 조회
    public List<Map<String, Object>> getMatchParticipantsWithNames(Long matchId) {
        return matchDAO.selectParticipantsByMatchId(matchId);
    }

    // 📌 매치 참가자 + 클럽 정보 함께 조회
    public List<Map<String, Object>> selectParticipantsWithClubByMatchId(Long matchId) {
        return matchDAO.selectParticipantsWithClubByMatchId(matchId);
    }

//    // 📌 참가자의 상태를 변경하고, 필요시 match_closed 상태도 변경
//    public int updateMatchParticipantStatus(Map<String, Object> param) {
//        int result = matchDAO.updateMatchParticipantStatus(param);
//
//        Long matchId = Long.valueOf(param.get("match_id").toString());
//        Match match = matchDAO.selectMatchDetailById(matchId);
//        String matchCode = match.getMatch_code();
//        int currentCount = matchDAO.countMatchParticipants(matchId);
//
//        String currentClosed = match.getMatch_closed();
//        String newClosed = null;
//
//        // 정원 도달 여부에 따라 match_closed 상태 갱신
//        if ("social".equalsIgnoreCase(matchCode)) {
//            newClosed = currentCount >= 18 ? "closed" : "active";
//        } else if ("league".equalsIgnoreCase(matchCode)) {
//            newClosed = currentCount >= 3 ? "closed" : "active";
//        }
//
//        if (newClosed != null && !newClosed.equalsIgnoreCase(currentClosed)) {
//            Map<String, Object> closeParam = new HashMap<>();
//            closeParam.put("match_id", matchId);
//            closeParam.put("match_closed", newClosed);
//            matchDAO.updateMatchClosedStatus(closeParam);
//        }
//
//        return result;
//    }
    
 // 참가자의 상태를 변경하고, 필요시 match_closed 상태도 변경 및 승인/거절 알림 전송
    public int updateMatchParticipantStatus(Map<String, Object> param) {
        
    	// DB에 상태 저장(승인/거절)
    	int result = matchDAO.updateMatchParticipantStatus(param);

    	
        Long matchId = Long.valueOf(param.get("match_id").toString());
        Integer userNo = Integer.valueOf(param.get("user_no").toString());
        String userStatus = param.get("user_status").toString();

        // 해당 매치 가져오는 로직
        Match match = matchDAO.selectMatchDetailById(matchId);
        
        String matchCode = match.getMatch_code();
        int currentCount = matchDAO.countMatchParticipants(matchId);

        String currentClosed = match.getMatch_closed();
        String newClosed = null;

        // 정원 도달 여부에 따라 match_closed 상태 갱신 (기존 로직 유지)
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
        
        
        // 내가 만든 웹소켓 불러오는 코드
        websoket.matchWebsocket(userStatus, match, userNo, matchId);
        return result;
    }

    

    // 📌 특정 매치를 수동으로 종료 처리
    public void closeMatch(Long matchId) {
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", matchId);
        param.put("match_closed", "closed");
        matchDAO.updateMatchClosedStatus(param);
    }

    // ⏰ 스케줄링: 2시간마다 매치 상태 자동 변경 (waiting → active, active → completed)
    @Scheduled(cron = "0 0 */2 * * *")
    public void activatePastMatches() {
        List<Match> allMatches = matchDAO.selectFilteredMatches(new HashMap<>());
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        for (Match match : allMatches) {
            LocalDateTime matchDateTime = match.getMatch_date();
            String status = match.getMatch_status();

            if ("waiting".equalsIgnoreCase(status) && !matchDateTime.toLocalDate().isAfter(today)) {
                // 오늘 이후가 아닌 waiting 매치 → active
                Map<String, Object> param = new HashMap<>();
                param.put("match_id", match.getMatch_id());
                param.put("match_status", "active");
                matchDAO.updateMatchStatus(param);
            } else if ("active".equalsIgnoreCase(status) && matchDateTime.plusHours(2).isBefore(now)) {
                // 시작 후 2시간 경과 시 → completed
                Map<String, Object> param = new HashMap<>();
                param.put("match_id", match.getMatch_id());
                param.put("match_status", "completed");
                matchDAO.updateMatchStatus(param);
            }
        }
    }

    // 📌 유저가 특정 매치에 이미 신청했는지 여부 확인
    public boolean hasUserApplied(Long matchId, Integer userNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", matchId);
        param.put("user_no", userNo);
        return matchDAO.checkUserApplied(param) > 0;
    }

    // 📌 유저가 참가 신청한 매치를 취소 처리
    public void cancelMatchParticipant(Long matchId, Integer userNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("match_id", matchId);
        param.put("user_no", userNo);
        matchDAO.cancelMatchParticipant(param);
    }
}
