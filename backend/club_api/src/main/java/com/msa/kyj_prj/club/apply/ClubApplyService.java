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

    /**
     * ✅ 클럽 가입 신청 처리 서비스
     * 1. 모집글 번호(bno)로 작성자(writer) 조회
     * 2. writer가 팀장인 클럽의 club_id 조회
     * 3. 신청 정보 DB에 저장
     * 4. 알림 메시지 DTO 생성 후 반환
     */
    public AlarmMessageDTO applyToRecruit(ClubApply clubApply, String user_no) {
    	AlarmMessageDTO alarm = new AlarmMessageDTO();
        int result = clubApplyDAO.insert(clubApply);        // INSERT 수행
        
        if(result == 1) {
        	// ✅ 4. 알림 메시지 구성
        	alarm.setType("CLUB_APPLY");
        	alarm.setSenderId(clubApply.getAppli_user_no()); // 신청자 ID
        	alarm.setReceiverId(String.valueOf(user_no));                    // 팀장 ID
        	alarm.setClubId(clubApply.getClub_id());                        // 클럽 ID
        	alarm.setMessage(clubApply.getApply_id() + " 님이 클럽가입을 신청했습니다.");
        	// alarm.setUrl("/club/...") // 🔄 알림 클릭 시 이동할 URL (선택 구현)
        } else {
        	return null;
        }

        return alarm;
    }
}
