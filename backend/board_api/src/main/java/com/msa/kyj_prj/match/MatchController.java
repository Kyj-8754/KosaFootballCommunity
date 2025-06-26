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
}