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
		Integer userNo = club.getUser_no();

		// 1. 이미 클럽이 있는지 체크 (리더 기준)
		if (clubDAO.findClubByUserNo(userNo) != null) {
			throw new IllegalStateException("이미 해당 유저는 클럽을 보유하고 있습니다.");
		}

		int result = clubDAO.insert(club); // 1. 클럽 생성

		Integer clubId = club.getClub_id(); // 2. 생성된 club_id (useGeneratedKeys로 주입됨)
		// Integer userNo = club.getUser_no(); // ✅ 클럽 생성자 user_no 가져오기

		// 3. 팀장을 club_member 테이블에 자동 등록
		ClubMember leader = new ClubMember();
		leader.setClub_id(clubId);
		leader.setUser_no(userNo); // ✅ 기존 user_id → user_no로 수정
		leader.setRole("LEADER"); // 팀장 역할 지정
		leader.setPom(String.valueOf(0));           // ✅ int → String 변환
		leader.setMatch_count(String.valueOf(0));   // ✅ int → String 변환
		clubMemberDAO.insert(leader); // 4. 클럽 멤버 등록

		return result;
	}

	// ✅ userNo 기준으로 클럽이 존재하는지 여부 확인
	public boolean hasClubByUserNo(int userNo) {
		return clubDAO.findClubByUserNo(userNo) != null;
	}

	// userNo 기준으로 여러 클럽을 조회
	public List<Club> findClubsByUserNo(int userNo) {
		return clubDAO.findClubsByUserNo(userNo);
	}

	// ✅ club_id 1건 조회를 위한 서비스 메서드
	public Club findClubByUserNo(int userNo) {
		return clubDAO.findClubByUserNo(userNo);
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

	// ✅ 클럽 정보 수정 - club_id 기준으로 정보 업데이트
	public int updateClub(Club club) {
		// clubDAO의 update 메소드 호출 (업데이트 성공 시 1 반환)
		return clubDAO.update(club);
	}

	public String calculateClubLevel(int win, int draw, int loss) {
		int total = win + draw + loss;
		if (total == 0)
			return "브론즈";
		double rate = (double) win / total * 100;

		if (rate >= 90)
			return "다이아";
		if (rate >= 70)
			return "플래티넘";
		if (rate >= 50)
			return "골드";
		if (rate >= 30)
			return "실버";
		return "브론즈";
	}

	// 경기 결과 입력/수정 시 호출 (예)
	public void updateClubLevel(int clubId) {
		Club club = clubDAO.getClub(clubId);
		String clubLevel = calculateClubLevel(club.getWin_count(), club.getDraw_count(), club.getLoss_count());
		club.setClub_level(clubLevel);
		clubDAO.updateClubLevel(clubId, clubLevel); // update 쿼리 필요
	}
	
	public void updateLogoPath(int clubId, String logoPath) {
		clubDAO.updateLogoPath(clubId, logoPath);
	}
	
	

}
