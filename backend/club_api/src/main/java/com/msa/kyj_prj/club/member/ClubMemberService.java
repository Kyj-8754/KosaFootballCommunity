package com.msa.kyj_prj.club.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.msa.kyj_prj.club.ClubDAO;

@Service
@Transactional
public class ClubMemberService {

	@Autowired
	private ClubMemberDAO clubMemberDAO;

	// 클럽 멤버 등록
	public int insert(ClubMember club_member) {
		return clubMemberDAO.insert(club_member);
	}

	// 클럽별 전체 멤버 리스트 조회
	public List<ClubMember> findByClubId(int club_id) {
		return clubMemberDAO.findByClubId(club_id);
	}

	// 특정 클럽의 특정 유저 멤버 정보 조회
	public ClubMember findByClubIdAndUserNo(int club_id, int user_no) {
		return clubMemberDAO.findByClubIdAndUserNo(club_id, user_no);
	}

	// 클럽 멤버 탈퇴(논리삭제)
	public int deleteByClubIdAndUserNo(int club_id, int user_no) {
		ClubMember member = clubMemberDAO.findByClubIdAndUserNo(club_id, user_no);
		if (member != null && "LEADER".equals(member.getRole())) {
			throw new IllegalStateException("팀장은 클럽을 탈퇴할 수 없습니다. 리더 권한을 위임한 뒤 탈퇴해주세요.");
		}
		return clubMemberDAO.deleteByClubIdAndUserNo(club_id, user_no);
	}

	// 클럽 멤버 역할 변경
	public int updateRoleByClubIdAndUserNo(int club_id, int user_no, String role) {
		return clubMemberDAO.updateRoleByClubIdAndUserNo(club_id, user_no, role);
	}

	@Autowired // 클럽 리더 위임
	private ClubDAO clubDAO;
	
	public void delegateLeader(int club_id, int new_leader_user_no, int old_leader_user_no) {
		// 1. club 테이블 리더(user_no) 변경
		clubDAO.updateLeader(club_id, new_leader_user_no);

		// 2. 기존 리더를 MEMBER로 변경
		clubMemberDAO.updateRoleByClubIdAndUserNo(club_id, old_leader_user_no, "MEMBER");

		// 3. 새로운 리더를 LEADER로 변경
		clubMemberDAO.updateRoleByClubIdAndUserNo(club_id, new_leader_user_no, "LEADER");
	}
}
