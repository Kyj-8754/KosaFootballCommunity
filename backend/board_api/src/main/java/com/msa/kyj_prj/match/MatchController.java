package com.msa.kyj_prj.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;


    // 소셜 매치 목록 조회
    @GetMapping("/social")
    public List<Match> getSocialMatches(@RequestParam Map<String, Object> params) {
        return matchService.getSocialMatches(params);
    }


    // 리그 매치 목록 조회
    @GetMapping("/league")
    public List<Match> getLeagueMatches(@RequestParam Map<String, Object> params) {
        return matchService.getLeagueMatches(params);
    }

    // 매치 상세 조회
    @GetMapping("/{id}")
    public Match getMatchDetail(@PathVariable("id") Long matchId) {
        return matchService.getMatchDetail(matchId);
    }
    
    // 매치 참가 신청
    @PostMapping("/apply")
    public void applyToMatch(@RequestBody MatchParticipant participant) {
        matchService.applyToMatch(participant);
    }
    
    // 매치 참가 신청 여부 확인
    @GetMapping("/applied")
    public boolean checkUserApplied(@RequestParam Long matchId, @RequestParam Integer userNo) {
        return matchService.hasUserApplied(matchId, userNo);
    }
    
    // 매치 신청 취소
    @DeleteMapping("/cancel")
    public void cancelMatchParticipant(@RequestParam Long matchId, @RequestParam Integer userNo) {
        matchService.cancelMatchParticipant(matchId, userNo);
    }

}