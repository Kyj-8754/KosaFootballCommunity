package com.msa.kyj_prj.club.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/club/member")
public class ClubMemberController {

    @Autowired
    private ClubMemberService clubMemberService;

    // 클럽 멤버 등록
    @PostMapping("")
    public ResponseEntity<String> insert(@RequestBody ClubMember club_member) {
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
    @GetMapping("/list/{club_id}")
    public ResponseEntity<?> findByClubId(@PathVariable int club_id) {
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
    @GetMapping("/{club_id}/{user_no}")
    public ResponseEntity<?> findByClubIdAndUserNo(@PathVariable int club_id, @PathVariable int user_no) {
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
    @DeleteMapping("/{club_id}/{user_no}")
    public ResponseEntity<?> deleteByClubIdAndUserNo(@PathVariable int club_id, @PathVariable int user_no) {
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
    @PutMapping("/role")
    public ResponseEntity<?> updateRoleByClubIdAndUserNo(@RequestBody ClubMember club_member) {
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
    @PutMapping("/delegate_leader")
    public ResponseEntity<?> delegateLeader(@RequestBody DelegateLeaderRequest request) {
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

    // 아래 DTO를 파일 위쪽이나 별도 파일로 선언
    public static class DelegateLeaderRequest {
        private int club_id;
        private int new_leader_user_no;
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
