package com.msa.kyj_prj.club;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClubDAO {

	// 클럽 단건 조회 (by clubId)
	Club getClub(Integer clubId);

	// ✅ 클럽 단건 조회 (by teamCode)
	Club getClubByTeamCode(String teamCode);

	Club getClubByName(String name);

	Integer getClubIdByTeamCode(String teamCode);

	// 클럽 등록
	int insert(Club club);

	// 클럽 리스트 (검색, 정렬, 페이징 포함)
	List<Club> list(Map<String, Object> params);

	// 클럽 전체 개수
	int getTotalCount(Map<String, Object> params);

	// 1개의 클럽 조회
	Club findClubByUserNo(int userNo);

	// 여러개의 클럽 조회
	List<Club> findClubsByUserNo(int userNo);

	// 클럽 정보 수정
	int update(Club club);

	// 클럽 레벨 전적을 바탕으로 업데이트
	int updateClubLevel(@Param("clubId") int clubId, @Param("clubLevel") String clubLevel);	
	// 클럽 로고 경로 업데이트
	int updateLogoPath(@Param("clubId") int clubId, @Param("logoPath") String logoPath);
	// 클럼 리더 위임
	int updateLeader(@Param("club_id") int club_id, @Param("user_no") int user_no);
	


}
