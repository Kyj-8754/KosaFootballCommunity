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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/club")
public class ClubController {

    @Autowired
    private ClubService clubService;

    // 클럽 생성
    @PostMapping("")
    public ResponseEntity<?> createClub(@RequestBody Club club) {
        try {
            if (club == null || club.getClub_name() == null || club.getClub_name().trim().isEmpty()
                    || club.getTeam_code() == null || club.getTeam_code().trim().isEmpty()
                    || club.getUser_no() == 0) {
                return ResponseEntity.badRequest().body("❌ 필수 입력값이 누락되었습니다.");
            }
            clubService.insert(club);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 생성 실패: " + e.getMessage());
        }
    }

    // 팀 코드로 클럽 조회
    @GetMapping("/code/{teamCode}")
    public ResponseEntity<?> getClubByTeamCode(@PathVariable String teamCode) {
        if (teamCode == null || teamCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("팀 코드가 누락되었습니다.");
        }
        Club club = clubService.findByTeamCode(teamCode);
        if (club == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(club);
    }

    // 클럽 이름 중복 여부 확인
    @GetMapping("/check-name")
    public ResponseEntity<Boolean> isClubNameAvailable(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        boolean available = clubService.findByName(name) == null;
        return ResponseEntity.ok(available);
    }

    // 팀 코드 중복 확인
    @GetMapping("/check-teamcode")
    public ResponseEntity<Boolean> isTeamCodeAvailable(@RequestParam(required = false) String teamCode) {
        try {
            if (teamCode == null || teamCode.trim().isEmpty()) {
                throw new IllegalArgumentException("팀 코드는 필수 입력값입니다.");
            }
            boolean isAvailable = clubService.findByTeamCode(teamCode) == null;
            return ResponseEntity.ok(isAvailable);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 유저 번호로 클럽 보유 여부 확인
    @GetMapping("/hasClub/{userNo}")
    public ResponseEntity<Map<String, Boolean>> hasClub(@PathVariable int userNo) {
        Map<String, Boolean> result = new HashMap<>();
        try {
            boolean exists = clubService.hasClubByUserNo(userNo);
            result.put("result", exists);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("result", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // 여러 클럽 조회
    @GetMapping("/myClubs/{userNo}")
    public ResponseEntity<?> getClubsByUser(@PathVariable int userNo) {
        try {
            List<Club> clubs = clubService.findClubsByUserNo(userNo);
            return ResponseEntity.ok(clubs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 목록 조회 실패: " + e.getMessage());
        }
    }

    // 단일 클럽 club_id만 반환
    @GetMapping("/getSingleClub/{userNo}")
    public ResponseEntity<Map<String, Object>> getSingleClub(@PathVariable int userNo) {
        Map<String, Object> result = new HashMap<>();
        try {
            Club club = clubService.findClubByUserNo(userNo);
            result.put("club_id", club != null ? club.getClub_id() : null);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("club_id", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // 클럽 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> listClubs(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String searchKeyword,
            @RequestParam(defaultValue = "ranking") String sortColumn,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        try {
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

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 목록 조회 실패: " + e.getMessage());
        }
    }

    // 클럽 정보 수정
    @PutMapping("/{club_id}")
    public ResponseEntity<String> updateClub(@PathVariable int club_id, @RequestBody Club club) {
        try {
            club.setClub_id(club_id);
            int result = clubService.updateClub(club);
            if (result > 0) {
                return ResponseEntity.ok("클럽 정보가 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("클럽 정보 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }

    // teamCode로 club_id만 반환
    @GetMapping("/idByTeamCode")
    public ResponseEntity<Map<String, Integer>> getClubIdByTeamCode(@RequestParam("teamCode") String teamCode) {
        Map<String, Integer> result = new HashMap<>();
        try {
            if (teamCode == null || teamCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            Club club = clubService.findByTeamCode(teamCode);
            if (club != null) {
                result.put("club_id", club.getClub_id());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 클럽 로고 이미지 업로드
    @PostMapping("/{club_id}/uploadLogo")
    public ResponseEntity<String> uploadLogo(@PathVariable int club_id, @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("파일이 선택되지 않았습니다.");
            }
            String uploadDir = "C:/workspace-sts4/MsaTeamProject/backend/club_api/uploads/club_logos/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = club_id + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + filename);
            file.transferTo(dest);

            String logoPath = "/uploads/club_logos/" + filename;
            clubService.updateLogoPath(club_id, logoPath);

            return ResponseEntity.ok(logoPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패: " + e.getMessage());
        }
    }

    @GetMapping("/test-upload-path")
    public ResponseEntity<String> testUploadPath() {
        try {
            File dir = new File("C:/workspace-sts4/MsaTeamProject/backend/club_api/uploads/club_logos/");
            return ResponseEntity.ok("폴더 exists: " + dir.exists() + ", path: " + dir.getAbsolutePath());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("경로 확인 실패: " + e.getMessage());
        }
    }
}
