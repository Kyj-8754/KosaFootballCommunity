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

	// 신청 취소(Soft delete: 상태만 변경)
	int cancelByBnoAndApplicant(@Param("bno") int bno, @Param("appli_user_no") int appli_user_no);

	// 최근 24시간 이내 취소 이력 조회
	ClubApply findRecentCanceledApply(@Param("bno") int bno, @Param("appli_user_no") int appli_user_no);

	// 최신 한 건 조회 (status 관계없이)
	ClubApply findLastApplyByBnoAndApplicant(@Param("bno") int bno, @Param("appli_user_no") int appli_user_no);

	// 🔴 신규 추가: 24시간 뒤 재신청 시 status를 pending으로 복원
	int updateStatusToPending(@Param("bno") int bno, @Param("appli_user_no") int appli_user_no);

}
