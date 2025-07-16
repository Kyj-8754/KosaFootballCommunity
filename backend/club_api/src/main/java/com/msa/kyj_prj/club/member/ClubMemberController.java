package com.msa.kyj_prj.club.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Swagger/OpenAPI 어노테이션 임포트
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody; // Spring의 @RequestBody와 이름 충돌 방지를 위해 풀 패키지 경로 사용

@RestController
@RequestMapping("/club/member")
@Tag(name = "Club Member API", description = "클럽 멤버 관련 작업을 위한 API") // 컨트롤러에 대한 태그
public class ClubMemberController {

    @Autowired
    private ClubMemberService clubMemberService;

    // 클럽 멤버 등록
    @Operation(summary = "클럽 멤버 등록", description = "새로운 클럽 멤버를 등록합니다. club_id와 user_no는 필수입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "클럽 멤버로 정상 등록되었습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "클럽/유저 정보가 누락되었거나 이미 등록된 유저입니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "멤버 등록 중 서버 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("")
    public ResponseEntity<String> insert(
            @RequestBody(description = "등록할 클럽 멤버 정보", required = true,
                    content = @Content(schema = @Schema(implementation = ClubMember.class))) // OpenAPI의 @RequestBody
            @org.springframework.web.bind.annotation.RequestBody ClubMember club_member) { // Spring의 @RequestBody
        if (club_member == null || club_member.getClub_id() == 0 || club_member.getUser_no() == 0) {
            return ResponseEntity.badRequest().body("클럽/유저 정보가 누락되었습니다.");
        }
        try {
            int result = clubMemberService.insert(club_member);
            if (result > 0) {
                return ResponseEntity.ok("클럽 멤버로 정상 등록되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("멤버 등록 실패 또는 이미 등록된 유저입니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("멤버 등록 중 서버 오류: " + e.getMessage());
        }
    }

    // 클럽별 전체 멤버 리스트 조회
    @Operation(summary = "클럽별 전체 멤버 리스트 조회", description = "주어진 클럽 ID에 해당하는 모든 클럽 멤버의 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "클럽 멤버 목록을 성공적으로 조회했습니다.", content = @Content(schema = @Schema(implementation = ClubMember.class))),
            @ApiResponse(responseCode = "400", description = "클럽 ID가 누락되었습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "멤버 목록 조회 실패: 서버 오류", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/list/{club_id}")
    public ResponseEntity<?> findByClubId(
            @Parameter(description = "조회할 클럽의 ID", required = true, example = "101")
            @PathVariable int club_id) {
        if (club_id == 0) {
            return ResponseEntity.badRequest().body("클럽 ID가 필요합니다.");
        }
        try {
            List<ClubMember> list = clubMemberService.findByClubId(club_id);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("멤버 목록 조회 실패: " + e.getMessage());
        }
    }

    // 특정 클럽의 특정 유저 멤버 정보 조회
    @Operation(summary = "특정 클럽의 특정 유저 멤버 정보 조회", description = "주어진 클럽 ID와 사용자 번호에 해당하는 클럽 멤버 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "클럽 멤버 정보를 성공적으로 조회했습니다.", content = @Content(schema = @Schema(implementation = ClubMember.class))),
            @ApiResponse(responseCode = "400", description = "클럽/유저 정보가 누락되었습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "멤버 정보 조회 실패: 서버 오류", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/{club_id}/{user_no}")
    public ResponseEntity<?> findByClubIdAndUserNo(
            @Parameter(description = "클럽 ID", required = true, example = "101")
            @PathVariable int club_id,
            @Parameter(description = "사용자 번호", required = true, example = "777")
            @PathVariable int user_no) {
        if (club_id == 0 || user_no == 0) {
            return ResponseEntity.badRequest().body("클럽/유저 정보가 필요합니다.");
        }
        try {
            ClubMember member = clubMemberService.findByClubIdAndUserNo(club_id, user_no);
            if (member != null) {
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 멤버를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("멤버 정보 조회 실패: " + e.getMessage());
        }
    }

    // 클럽 멤버 탈퇴(논리삭제)만 수행
    @Operation(summary = "클럽 멤버 탈퇴 (논리 삭제)", description = "클럽 ID와 사용자 번호를 사용하여 클럽 멤버를 논리적으로 탈퇴 처리합니다. (left_at 필드 업데이트)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 클럽을 탈퇴하였습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "클럽/유저 정보가 누락되었거나 이미 탈퇴한 멤버입니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "멤버 탈퇴 중 서버 오류", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("/{club_id}/{user_no}")
    public ResponseEntity<?> deleteByClubIdAndUserNo(
            @Parameter(description = "탈퇴할 클럽의 ID", required = true, example = "101")
            @PathVariable int club_id,
            @Parameter(description = "탈퇴할 사용자의 번호", required = true, example = "777")
            @PathVariable int user_no) {
        if (club_id == 0 || user_no == 0) {
            return ResponseEntity.badRequest().body("클럽/유저 정보가 필요합니다.");
        }
        try {
            int result = clubMemberService.deleteByClubIdAndUserNo(club_id, user_no);
            if (result > 0) {
                return ResponseEntity.ok("정상적으로 클럽을 탈퇴하였습니다.");
            } else {
                return ResponseEntity.badRequest().body("탈퇴 실패 또는 이미 탈퇴한 멤버입니다.");
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("멤버 탈퇴 중 서버 오류: " + e.getMessage());
        }
    }

    // 클럽 멤버 역할 변경
    @Operation(summary = "클럽 멤버 역할 변경", description = "클럽 멤버의 역할을 변경합니다 (예: MEMBER에서 LEADER로).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "역할이 성공적으로 변경되었습니다.", content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "필수 정보가 누락되었거나 역할 변경에 실패했습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "역할 변경 중 서버 오류", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/role")
    public ResponseEntity<?> updateRoleByClubIdAndUserNo(
            @RequestBody(description = "역할을 변경할 클럽 멤버 정보 (club_id, user_no, role 필요)", required = true,
                    content = @Content(schema = @Schema(implementation = ClubMember.class))) // OpenAPI의 @RequestBody
            @org.springframework.web.bind.annotation.RequestBody ClubMember club_member) { // Spring의 @RequestBody
        if (club_member == null || club_member.getClub_id() == 0 || club_member.getUser_no() == 0 || club_member.getRole() == null) {
            return ResponseEntity.badRequest().body("필수 정보가 누락되었습니다.");
        }
        try {
            int result = clubMemberService.updateRoleByClubIdAndUserNo(club_member.getClub_id(), club_member.getUser_no(),
                    club_member.getRole());
            if (result > 0) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("역할 변경 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("역할 변경 중 서버 오류: " + e.getMessage());
        }
    }

    // 리더(클럽장) 위임 API
    @Operation(summary = "리더 (클럽장) 위임", description = "현재 클럽 리더의 권한을 다른 멤버에게 위임합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리더가 성공적으로 위임되었습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "필수 정보가 누락되었습니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "리더 위임 실패: 서버 오류", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/delegate_leader")
    public ResponseEntity<?> delegateLeader(
            @RequestBody(description = "리더 위임 요청 정보 (club_id, new_leader_user_no, old_leader_user_no 필요)", required = true,
                    content = @Content(schema = @Schema(implementation = DelegateLeaderRequest.class))) // OpenAPI의 @RequestBody
            @org.springframework.web.bind.annotation.RequestBody DelegateLeaderRequest request) { // Spring의 @RequestBody
        if (request == null || request.getClub_id() == 0
                || request.getNew_leader_user_no() == 0 || request.getOld_leader_user_no() == 0) {
            return ResponseEntity.badRequest().body("필수 정보가 누락되었습니다.");
        }
        try {
            clubMemberService.delegateLeader(request.getClub_id(), request.getNew_leader_user_no(),
                    request.getOld_leader_user_no());
            return ResponseEntity.ok("리더가 성공적으로 위임되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리더 위임 실패: " + e.getMessage());
        }
    }

    // DelegateLeaderRequest DTO를 파일 위쪽이나 별도 파일로 선언
    @Schema(description = "리더 위임 요청을 위한 데이터 전송 객체") // DTO에 대한 설명
    public static class DelegateLeaderRequest {
        @Schema(description = "클럽 ID", example = "101")
        private int club_id;
        @Schema(description = "새로운 리더의 사용자 번호", example = "778")
        private int new_leader_user_no;
        @Schema(description = "기존 리더의 사용자 번호", example = "777")
        private int old_leader_user_no;

        // getter/setter
        public int getClub_id() {
            return club_id;
        }

        public void setClub_id(int club_id) {
            this.club_id = club_id;
        }

        public int getNew_leader_user_no() {
            return new_leader_user_no;
        }

        public void setNew_leader_user_no(int new_leader_user_no) {
            this.new_leader_user_no = new_leader_user_no;
        }

        public int getOld_leader_user_no() {
            return old_leader_user_no;
        }

        public void setOld_leader_user_no(int old_leader_user_no) {
            this.old_leader_user_no = old_leader_user_no;
        }
    }
}
