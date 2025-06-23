package com.msa.kyj_prj.club;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;

    // ✅ [단건 조회] 팀 코드로 클럽 조회
    @GetMapping("/code/{teamCode}")
    public Club getClubByTeamCode(@PathVariable String teamCode) {
        return clubService.findByTeamCode(teamCode);
    }

    // ✅ [클럽 등록] 로그인된 유저의 세션에서 leader_user_id 주입
    @PostMapping
    public int insertClub(@RequestBody Club club, HttpSession session) {
        String loginUserId = (String) session.getAttribute("loginUserid");
        if (loginUserId == null || loginUserId.isEmpty()) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        club.setLeaderUserId(loginUserId); // 🔥 자동으로 팀장 설정
        return clubService.insert(club);
    }

    /*
    // 🔧 Postman 테스트용 하드코딩 버전 (임시로만 사용)
    @PostMapping
    public int insertClub(@RequestBody Club club) {
        club.setLeaderUserId("testuser001"); // DB에 있는 유저 ID 하드코딩
        return clubService.insert(club);
    }
    */

    // ✅ [중복 체크] 클럽 이름 중복 여부 확인
    @GetMapping("/check-name")
    public boolean isClubNameAvailable(@RequestParam String name) {
        return clubService.findByName(name) == null;
    }

    // ✅ [중복 체크] 팀 코드 중복 여부 확인
    @GetMapping("/check-teamcode")
    public boolean isTeamCodeAvailable(@RequestParam String teamCode) {
        return clubService.findByTeamCode(teamCode) == null;
    }

    // ✅ [클럽 수정] club_id 기준으로 수정
    @PutMapping("/{club_id}")
    public int updateClub(@PathVariable("club_id") int clubId, @RequestBody Club club) {
        club.setClubId(clubId);
        return clubService.update(club);
    }

    // ✅ [클럽 삭제] club_id 기준으로 삭제
    @DeleteMapping("/{club_id}")
    public int deleteClub(@PathVariable("club_id") int clubId) {
        return clubService.delete(clubId);
    }

    // ✅ [클럽 목록 조회] 페이징 + 검색 + 정렬 지원
    @GetMapping
    public Map<String, Object> listClubs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(defaultValue = "ranking") String sortColumn,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        int startRow = (page - 1) * size;

        Map<String, Object> params = new HashMap<>();
        params.put("searchKeyword", searchKeyword);
        params.put("startRow", startRow);
        params.put("pageSize", size);
        params.put("sortColumn", sortColumn);
        params.put("sortDirection", sortDirection);

        List<Club> clubList = clubService.list(params);
        int totalCount = clubService.getTotalCount(params);

        Map<String, Object> result = new HashMap<>();
        result.put("data", clubList);
        result.put("total", totalCount);
        result.put("page", page);
        result.put("size", size);

        return result;
    }
}
