package com.msa.kyj_prj.club.apply;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClubApplyDAO {

    // 지원서 등록
    int insert(ClubApply clubApply);
    
    // 2. bno로 club_id 조회
    Integer findClubIdByBno(int bno);
    
    // 3. bno로 모집글의 팀장(user_no) 조회
    Integer findUserNoByBno(int bno);
    

    // 모집글(bno)별 신청 목록 조회
    List<ClubApply> findByBno(int bno);

    // 지원 상태 변경 (예: approved, rejected)
    int updateStatus(Map<String, Object> param);

    // 특정 모집글에 이미 신청한 사용자인지 확인 (중복 방지용)
    int findByBnoAndApplicant(@Param("bno") int bno, @Param("appli_user_no") int appli_user_no);


}
