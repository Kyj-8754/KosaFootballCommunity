package com.msa.kyj_prj.club.apply;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface ClubApplyDAO {

	// 지원서 등록
	int insert(ClubApply clubApply);

	// 모집글 번호(bno)로 club_id 조회
	Integer findClubIdByBno(int bno);

	// 모집글 번호(bno)로 팀장(user_no) 조회
	Integer findUserNoByBno(int bno);

	// 클럽 리더 user_no 조회
	Integer findLeaderUserNoByClubId(@Param("club_id") int club_id);

	// user_no로 user_name 조회 (단일 사용자)
	String findUserNameByUserNo(@Param("user_no") int user_no);

	
	// 모집글별 신청 목록 조회
	List<ClubApply> findByBno(int bno);

	// club_id로 전체 신청자 목록 조회
	List<ClubApply> findByClubId(@Param("club_id") int club_id);

	// 단건 신청 내역 조회 (apply_id)
	ClubApply findByApplyId(@Param("apply_id") int apply_id);

	// club_id로 신청자 이름 목록 조회 (join user)
	List<Map<String, Object>> findByClubIdWithUserName(int club_id);

	// 지원 상태 변경 (예: approved, rejected 등)
	int updateStatus(@Param("apply_id") int apply_id, @Param("status") String status);

	// 지원 상태 변경 (Map 파라미터 버전)
	int updateStatus(Map<String, Object> param);

	// 24시간 뒤 재신청 시 status를 pending으로 복원
	int updateStatusToPending(@Param("bno") int bno, @Param("appli_user_no") int appli_user_no);

	// 신청 취소 (Soft delete: 상태만 변경)
	int cancelByBnoAndApplicant(@Param("bno") int bno, @Param("appli_user_no") int appli_user_no);

	// 최근 24시간 이내 취소 이력 조회
	ClubApply findRecentCanceledApply(@Param("bno") int bno, @Param("appli_user_no") int appli_user_no);

	// 최신 한 건 조회 (status 관계없이)
	ClubApply findLastApplyByBnoAndApplicant(@Param("bno") int bno, @Param("appli_user_no") int appli_user_no);

	// 클럽 멤버 등록 (승인 시)
	int insertClubMember(@Param("club_id") int club_id, @Param("user_no") int user_no);

	// 특정 유저가 클럽에 approved 상태로 있는지 체크 (중복 가입 방지)
	int countApprovedMember(@Param("club_id") int club_id, @Param("appli_user_no") int appli_user_no);

	// 특정 유저가 현재 신청중(pending)인지 체크 (중복 신청 방지)
	int countPendingApply(@Param("club_id") int club_id, @Param("appli_user_no") int appli_user_no);
	
	// 탈퇴 처리(approved → withdrawn)
	int updateWithdrawnStatus(@Param("club_id") int club_id, @Param("appli_user_no") int appli_user_no);

	// 강퇴 처리 (approved → kicked)
	int updateKickedStatus(@Param("club_id") int club_id, @Param("appli_user_no") int appli_user_no);

}
