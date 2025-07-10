package com.msa.kyj_prj.club.info;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClubInfoDAO {

    // 클럽 info 단건 조회 (by club_id)
    ClubInfo getClubInfo(Integer club_id);

    // 클럽 info 등록 (insert)
    int insertClubInfo(ClubInfo clubInfo);

    // 클럽 info 수정 (update)
    int updateClubInfo(ClubInfo clubInfo);

    // (필요시) 전체 클럽 info 리스트 (예: 관리/테스트용)
    List<ClubInfo> listClubInfo();

    // (필요시) 클럽 info 삭제
    int deleteClubInfo(Integer club_id);
}
