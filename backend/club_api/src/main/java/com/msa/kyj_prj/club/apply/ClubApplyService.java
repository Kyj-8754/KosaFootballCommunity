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
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(ClubApplyService.class);

    @Value("${alarm.api.url}")
    private String alarmApiUrl;

    /**
     * 클럽 모집글 가입 신청 처리
     * (🔴 [중요] 중복신청/24시간 제한 체크 추가)
     */
    public AlarmMessageDTO applyToRecruit(ClubApply clubApply, int user_no) {

        // (1) 🔴 [중요 추가] 신청 중복/취소 24시간 제한 로직
        int bno = clubApply.getBno();
        int appli_user_no = clubApply.getAppli_user_no();

        // 1-1. 기존 신청 이력 확인 (중복 insert 막기)
        ClubApply exist = clubApplyDAO.findLastApplyByBnoAndApplicant(bno, appli_user_no);

        if (exist != null) {
            // 이미 신청한 적이 있다면 상태값으로 분기
            if ("pending".equals(exist.getStatus()) || "approved".equals(exist.getStatus())) {
                // 아직 대기/승인상태: 중복신청 차단
                throw new IllegalStateException("❌ 재가입 신청은 24시간 뒤에 해주세요.");
            }
            if ("canceled".equals(exist.getStatus())) {
                // 최근 24시간 내 취소 이력 존재하면 차단
                ClubApply canceled = clubApplyDAO.findRecentCanceledApply(bno, appli_user_no);
                if (canceled != null) {
                    throw new IllegalStateException("❌ 재가입 신청은 24시간 뒤에 해주세요.");
                }
                // 24시간 경과 후 재신청 → 기존 row status를 pending으로 변경
                int updated = clubApplyDAO.updateStatusToPending(bno, appli_user_no);
                if (updated != 1) throw new IllegalStateException("재신청 갱신에 실패했습니다.");
                // 나머지 로직은 그대로 진행(알람 전송)
            }
            // 기타(예: rejected 등) → 필요시 추가 분기 가능
        } else {
            // (2) 신규 신청: DB insert
            // 2-1. bno로 club_id 조회 → DB에서 직접 조회
            Integer clubId = clubApplyDAO.findClubIdByBno(clubApply.getBno());
            if (clubId == null) {
                log.warn("❗ club_id를 찾을 수 없음 (bno: {})", clubApply.getBno());
                return null;
            }
            clubApply.setClub_id(clubId);

            // 2-2. bno로 팀장 user_no(팀장 ID) 조회 → DB에서 직접 조회
            Integer userNo = clubApplyDAO.findUserNoByBno(clubApply.getBno());
            if (userNo == null) {
                log.warn("❗ 팀장 user_no를 찾을 수 없음 (bno: {})", clubApply.getBno());
                return null;
            }

            // 2-3. 신규 신청 INSERT (상태 초기값 지정)
            clubApply.setStatus("pending");
            int result = clubApplyDAO.insert(clubApply);
            if (result != 1) {
                log.error("❌ 클럽 신청 정보 저장 실패!");
                return null;
            }
        }

        // (3) 알림 메시지 구성
        Integer clubId = clubApplyDAO.findClubIdByBno(clubApply.getBno());
        Integer userNo = clubApplyDAO.findUserNoByBno(clubApply.getBno());
        AlarmMessageDTO alarm = new AlarmMessageDTO();
        alarm.setType("CLUB_APPLY");
        alarm.setSenderId(String.valueOf(clubApply.getAppli_user_no()));  // 신청자 ID
        alarm.setReceiverId(String.valueOf(userNo));                      // 팀장 ID
        alarm.setClubId(clubId);                                          // 클럽 ID
        alarm.setMessage(clubApply.getAppli_user_no() + " 님이 클럽가입을 신청했습니다.");

        // (4) 알람 서버(8086)에 REST POST로 알림 메시지 전송 (실패해도 무방)
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

    // 가입 취소 (status를 canceled로 변경)
    public boolean cancelApply(int bno, int appli_user_no) {
        int updated = clubApplyDAO.cancelByBnoAndApplicant(bno, appli_user_no);
        return updated > 0;
    }

    // 최근 24시간 이내 취소 이력 조회
    public boolean canReApply(int bno, int appli_user_no) {
        ClubApply canceled = clubApplyDAO.findRecentCanceledApply(bno, appli_user_no);
        return (canceled == null); // null이면 24시간 제한 없음 → 재신청 가능
    }
    
    
 // ClubApplyService.java
    public ClubApply findLastApplyByBnoAndApplicant(int bno, int appli_user_no) {
        return clubApplyDAO.findLastApplyByBnoAndApplicant(bno, appli_user_no);
    }

    
    
}
