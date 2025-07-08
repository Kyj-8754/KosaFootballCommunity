package com.msa.kyj_prj.match;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
    
    // 지역 목록 (중복 제거된 AREANM 리스트) 조회
    List<String> selectDistinctAreanms();
    
    // 특정 매치의 참가자 + 사용자 이름 조회
    List<Map<String, Object>> selectParticipantsByMatchId(@Param("matchId") Long matchId);
    
    // 특정 매치의 참가자 + 사용자 이름 + 클럽 명 조회
    List<Map<String, Object>> selectParticipantsWithClubByMatchId(@Param("matchId") Long matchId);
    
    // ✅ 매치 참가자의 상태(user_status) 업데이트
    int updateMatchParticipantStatus(Map<String, Object> param);
    
    // match_id 기준으로 match_closed 값 수정 (Map 기반)
    int updateMatchClosedStatus(Map<String, Object> param);
    
    // match_id 기준으로 match_closed 값 수정 (Map 기반)
    int updateMatchStatus(Map<String, Object> param);

	// 매치 목록 검색
    List<Match> selectAllMatches(Map<String, Object> params);

    // 임시 추가
    @Select("SELECT user_name FROM user WHERE user_no = #{userNo}")
    String findUserNameByUserNo(@Param("userNo") Integer userNo); // ✅ 바인딩 이름 지정

}
