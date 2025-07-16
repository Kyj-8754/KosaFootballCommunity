package com.msa.kyj_prj.webSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// [추가: 필요한 import]
import com.msa.kyj_prj.dto.WebsocketDTO;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Websocket {

    private final RestTemplate restTemplate;
    private final MatchQueryMapper matchQueryMapper;

    @Value("${alarm.api.url}")
    private String alarmApiUrl;

    // (기존) 참가자 전원 알림 전송
    public void sendReservationCancelAlarmToAll(WebsocketDTO receiverUserList,ReservationCancelAlarmMessageDTO alarm) {
    	
        if (receiverUserList == null) return;
        
        try {
            restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
        } catch (Exception e) {
            log.error("🔴 ReservationCancelAlarmMessageDTO 알림 전송 실패! user_no={}", alarm.getSenderId(), e);
        }
    }

    // [★추가★] 예약 id만 받아서 전체 취소 알림을 자동으로 보내는 메서드
    public void sendReservationCancelAlarmByReservationId(int reservation_id) {
    	
        // 1. 예약 상세 조회(여기서는 ReservationDAO 사용)
        ReservationCancelAlarmMessageDTO alarm = new ReservationCancelAlarmMessageDTO();
        
        log.info("유저리스트 받아올거임");
        
        // userlist 받아오기
        List<WebsocketDTO> userList = matchQueryMapper.selectParticipantsUserNoByMatchId(reservation_id);
        
        log.info("받아온 유저리스트"+userList.toString());

        // 2. 세팅
        
        Long match_id = userList.get(0).getMatch_id();
        String match_title = userList.get(0).getMatchTitle();
        
        // 4. 알림 공통 정보 세팅
        String message = (match_title != null)
                ? "[" + match_title + "] 경기가 예약 취소로 인해 취소되었습니다."
                : "예약이 취소되었습니다.";
        String url = (match_id != null) ? "match/" + match_id : "/reservation/" + reservation_id;

        
        // 5. 참가자 전원에게 알림 (매치 연동된 경우)
        if (userList != null && !userList.isEmpty()) {
        	log.info("전원 알림 시작" + userList.toString());
        	for(int i = 0 ; i < userList.size() ; i++) {
        		
        		alarm = alarmSetting(userList.get(i), message, url);
        		
	            sendReservationCancelAlarmToAll(userList.get(i), alarm);
        	}
        } else {
            // 6. 매치가 없거나 참가자가 없으면 예약자 본인에게만 알림
           
        	alarm = alarmSetting(userList.get(0), message, url);
            
            sendReservationCancelAlarmToAll(userList.get(0), alarm);

        }
    }
    
    
    // 보낼 메시지 세팅
	private static ReservationCancelAlarmMessageDTO alarmSetting(WebsocketDTO userList, String message, String url) {
    	ReservationCancelAlarmMessageDTO alarm = new ReservationCancelAlarmMessageDTO();
    	
    	alarm.setType("RESERVATION_CANCEL");
        alarm.setSenderId(userList.getSenderId()); // 받아야하는 사람
        alarm.setReceiverId(userList.getReceiverId()); // 보내는사람
        alarm.setMessage(message);
        alarm.setUrl(url);
        alarm.setMatchId(userList.getMatch_id());
        alarm.setMatchTitle(userList.getMatchTitle());
    	
    	
    	return alarm;
    }
}
