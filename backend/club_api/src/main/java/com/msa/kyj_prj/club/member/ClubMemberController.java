package com.msa.kyj_prj.club.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/club/member")
public class ClubMemberController {

    @Autowired
    private ClubMemberService clubMemberService;

    // 클럽 멤버 등록
    @PostMapping("")
    public ResponseEntity<String> insert(@RequestBody ClubMember club_member) {
        int result = clubMemberService.insert(club_member);
        if (result > 0) {
            return ResponseEntity.ok("클럽 멤버로 정상 등록되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("멤버 등록 실패 또는 이미 등록된 유저입니다.");
        }
    }

    // 클럽별 전체 멤버 리스트 조회
    @GetMapping("/list/{club_id}")
    public List<ClubMember> findByClubId(@PathVariable int club_id) {
        return clubMemberService.findByClubId(club_id);
    }

    // 특정 클럽의 특정 유저 멤버 정보 조회
    @GetMapping("/{club_id}/{user_no}")
    public ClubMember findByClubIdAndUserNo(@PathVariable int club_id, @PathVariable int user_no) {
        return clubMemberService.findByClubIdAndUserNo(club_id, user_no);
    }

    // 클럽 멤버 탈퇴(논리삭제)만 수행
    @DeleteMapping("/{club_id}/{user_no}")
    public ResponseEntity<?> deleteByClubIdAndUserNo(@PathVariable int club_id, @PathVariable int user_no) {
        try {
            int result = clubMemberService.deleteByClubIdAndUserNo(club_id, user_no);

            if (result > 0) {
                return ResponseEntity.ok("정상적으로 클럽을 탈퇴하였습니다.");
            } else {
                return ResponseEntity.badRequest().body("탈퇴 실패 또는 이미 탈퇴한 멤버입니다.");
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 클럽 멤버 역할 변경
    @PutMapping("/role")
    public int updateRoleByClubIdAndUserNo(@RequestBody ClubMember club_member) {
        return clubMemberService.updateRoleByClubIdAndUserNo(
                club_member.getClub_id(),
                club_member.getUser_no(),
                club_member.getRole()
        );
    }
}
