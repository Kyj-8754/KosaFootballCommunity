package com.msa.kyj_prj.club.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubMemberService {

    @Autowired
    private ClubMemberDAO clubMemberDAO;

    /**
     * 클럽 멤버 등록
     */
    public int insert(ClubMember club_member) {
        return clubMemberDAO.insert(club_member);
    }

    /**
     * 클럽별 전체 멤버 리스트 조회
     */
    public List<ClubMember> findByClubId(int club_id) {
        return clubMemberDAO.findByClubId(club_id);
    }

    /**
     * 특정 클럽의 특정 유저 멤버 정보 조회
     */
    public ClubMember findByClubIdAndUserNo(int club_id, int user_no) {
        return clubMemberDAO.findByClubIdAndUserNo(club_id, user_no);
    }

    /**
     * 클럽 멤버 탈퇴(삭제)
     */
    public int deleteByClubIdAndUserNo(int club_id, int user_no) {
        return clubMemberDAO.deleteByClubIdAndUserNo(club_id, user_no);
    }

    /**
     * 클럽 멤버 역할 변경
     */
    public int updateRoleByClubIdAndUserNo(int club_id, int user_no, String role) {
        return clubMemberDAO.updateRoleByClubIdAndUserNo(club_id, user_no, role);
    }
}
