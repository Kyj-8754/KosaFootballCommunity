package com.msa.kyj_prj.recruit;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitService {

	private final RecruitDAO recruit_dao;

	// 전체 모집글 목록 조회 (최신순)
	public List<RecruitBoard> get_all_recruits() {
		return recruit_dao.find_all();
	}
	
	// 기존: 인기순 정렬 (파라미터 없음)
	public List<RecruitBoard> get_recruits_order_by_view_count() {
	    return recruit_dao.find_all_order_by_view_count();
	}

	// 추가: 인기순 정렬 + 검색어
	public List<RecruitBoard> get_recruits_order_by_view_count(String keyword) {
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return recruit_dao.find_all_order_by_view_count();
	    } else {
	        return recruit_dao.find_by_keyword_order_by_view_count(keyword);
	    }
	}
	// 전체 모집글 목록 조회 (최신순 + 검색어)
	public List<RecruitBoard> get_all_recruits(String keyword) {
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return recruit_dao.find_all();
	    } else {
	        return recruit_dao.find_by_keyword(keyword);
	    }
	}



	// 클럽별 모집글 목록 조회
	public List<RecruitBoard> get_recruits_by_club(int club_id) {
		return recruit_dao.find_by_club_id(club_id);
	}

	// 모집글 단건 조회
	public RecruitBoard get_recruit(int bno) {
		return recruit_dao.find_by_id(bno);
	}

	// 모집글 등록
	public void create_recruit(RecruitBoard recruit) {
	    recruit_dao.insert(recruit);
	}



	// 모집글 수정
	public void update_recruit(RecruitBoard board) {
		recruit_dao.update(board);
	}

//	// 모집글 삭제
//	public void delete_recruit(int bno) {
//		recruit_dao.delete(bno);
//	}

//	// 팀장 여부 확인
//	public boolean is_club_leader(int user_no, int club_id) {
//	    return recruit_dao.is_club_leader(user_no, club_id) > 0;
//	}

//	public boolean is_club_leader(int userNo, int clubId) {
//	    Map<String, Object> map = new HashMap<>();
//	    map.put("user_no", userNo);
//	    map.put("club_id", clubId);
//
//	    int result = recruitDAO.is_club_leader(map);
//	    return result > 0; // ✅ int → boolean 변환
//	}


	// 조회수 증가
	public void increase_view_count(int bno) {
		recruit_dao.increase_view_count(bno);
	}
}
