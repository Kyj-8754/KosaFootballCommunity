package com.msa.kyj_prj.club.apply;

import com.msa.kyj_prj.alarm.AlarmMessageDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class ClubApplyService {

    private final ClubApplyDAO clubApplyDAO;
    private static final Logger log = LoggerFactory.getLogger(ClubApplyService.class);

    public AlarmMessageDTO applyToRecruit(ClubApply clubApply, int user_no) {
        // 1. bno로 club_id 조회 → DB에서 직접 조회
        Integer clubId = clubApplyDAO.findClubIdByBno(clubApply.getBno());
        if (clubId == null) {
            log.warn("❗ club_id를 찾을 수 없음 (bno: {})", clubApply.getBno());
            return null;
        }
        clubApply.setClub_id(clubId); // 반드시 set

        // 2. bno로 팀장 user_no(팀장 ID) 조회 → DB에서 직접 조회
        Integer UserNo = clubApplyDAO.findUserNoByBno(clubApply.getBno());
        if (UserNo == null) { // ★ 변수명 일치!
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
        alarm.setReceiverId(String.valueOf(UserNo));                // 팀장 ID
        alarm.setClubId(clubId);                                          // 클럽 ID
        alarm.setMessage(clubApply.getAppli_user_no() + " 님이 클럽가입을 신청했습니다.");
        // alarm.setUrl("/club/...") // (선택 구현)

        return alarm;
    }
}
