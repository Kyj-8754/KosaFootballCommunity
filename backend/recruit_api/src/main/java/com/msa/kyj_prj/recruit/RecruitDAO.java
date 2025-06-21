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

	// 모집글 삭제
	void delete(@Param("bno") int bno);

	// 팀장 여부 확인
	int is_club_leader(@Param("user_id") String user_id, @Param("club_id") int club_id);

	// 조회수 증가
	void increase_view_count(@Param("bno") int bno);
}
