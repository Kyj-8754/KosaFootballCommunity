package com.msa.kyj_prj.club.apply;

import com.msa.kyj_prj.alarm.AlarmMessageDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClubApplyService {

    private final ClubApplyDAO clubApplyDAO;
    private static final Logger log = LoggerFactory.getLogger(ClubApplyService.class);

    public AlarmMessageDTO applyToRecruit(ClubApply clubApply) {
        // 1. 가입 신청 상태 초기화
        clubApply.setStatus("pending");

        // 2. DB에 신청 정보 저장
        clubApplyDAO.insert(clubApply);

        // 3. 팀장 ID 조회
        String leaderId = clubApplyDAO.findLeaderIdByBno(clubApply.getBno());
        if (leaderId == null || leaderId.isEmpty()) {
            log.warn("❗ 팀장 ID를 찾을 수 없습니다. (bno: {})", clubApply.getBno());
            return null;
        }

        // 4. 알림 메시지 구성
        AlarmMessageDTO alarm = new AlarmMessageDTO();
        alarm.setType("CLUB_APPLY");
        alarm.setSenderId(clubApply.getApplicant_id());
        alarm.setReceiverId(leaderId);
        // 🔽 club_id가 있다면 아래도 추가
        // alarm.setClubId(clubApply.getClub_id());
        alarm.setMessage(clubApply.getApplicant_id() + " 님이 클럽가입을 신청했습니다.");
        // alarm.setUrl("/club/..." + clubApply.getBno());  // 필요시

        return alarm;
    }
}
