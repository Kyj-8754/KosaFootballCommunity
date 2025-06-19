package com.msa.kyj_prj.club;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClubService {

	@Autowired
	private ClubDAO clubDAO;

	// 클럽 단건 조회
	public Club getClub(Integer clubId) {
	    return clubDAO.getClub(clubId);
	}

	// 클럽 등록
	public int insert(Club club) {
		return clubDAO.insert(club);
	}

	// 클럽 수정
	public int update(Club club) {
		return clubDAO.update(club);
	}

	// 클럽 삭제
	public int delete(int clubId) {
		return clubDAO.delete(clubId);
	}

	// 클럽 목록 조회 (검색 + 페이징 + 정렬 포함)
	public List<Club> list(Map<String, Object> params) {
		return clubDAO.list(params);
	}

	// 클럽 전체 수 (검색 포함)
	public int getTotalCount(Map<String, Object> params) {
		return clubDAO.getTotalCount(params);
	}
}
