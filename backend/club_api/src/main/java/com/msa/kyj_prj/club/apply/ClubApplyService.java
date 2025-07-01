package com.msa.kyj_prj.club.apply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import com.msa.kyj_prj.alarm.AlarmMessageDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubApplyService {

    private final ClubApplyDAO clubApplyDAO;
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(ClubApplyService.class);

    @Value("${alarm.api.url}")
    private String alarmApiUrl;

    public AlarmMessageDTO applyToRecruit(ClubApply clubApply, int appli_user_no) {
        int bno = clubApply.getBno();

        // 1. 팀장 user_no 조회 (모집글에서)
        int user_no = clubApplyDAO.findUserNoByBno(bno);
        if (user_no == 0) {
            throw new IllegalStateException("팀장 정보를 찾을 수 없습니다.");
        }

        // 2. 팀장이 본인 클럽에 가입 신청할 수 없도록 체크
        if (appli_user_no == user_no) {
            throw new IllegalStateException("팀장은 본인 클럽에 가입 신청할 수 없습니다.");
        }

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
    
    
    // ★ 전체 신청자 목록 조회 (컨트롤러에서 호출)
    public List<ClubApply> findByClubId(int club_id) {
        return clubApplyDAO.findByClubId(club_id);
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

    // 가장 마지막에 신청한 취소 이력 조회
    public ClubApply findLastApplyByBnoAndApplicant(int bno, int appli_user_no) {
        return clubApplyDAO.findLastApplyByBnoAndApplicant(bno, appli_user_no);
    }

 // 멤버의 클럽 가입 신청을 승인(상태변경 + 멤버테이블 insert)하는 메소드
    public boolean approveApply(int apply_id) {
        int updated = clubApplyDAO.updateStatus(apply_id, "approved");
        if (updated > 0) {
            // 승인된 신청 정보(apply_id 기준) 단건 조회
            ClubApply apply = clubApplyDAO.findByApplyId(apply_id); // 반드시 단건 조회 쿼리 필요
            if (apply != null) {
                int club_id = apply.getClub_id();
                int user_no = apply.getAppli_user_no();
                // 승인과 동시에 멤버 insert (이미 insertClubMember 메서드가 존재해야 함)
                insertClubMember(club_id, user_no);
            }
            return true;
        }
        return false;
    }


    // 멤버의 클럽 가입 신청을 거절하는 메소드
    public boolean rejectApply(int apply_id) {
        int updated = clubApplyDAO.updateStatus(apply_id, "rejected");
        return updated > 0;
    }

    // 클럽 멤버 테이블에 insert (승인 시 직접 호출)
    public int insertClubMember(int club_id, int user_no) {
        return clubApplyDAO.insertClubMember(club_id, user_no);
    }
}
