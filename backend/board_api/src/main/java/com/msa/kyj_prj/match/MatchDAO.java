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
}
