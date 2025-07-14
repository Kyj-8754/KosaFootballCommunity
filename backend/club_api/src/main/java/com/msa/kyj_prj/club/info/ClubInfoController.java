package com.msa.kyj_prj.club.info;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 클럽 상세 정보 API 컨트롤러
 */
@RestController
@RequestMapping("/club_info")
public class ClubInfoController {

    @Autowired
    private ClubInfoService clubInfoService;

    // ✅ 클럽 상세 정보 단건 조회 (by club_id)
    @GetMapping("/{club_id}")
    public ClubInfo getClubInfo(@PathVariable Integer club_id) {
        return clubInfoService.getClubInfo(club_id);
    }

    // ✅ 클럽 상세 정보 등록
    @PostMapping("")
    public int insertClubInfo(@RequestBody ClubInfo clubInfo) {
        return clubInfoService.insertClubInfo(clubInfo);
    }

    // ✅ 클럽 상세 정보 수정
    @PutMapping("/{club_id}")
    public int updateClubInfo(@PathVariable Integer club_id, @RequestBody ClubInfo clubInfo) {
        clubInfo.setClub_id(club_id);
        return clubInfoService.updateClubInfo(clubInfo);
    }

    // (선택) 전체 클럽 info 리스트 조회 (관리/테스트용)
    @GetMapping("/list")
    public List<ClubInfo> listClubInfo() {
        return clubInfoService.listClubInfo();
    }

    // (선택) 클럽 상세 정보 삭제
    @DeleteMapping("/{club_id}")
    public int deleteClubInfo(@PathVariable Integer club_id) {
        return clubInfoService.deleteClubInfo(club_id);
    }
}
