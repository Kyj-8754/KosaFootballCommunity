package com.msa.kyj_prj.club.member;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/club/member")
public class ClubMemberController {

    @GetMapping("/test")
    public String test() {
        return "클럽 멤버 컨트롤러 작동 중";
    }
}
