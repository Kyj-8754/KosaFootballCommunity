package com.msa.kyj_prj.recruit;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecruitDAO {

	// 클럽별 모집글 목록 조회
	List<RecruitBoard> find_by_club_id(@Param("club_id") int club_id);

	// 모집글 단건 조회
	RecruitBoard find_by_id(@Param("bno") int bno);

	// 모집글 등록
	void insert(RecruitBoard board);

	// 전체 모집글 목록
	List<RecruitBoard> find_all();

	// 조회수 기준 인기순 정렬
	List<RecruitBoard> find_all_order_by_view_count();

	// 모집글 수정
	void update(RecruitBoard board);
//
//	// 모집글 삭제
//	void delete(@Param("bno") int bno);

	// 팀장인지 아닌지 검사 로직
//	int is_club_leader(@Param("user_no") int user_no, @Param("club_id") int club_id);

	 // user_no로 본인이 리더인 클럽의 club_id 조회
    
 // user_no로 리더 클럽 전체 조회
    List<Integer> findClubIdsByLeaderUserNo(int user_no);


	// 조회수 증가
	void increase_view_count(@Param("bno") int bno);
}
