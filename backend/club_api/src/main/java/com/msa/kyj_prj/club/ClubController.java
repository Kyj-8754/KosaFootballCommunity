package com.msa.kyj_prj.club;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/club") // ✅ club_api 프록시와 매핑되도록 루트 수정
public class ClubController {

    @Autowired
    private ClubService clubService;

 // ✅ 클럽 생성 - JWT 없이 단순 등록
    @PostMapping("")
    public void createClub(@RequestBody Club club) {
        clubService.insert(club); // ✅ create → insert 로 변경
    }

    
    // ✅ [단건 조회] 팀 코드로 클럽 조회 - /club/code/{teamCode}
    @GetMapping("/code/{teamCode}")
    public Club getClubByTeamCode(@PathVariable String teamCode) {
        return clubService.findByTeamCode(teamCode);
    }

    // ✅ [중복 체크] 클럽 이름 중복 여부 확인 - /club/check-name?name=...
    @GetMapping("/check-name")
    public boolean isClubNameAvailable(@RequestParam String name) {
        return clubService.findByName(name) == null;
    }

 // ✅ 팀 코드 중복 확인 API - 예외 처리 포함
    @GetMapping("/check-teamcode")
    public ResponseEntity<Boolean> isTeamCodeAvailable(@RequestParam(required = false) String teamCode) {
        try {
            // 빈 문자열 또는 null이면 잘못된 요청 처리
            if (teamCode == null || teamCode.trim().isEmpty()) {
                throw new IllegalArgumentException("팀 코드는 필수 입력값입니다.");
            }

            boolean isAvailable = clubService.findByTeamCode(teamCode) == null;
            return ResponseEntity.ok(isAvailable);  // 200 OK + true/false

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);  // 400 Bad Request
        } catch (Exception e) {
            // 예기치 못한 서버 오류 처리 (500)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // ✅ [클럽 목록 조회] - /club/list?page=1&size=10 ...
    // 🔧 이 메서드는 clubs_api 에 대응되므로 URL 수정하거나 컨트롤러를 분리해도 됨
    @GetMapping("/list")
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
