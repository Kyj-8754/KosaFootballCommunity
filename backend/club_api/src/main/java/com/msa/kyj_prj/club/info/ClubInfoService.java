package com.msa.kyj_prj.club.info;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClubInfoService {

	@Autowired
	private ClubInfoDAO clubInfoDAO;

	// 클럽 상세정보 단건 조회
	public ClubInfo getClubInfo(Integer club_id) {
		return clubInfoDAO.getClubInfo(club_id);
	}

	// 클럽 상세정보 등록
	public int insertClubInfo(ClubInfo clubInfo) {
		return clubInfoDAO.insertClubInfo(clubInfo);
	}

	// 클럽 상세정보 수정
	public int upsertClubInfo(ClubInfo clubInfo) {
		ClubInfo existing = clubInfoDAO.getClubInfo(clubInfo.getClub_id());

		if (existing == null) {
			return clubInfoDAO.insertClubInfo(clubInfo);
		} else {
			return clubInfoDAO.updateClubInfo(clubInfo);
		}
	}

	// 클럽 상세정보 수정 (업서트 포함)
    // ✅ 클럽 상세정보 수정 (업서트 처리)
	public int updateClubInfo(ClubInfo clubInfo) {
	    // ✅ 필수 값 검증
	    if (clubInfo.getGender() == null || clubInfo.getGender().isBlank()
	        || clubInfo.getAge_group() == null || clubInfo.getAge_group().isBlank()
	        || clubInfo.getActive_days() == null || clubInfo.getActive_days().isBlank()
	        || clubInfo.getActive_times() == null || clubInfo.getActive_times().isBlank()) {

	        throw new IllegalArgumentException("성별, 나이대, 활동 요일, 활동 시간을 모두 입력해 주세요.");
	    }

	    ClubInfo existing = clubInfoDAO.getClubInfo(clubInfo.getClub_id());

	    if (existing == null) {
	        return clubInfoDAO.insertClubInfo(clubInfo);
	    } else {
	        return clubInfoDAO.updateClubInfo(clubInfo);
	    }
	}
	// 전체 클럽 info 리스트(관리/테스트용)
	public List<ClubInfo> listClubInfo() {
		return clubInfoDAO.listClubInfo();
	}

	// 삭제 (필요시)
	public int deleteClubInfo(Integer club_id) {
		return clubInfoDAO.deleteClubInfo(club_id);
	}
}
