package com.msa.kyj_prj.match.log;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MatchLogDAO {
    // 특정 매치의 로그 전체 조회
    List<MatchLog> selectLogsByMatchId(@Param("match_id") Long match_id);

    // 로그 등록
    int insertMatchLog(MatchLog log);

    // 로그 수정
    int updateMatchLog(MatchLog log);

    // 로그 삭제
    int deleteMatchLog(@Param("log_id") Long log_id);

    // 승인된 참가자(user_no) 리스트 조회
    List<Map<String, Object>> selectApprovedUsersByMatchId(@Param("match_id") Long match_id);
    
    // 승인된 팀 리스트 조회
    List<Map<String, Object>> selectApprovedTeamsByMatchId(@Param("match_id") Long match_id);
    
    // log_type이 POM인 로그만 조회
    List<MatchLog> selectPomLogsByMatchId(@Param("match_id") Long match_id);
}
