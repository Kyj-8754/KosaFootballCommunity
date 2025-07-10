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
        ClubInfo existing = clubInfoDAO.getClubInfo(clubInfo.getClub_id());

        if (existing == null) {
            return clubInfoDAO.insertClubInfo(clubInfo);  // INSERT 실행 (MyBatis XML에 있음)
        } else {
            return clubInfoDAO.updateClubInfo(clubInfo);  // UPDATE 실행 (MyBatis XML에 있음)
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
