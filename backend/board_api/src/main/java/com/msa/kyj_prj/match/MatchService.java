package com.msa.kyj_prj.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MatchService {

    @Autowired
    private MatchDAO matchDAO;

    // 소셜매치 목록 호출
    public List<Match> getSocialMatches(Map<String, Object> params) {
        params.put("match_code", "social");
        System.out.println("params = " + params);
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
}
