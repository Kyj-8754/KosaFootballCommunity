package com.msa.kyj_prj.club.apply;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClubApplyDAO {

    // 지원서 등록
    int insert(ClubApply clubApply);

    // 모집글(bno)별 신청 목록 조회
    List<ClubApply> findByBno(int bno);

    // 지원 상태 변경 (예: approved, rejected)
    int updateStatus(Map<String, Object> param);

    // 특정 모집글에 이미 신청한 사용자인지 확인 (중복 방지용)
    int findByBnoAndApplicant(Map<String, Object> param);


}
