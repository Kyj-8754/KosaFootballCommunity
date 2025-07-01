package com.msa.kyj_prj.match;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MatchDAO {

	// 매치 목록 검색
    List<Match> selectFilteredMatches(Map<String, Object> params);

    // 매치 상세내용 호출
    Match selectMatchDetailById(@Param("match_id") Long match_id);
    
    // 매치 신청
    void insertMatchParticipant(MatchParticipant participant);
    
    // 매치 신청 여부 확인
    int checkUserApplied(Map<String, Object> param);
    
    // 매치 신청 취소
    int cancelMatchParticipant(Map<String, Object> param);
    
    // 매치 인원 조회
    int countMatchParticipants(Long matchId);
    
    // 유저 번호로 클럽 정보 조회
    Map<String, Object> selectClubByUserNo(@Param("user_no") Long user_no);
}
