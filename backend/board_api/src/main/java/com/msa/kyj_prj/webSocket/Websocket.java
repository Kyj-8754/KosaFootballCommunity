package com.msa.kyj_prj.webSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.msa.kyj_prj.match.Match;
import com.msa.kyj_prj.match.alarm.MatchAlarmMessageDTO;

import lombok.RequiredArgsConstructor;

@Component // 반드시 추가!
@RequiredArgsConstructor
public class Websocket {

    private final RestTemplate restTemplate;

    @Value("${alarm.api.url}")
    private String alarmApiUrl;

    // userNo, matchId 등 파라미터 추가!
    public boolean matchWebsocket(String userStatus, Match match, Integer userNo, Long matchId) {
        if ("approve".equalsIgnoreCase(userStatus) || "reject".equalsIgnoreCase(userStatus)) {
            MatchAlarmMessageDTO alarm = new MatchAlarmMessageDTO();
            alarm.setMatchId(matchId);
            alarm.setMatchTitle(match.getMatch_title());
            alarm.setReceiverId(String.valueOf(userNo));
            alarm.setUrl("/match/" + matchId);

            if ("approve".equalsIgnoreCase(userStatus)) {
                alarm.setType("MATCH_APPROVED");
                alarm.setSenderId(String.valueOf(match.getUser_no())); // 글쓴이
                alarm.setMessage("[" + match.getMatch_title() + "] 경기에 참가가 승인되었습니다!");
            } else {
                alarm.setType("MATCH_REJECT");
                alarm.setSenderId(String.valueOf(match.getUser_no()));
                alarm.setMessage("[" + match.getMatch_title() + "] 경기에 참가가 거절되었습니다.");
            }

            try {
                restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
                return true;
            } catch (Exception e) {
                // 알람 실패 무시 (필요시 로그만)
                return false;
            }
        }
        return false;
    }
}
