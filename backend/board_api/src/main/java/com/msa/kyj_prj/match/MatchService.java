package com.msa.kyj_prj.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


}
