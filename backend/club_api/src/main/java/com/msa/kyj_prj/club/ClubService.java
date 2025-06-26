package com.msa.kyj_prj.club;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.msa.kyj_prj.club.member.ClubMember;
import com.msa.kyj_prj.club.member.ClubMemberDAO;

@Service
public class ClubService {

	@Autowired
	private ClubDAO clubDAO;

	@Autowired
	private ClubMemberDAO clubMemberDAO; // ✅ 클럽 멤버 DAO 주입

	// 클럽 단건 조회 (by clubId)
	public Club getClub(Integer clubId) {
		return clubDAO.getClub(clubId);
	}

	// ✅ 클럽 단건 조회 (by teamCode)
	public Club getClubByTeamCode(String teamCode) {
		return clubDAO.getClubByTeamCode(teamCode);
	}

	public Club findByTeamCode(String teamCode) {
		return getClubByTeamCode(teamCode);
	}

	
	// ✅ 클럽 등록 + 팀장 자동 등록
	public int insert(Club club) {
	    int result = clubDAO.insert(club); // 1. 클럽 생성

	    Integer clubId = club.getClub_id();   // 2. 생성된 club_id (useGeneratedKeys로 주입됨)
	    Integer userNo = club.getUser_no();   // ✅ 클럽 생성자 user_no 가져오기

	    // 3. 팀장을 club_member 테이블에 자동 등록
	    ClubMember leader = new ClubMember();
	    leader.setClub_id(clubId);
	    leader.setUser_no(userNo);           // ✅ 기존 user_id → user_no로 수정
	    leader.setRole("LEADER");            // 팀장 역할 지정

	    clubMemberDAO.insert(leader);        // 4. 클럽 멤버 등록

	    return result;
	}



	// 클럽 이름으로 단건 조회
	public Club findByName(String name) {
		return clubDAO.getClubByName(name);
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
