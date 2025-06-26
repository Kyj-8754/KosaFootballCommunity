package com.msa.kyj_prj.club;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClubDAO {

	// 클럽 단건 조회 (by clubId)
	Club getClub(Integer clubId);

	// ✅ 클럽 단건 조회 (by teamCode)
	Club getClubByTeamCode(String teamCode);
	
	Club getClubByName(String name);

	// 클럽 등록
	int insert(Club club);

	// 클럽 리스트 (검색, 정렬, 페이징 포함)
	List<Club> list(Map<String, Object> params);

	// 클럽 전체 개수
	int getTotalCount(Map<String, Object> params);
}
