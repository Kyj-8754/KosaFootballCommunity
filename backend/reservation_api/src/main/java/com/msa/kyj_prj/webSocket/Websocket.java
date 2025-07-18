package com.msa.kyj_prj.webSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.msa.kyj_prj.dto.WebsocketDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Websocket {

    private final RestTemplate restTemplate;
    private final MatchQueryMapper matchQueryMapper;

    @Value("${alarm.api.url}")
    private String alarmApiUrl;

    // ✅ 알림 전송
    public void sendReservationCancelAlarmToAll(WebsocketDTO receiverUser, ReservationCancelAlarmMessageDTO alarm) {
        if (receiverUser == null || alarm == null) return;

        try {
            restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
        } catch (Exception e) {
            log.error("🔴 ReservationCancelAlarmMessageDTO 알림 전송 실패! sender_id={}", alarm.getSenderId(), e);
        }
    }

    // ✅ 예약 ID 기반 참가자 전체에게 알림 전송
    public void sendReservationCancelAlarmByReservationId(int reservation_id) {
        log.info("유저리스트 받아올거임");

        // 1. 유저 리스트 조회
        List<WebsocketDTO> userList = matchQueryMapper.selectParticipantsUserNoByMatchId(reservation_id);

        // 2. 유저리스트 null 또는 비어있으면 리턴
        if (userList == null || userList.isEmpty()) {
            log.warn("❗ 예약 ID {} 에 대한 참가자가 없어 알림을 전송하지 않습니다.", reservation_id);
            return;
        }

        log.info("✅ 받아온 유저리스트 = {}", userList);

        // 3. 공통 알림 메시지 구성
        WebsocketDTO firstUser = userList.get(0); // 안전하게 사용 가능
        Long match_id = firstUser.getMatch_id();
        String match_title = firstUser.getMatchTitle();

        String message = (match_title != null)
                ? "[" + match_title + "] 경기가 예약 취소로 인해 취소되었습니다."
                : "예약이 취소되었습니다.";

        String url = (match_id != null) ? "/match/" + match_id : "/reservation/" + reservation_id;

        // 4. 모든 참가자에게 알림 전송
        log.info("📢 전원 알림 전송 시작");
        for (WebsocketDTO user : userList) {
            ReservationCancelAlarmMessageDTO alarm = alarmSetting(user, message, url);
            sendReservationCancelAlarmToAll(user, alarm);
        }
    }

    // ✅ 알림 DTO 세팅
    private static ReservationCancelAlarmMessageDTO alarmSetting(WebsocketDTO user, String message, String url) {
        ReservationCancelAlarmMessageDTO alarm = new ReservationCancelAlarmMessageDTO();

        alarm.setType("RESERVATION_CANCEL");
        alarm.setSenderId(user.getSenderId());
        alarm.setReceiverId(user.getReceiverId());
        alarm.setMessage(message);
        alarm.setUrl(url);
        alarm.setMatchId(user.getMatch_id());
        alarm.setMatchTitle(user.getMatchTitle());

        return alarm;
    }
}
