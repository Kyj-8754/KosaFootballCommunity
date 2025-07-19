package com.msa.kyj_prj.club.info;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/club/club_info")
@Tag(name = "Club Info", description = "클럽 상세 정보 API")
public class ClubInfoController {

    @Autowired
    private ClubInfoService clubInfoService;

    // ✅ 클럽 상세 정보 단건 조회 (by club_id)
    @Operation(
        summary = "클럽 상세 정보 단건 조회",
        description = "club_id로 클럽 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "클럽 ID가 필요함"),
        @ApiResponse(responseCode = "404", description = "클럽 상세 정보를 찾을 수 없음")
    })
    @GetMapping("/{club_id}")
    public ResponseEntity<?> getClubInfo(
            @Parameter(description = "클럽 ID", example = "3") @PathVariable Integer club_id) {
        if (club_id == null || club_id == 0) {
            return ResponseEntity.badRequest().body("클럽 ID가 필요합니다.");
        }
        try {
            ClubInfo info = clubInfoService.getClubInfo(club_id);
            if (info == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("클럽 상세 정보를 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("조회 오류: " + e.getMessage());
        }
    }

    // ✅ 클럽 상세 정보 등록
    @Operation(
        summary = "클럽 상세 정보 등록",
        description = "클럽 상세 정보(ClubInfo)를 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "클럽 ID가 필요함"),
        @ApiResponse(responseCode = "500", description = "등록 오류")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "등록할 ClubInfo 객체"
    )
    @PostMapping("")
    public ResponseEntity<?> insertClubInfo(@RequestBody ClubInfo clubInfo) {
        // clubInfo가 null이거나 club_id가 0일 때 예외처리
        if (clubInfo == null || clubInfo.getClub_id() == 0) {
            return ResponseEntity.badRequest().body("클럽 ID가 필요합니다.");
        }
        try {
            int result = clubInfoService.insertClubInfo(clubInfo);
            if (result > 0) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 오류: " + e.getMessage());
        }
    }

    // ✅ 클럽 상세 정보 수정
    @Operation(
        summary = "클럽 상세 정보 수정",
        description = "club_id에 해당하는 클럽 상세 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "클럽 ID가 필요함"),
        @ApiResponse(responseCode = "500", description = "수정 오류")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "수정할 ClubInfo 객체"
    )
    @PutMapping("/{club_id}")
    public ResponseEntity<?> updateClubInfo(
            @Parameter(description = "클럽 ID", example = "12") @PathVariable Integer club_id,
            @RequestBody ClubInfo clubInfo) {
        if (club_id == null || club_id == 0) {
            return ResponseEntity.badRequest().body("클럽 ID가 필요합니다.");
        }
        try {
            clubInfo.setClub_id(club_id);
            int result = clubInfoService.updateClubInfo(clubInfo);
            if (result > 0) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("수정 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 오류: " + e.getMessage());
        }
    }

    // (선택) 전체 클럽 info 리스트 조회
    @Operation(
        summary = "전체 클럽 상세 정보 리스트 조회",
        description = "모든 클럽 상세 정보를 리스트로 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/list")
    public ResponseEntity<?> listClubInfo() {
        try {
            List<ClubInfo> list = clubInfoService.listClubInfo();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("목록 조회 오류: " + e.getMessage());
        }
    }

    // (선택) 클럽 상세 정보 삭제
    @Operation(
        summary = "클럽 상세 정보 삭제",
        description = "club_id로 클럽 상세 정보를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "400", description = "클럽 ID가 필요함"),
        @ApiResponse(responseCode = "500", description = "삭제 오류")
    })
    @DeleteMapping("/{club_id}")
    public ResponseEntity<?> deleteClubInfo(
            @Parameter(description = "클럽 ID", example = "12") @PathVariable Integer club_id) {
        if (club_id == null || club_id == 0) {
            return ResponseEntity.badRequest().body("클럽 ID가 필요합니다.");
        }
        try {
            int result = clubInfoService.deleteClubInfo(club_id);
            if (result > 0) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("삭제 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 오류: " + e.getMessage());
        }
    }

    // 공통 IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
