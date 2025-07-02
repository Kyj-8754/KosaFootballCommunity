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
        return matchDAO.updateMatchParticipantStatus(param);
    }
}
