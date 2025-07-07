package com.msa.kyj_prj.match.log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchLogService {

    @Autowired
    private MatchLogDAO matchLogDAO;

    // 특정 매치의 로그 전체 조회
    public List<MatchLog> getLogsByMatchId(Long match_id) {
        return matchLogDAO.selectLogsByMatchId(match_id);
    }

    // 로그 등록
    public int addMatchLog(MatchLog log) {
        return matchLogDAO.insertMatchLog(log);
    }

    // 로그 수정
    public int updateMatchLog(MatchLog log) {
        return matchLogDAO.updateMatchLog(log);
    }

    // 로그 삭제
    public int deleteMatchLog(Long log_id) {
        return matchLogDAO.deleteMatchLog(log_id);
    }
    
    // 승인된 참가자(user_no) 리스트 조회
    public List<Map<String, Object>> getApprovedUsers(Long match_id) {
        return matchLogDAO.selectApprovedUsersByMatchId(match_id);
    }
    // 승인된 팀 리스트 조회
    public List<Map<String, Object>> getApprovedTeamsByMatchId(Long match_id) {
        return matchLogDAO.selectApprovedTeamsByMatchId(match_id);
    }
    
    // 결과 보이게끔
    public List<List<MatchLog>> splitLogsByMatchSet(List<MatchLog> logs) {
        logs.sort(Comparator.comparing(MatchLog::getLog_created_at));

        List<List<MatchLog>> result = new ArrayList<>();
        List<MatchLog> currentSet = null;
        boolean inMatch = false;
        boolean waitingForResult = false;

        for (MatchLog log : logs) {
            String type = log.getLog_type();

            if ("경기 참가".equals(type)) {
                if (!inMatch) {
                    currentSet = new ArrayList<>();
                    result.add(currentSet);
                    inMatch = true;
                }
                currentSet.add(log);

            } else if ("경기 시작".equals(type)) {
                if (!inMatch || currentSet == null) {
                    currentSet = new ArrayList<>();
                    result.add(currentSet);
                    inMatch = true;
                }
                currentSet.add(log);

            } else if ("경기 종료".equals(type)) {
                if (currentSet != null) {
                    currentSet.add(log);
                    waitingForResult = true; // 승패 로그 기다림
                }

            } else if ("승리".equals(type) || "패배".equals(type) || "무승부".equals(type)) {
                if (waitingForResult && currentSet != null) {
                    currentSet.add(log);
                    // 2개까지만 넣고 세트 종료
                    long count = currentSet.stream().filter(l ->
                            "승리".equals(l.getLog_type()) ||
                            "패배".equals(l.getLog_type()) ||
                            "무승부".equals(l.getLog_type())
                    ).count();

                    if (count >= 2) {
                        currentSet = null;
                        inMatch = false;
                        waitingForResult = false;
                    }
                }

            } else {
                if (currentSet != null) {
                    currentSet.add(log);
                }
            }
        }

        return result;
    }

    // log_type이 'POM'인 로그만 조회
    public List<MatchLog> getPomLogsByMatchId(Long match_id) {
        return matchLogDAO.selectPomLogsByMatchId(match_id);
    }

}
