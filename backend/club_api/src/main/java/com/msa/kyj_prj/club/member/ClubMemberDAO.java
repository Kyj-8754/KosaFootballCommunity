package com.msa.kyj_prj.club.member;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClubMemberDAO {

    // 클럽 멤버 등록
    int insert(ClubMember clubMember);
    
    // 필요 시 더 추가: findByUserNo, findByClubId 등
}
