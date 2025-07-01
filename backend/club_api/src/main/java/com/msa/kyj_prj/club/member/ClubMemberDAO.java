package com.msa.kyj_prj.club.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClubMemberDAO {

    // 클럽 멤버 등록
    int insert(ClubMember clubMember);

    // 1. 클럽별 전체 멤버 리스트 조회
    List<ClubMember> findByClubId(@Param("club_id") int club_id);

    // 2. (선택) 특정 유저가 가입한 클럽 멤버 정보 조회
    ClubMember findByClubIdAndUserNo(@Param("club_id") int club_id, @Param("user_no") int user_no);

    // 3. (선택) 클럽 멤버 탈퇴(삭제)
    int deleteByClubIdAndUserNo(@Param("club_id") int club_id, @Param("user_no") int user_no);

    int updateRoleByClubIdAndUserNo(
    	    @Param("club_id") int club_id, 
    	    @Param("user_no") int user_no, 
    	    @Param("role") String role
    	);


}
