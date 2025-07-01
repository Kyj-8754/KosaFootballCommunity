package com.msa.kyj_prj.match.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/match-log")
public class MatchLogController {

    @Autowired
    private MatchLogService matchLogService;

    // 특정 매치의 로그 전체 조회
    @GetMapping("/{match_id}")
    public List<MatchLog> getLogsByMatchId(@PathVariable("match_id") Long match_id) {
        return matchLogService.getLogsByMatchId(match_id);
    }

    // 로그 등록
    @PostMapping("/add")
    public int addMatchLog(@RequestBody MatchLog log) {
        return matchLogService.addMatchLog(log);
    }

    // 로그 수정
    @PutMapping("/update")
    public int updateMatchLog(@RequestBody MatchLog log) {
        return matchLogService.updateMatchLog(log);
    }

    // 로그 삭제
    @DeleteMapping("/delete/{log_id}")
    public int deleteMatchLog(@PathVariable("log_id") Long log_id) {
        return matchLogService.deleteMatchLog(log_id);
    }
    
    // 승인된 참가자 목록 (user_no + user_name)
    @GetMapping("/approved-users/{match_id}")
    public List<Map<String, Object>> getApprovedUsers(@PathVariable("match_id") Long match_id) {
        return matchLogService.getApprovedUsers(match_id);
    }
    
    // 승인된 팀 목록
    @GetMapping("/approved-teams/{match_id}")
    public List<Map<String, Object>> getApprovedTeams(@PathVariable("match_id") Long match_id) {
        return matchLogService.getApprovedTeamsByMatchId(match_id);
    }


}
