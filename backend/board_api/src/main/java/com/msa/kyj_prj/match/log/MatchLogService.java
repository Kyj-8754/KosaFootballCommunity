package com.msa.kyj_prj.match.log;

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
}
