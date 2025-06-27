package com.msa.kyj_prj.club.apply;

import com.msa.kyj_prj.alarm.AlarmMessageDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ClubApplyService {

    private final ClubApplyDAO clubApplyDAO;
    private final RestTemplate restTemplate; // ✅ @Bean으로 등록된 RestTemplate 주입!
    private static final Logger log = LoggerFactory.getLogger(ClubApplyService.class);

    @Value("${alarm.api.url}")
    private String alarmApiUrl;

    public AlarmMessageDTO applyToRecruit(ClubApply clubApply, int user_no) {
        // 1. bno로 club_id 조회 → DB에서 직접 조회
        Integer clubId = clubApplyDAO.findClubIdByBno(clubApply.getBno());
        if (clubId == null) {
            log.warn("❗ club_id를 찾을 수 없음 (bno: {})", clubApply.getBno());
            return null;
        }
        clubApply.setClub_id(clubId);

        // 2. bno로 팀장 user_no(팀장 ID) 조회 → DB에서 직접 조회
        Integer userNo = clubApplyDAO.findUserNoByBno(clubApply.getBno());
        if (userNo == null) {
            log.warn("❗ 팀장 user_no를 찾을 수 없음 (bno: {})", clubApply.getBno());
            return null;
        }

        // 3. DB에 신청 정보 insert
        int result = clubApplyDAO.insert(clubApply);
        if (result != 1) {
            log.error("❌ 클럽 신청 정보 저장 실패!");
            return null;
        }

        // 4. 알림 메시지 구성
        AlarmMessageDTO alarm = new AlarmMessageDTO();
        alarm.setType("CLUB_APPLY");
        alarm.setSenderId(String.valueOf(clubApply.getAppli_user_no()));  // 신청자 ID
        alarm.setReceiverId(String.valueOf(userNo));                      // 팀장 ID
        alarm.setClubId(clubId);                                          // 클럽 ID
        alarm.setMessage(clubApply.getAppli_user_no() + " 님이 클럽가입을 신청했습니다.");

        // 5. 알람 서버(8086)에 REST POST로 알림 메시지 전송
        try {
            String url = alarmApiUrl + "/api/alarm/send";
            ResponseEntity<Void> response = restTemplate.postForEntity(url, alarm, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ 알림 메시지 전송 성공! (팀장 user_no: {})", userNo);
            } else {
                log.error("🔴 알림 메시지 전송 실패 (status: {})", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("🔴 알림 서버와 통신 중 예외 발생", e);
        }

        return alarm;
    }
}
