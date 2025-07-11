package com.msa.kyj_prj.club.apply;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.msa.kyj_prj.alarm.AlarmMessageDTO;
import com.msa.kyj_prj.club.member.ClubMemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class ClubApplyService {

	private final ClubApplyDAO clubApplyDAO;
	private final RestTemplate restTemplate;
	private static final Logger log = LoggerFactory.getLogger(ClubApplyService.class);
	private final ClubMemberService clubMemberService;

	@Value("${alarm.api.url}")
	private String alarmApiUrl;

	// ClubMemberService 주입

	// ◆ 클럽 가입 신청 처리 (중복 방지, 알림 전송 포함)
	public AlarmMessageDTO applyToRecruit(ClubApply clubApply, int appli_user_no) {
		int bno = clubApply.getBno();

		// 팀장 user_no 조회
		int user_no = clubApplyDAO.findUserNoByBno(bno);
		if (user_no == 0)
			throw new IllegalStateException("팀장 정보를 찾을 수 없습니다.");

		// 팀장 본인 가입 금지
		if (appli_user_no == user_no)
			throw new IllegalStateException("팀장은 본인 클럽에 가입 신청할 수 없습니다.");

		// 기존 신청 이력 확인
		ClubApply exist = clubApplyDAO.findLastApplyByBnoAndApplicant(bno, appli_user_no);

		if (exist != null) {
			String status = exist.getStatus();

			// ✅ (1) pending/approved면 무조건 불가
			if ("pending".equals(status) || "approved".equals(status)) {
				throw new IllegalStateException("❌ 이미 가입 신청 중이거나 멤버입니다.");
			}
			// ✅ (2) 강퇴(kicked)는 영구 불가!
			if ("kicked".equals(status)) {
				throw new IllegalStateException("❌ 강퇴된 유저는 재가입이 불가합니다.");
			}
			// ✅ (3) 취소(canceled)는 24시간 이내면 불가, 경과시 pending 복원
			if ("canceled".equals(status)) {
				ClubApply canceled = clubApplyDAO.findRecentCanceledApply(bno, appli_user_no);
				if (canceled != null)
					throw new IllegalStateException("❌ 재가입 신청은 24시간 뒤에 해주세요.");
				int updated = clubApplyDAO.updateStatusToPending(bno, appli_user_no);
				if (updated != 1)
					throw new IllegalStateException("재신청 갱신에 실패했습니다.");
			}
			// ✅ (4) rejected/withdrawn 등은 신규 신청 허용 (아무것도 안하면 됨)
			// -- rejected/withdrawn 등은 여기서 예외를 던지지 않으므로 아래 신규 신청 로직으로 이동
		}

		// (신규 신청 또는 withdrawn/rejected/24시간 지난 canceled 상태)
		Integer clubId = clubApplyDAO.findClubIdByBno(clubApply.getBno());
		if (clubId == null) {
			log.warn("❗ club_id를 찾을 수 없음 (bno: {})", clubApply.getBno());
			return null;
		}
		clubApply.setClub_id(clubId);

		Integer userNo = clubApplyDAO.findUserNoByBno(clubApply.getBno());
		if (userNo == null) {
			log.warn("❗ 팀장 user_no를 찾을 수 없음 (bno: {})", clubApply.getBno());
			return null;
		}

		clubApply.setStatus("pending");
		int result;

		// ★ 여기서 bno 값으로 분기!
		if (clubApply.getBno() > 0) {
		    // 모집글(bno)이 있으면 기존 방식 사용
		    result = clubApplyDAO.insert(clubApply);
		} else {
		    // 클럽 상세에서 직접 신청(bno 없음)일 때는 insertByClubDetail 사용!
		    result = clubApplyDAO.insertByClubDetail(clubApply);
		}

		if (result != 1) {
		    log.error("❌ 클럽 신청 정보 저장 실패!");
		    return null;
		}


		String senderName = clubApplyDAO.findUserNameByUserNo(clubApply.getAppli_user_no());

		// 알림 메시지 전송 (실패해도 무방)
		Integer clubIdForAlarm = clubApplyDAO.findClubIdByBno(clubApply.getBno());
		Integer userNoForAlarm = clubApplyDAO.findUserNoByBno(clubApply.getBno());
		AlarmMessageDTO alarm = new AlarmMessageDTO();
		alarm.setType("CLUB_APPLY");
		alarm.setSenderId(String.valueOf(clubApply.getAppli_user_no()));
		alarm.setReceiverId(String.valueOf(userNoForAlarm));
		alarm.setClubId(clubIdForAlarm);
		alarm.setMessage(senderName + " 님이 클럽가입을 신청했습니다.");

		log.info("나 이거 보내걸임" + alarm.toString());

		try {
			String url = alarmApiUrl + "/api/alarm/send";
			ResponseEntity<Void> response = restTemplate.postForEntity(url, alarm, Void.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				log.info("✅ 알림 메시지 전송 성공! (팀장 user_no: {})", userNoForAlarm);
			} else {
				log.error("🔴 알림 메시지 전송 실패 (status: {})", response.getStatusCode());
			}
		} catch (Exception e) {
			log.error("🔴 알림 서버와 통신 중 예외 발생", e);
		}
		return alarm;
	}

	// ◆ club_id로 전체 신청자 목록 조회
	public List<ClubApply> findByClubId(int club_id) {
		return clubApplyDAO.findByClubId(club_id);
	}

	// ◆ club_id로 전체 신청자 + 이름 목록 조회
	public List<Map<String, Object>> findByClubIdWithUserName(int club_id) {
		return clubApplyDAO.findByClubIdWithUserName(club_id);
	}

	// ◆ 신청 취소 (pending만 취소 가능, approved는 불가)
	public boolean cancelApply(int bno, int appli_user_no) {
		ClubApply lastApply = clubApplyDAO.findLastApplyByBnoAndApplicant(bno, appli_user_no);
		if (lastApply == null)
			return false;
		String status = lastApply.getStatus();
		if ("approved".equals(status)) {
			throw new IllegalStateException("이미 승인된 신청은 취소할 수 없습니다.");
		}
		if (!"pending".equals(status)) {
			return false;
		}
		int updated = clubApplyDAO.cancelByBnoAndApplicant(bno, appli_user_no);
		return updated > 0;
	}

	// ◆ 최근 24시간 내 취소 이력 조회 (재신청 가능 여부)
	public boolean canReApply(int bno, int appli_user_no) {
		ClubApply canceled = clubApplyDAO.findRecentCanceledApply(bno, appli_user_no);
		return (canceled == null);
	}

	// ◆ 모집글 기준 최신 한 건 조회 (상태 관계 없음)
	public ClubApply findLastApplyByBnoAndApplicant(int bno, int appli_user_no) {
		return clubApplyDAO.findLastApplyByBnoAndApplicant(bno, appli_user_no);
	}

	// ◆ 클럽 가입 신청 승인 (apply status → approved, club_member 등록)
	public boolean approveApply(int apply_id) {
		int updated = clubApplyDAO.updateStatus(apply_id, "approved");
		if (updated > 0) {
			ClubApply apply = clubApplyDAO.findByApplyId(apply_id);
			if (apply != null) {
				int club_id = apply.getClub_id();
				int user_no = apply.getAppli_user_no();
				int leader_user_no = clubApplyDAO.findLeaderUserNoByClubId(club_id);

				insertClubMember(club_id, user_no);

				// [1] 승인 알림 전송 (로그 추가)
				AlarmMessageDTO alarm = new AlarmMessageDTO();
				alarm.setType("JOIN_APPROVED"); // 문자열 상수로 직접 세팅!
				alarm.setSenderId(String.valueOf(leader_user_no)); // 또는 팀장 user_no
				alarm.setReceiverId(String.valueOf(user_no)); // 알림 받을 사람(= 승인된 멤버)
				alarm.setClubId(club_id);
				alarm.setMessage("클럽 가입이 승인되었습니다!");

				try {
					// [🔍 로그 추가] 실제로 보낼 JSON 찍기
					com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
					String json = mapper.writeValueAsString(alarm);
					System.out.println("🔼 RestTemplate 전송 JSON: " + json);

					String url = alarmApiUrl + "/alarm/send";
					org.springframework.http.ResponseEntity<Void> response = restTemplate.postForEntity(url, alarm,
							Void.class);
					if (response.getStatusCode().is2xxSuccessful()) {
						log.info("✅ 알림 메시지 전송 성공! (user_no: {})", user_no);
					} else {
						log.error("🔴 알림 메시지 전송 실패 (status: {})", response.getStatusCode());
					}
				} catch (Exception e) {
					log.error("🔴 승인 알림 전송 실패", e);
				}
				// [1] 추가 끝
			}
			return true;
		}
		return false;
	}

	// ◆ 가입 신청 거절
	public boolean rejectApply(int apply_id) {
		int updated = clubApplyDAO.updateStatus(apply_id, "rejected");
		if (updated > 0) {
			ClubApply apply = clubApplyDAO.findByApplyId(apply_id);
			if (apply != null) {
				int club_id = apply.getClub_id();
				int user_no = apply.getAppli_user_no();
				int leader_user_no = clubApplyDAO.findLeaderUserNoByClubId(club_id);

				// [1] 거절 알림 전송
				AlarmMessageDTO alarm = new AlarmMessageDTO();
				alarm.setType("JOIN_REJECTED"); // 문자열 상수로 직접 세팅!
				alarm.setSenderId(String.valueOf(leader_user_no)); // 또는 팀장 user_no
				alarm.setReceiverId(String.valueOf(user_no)); // 알림 받을 사람(= 거절된 멤버)
				alarm.setClubId(club_id);
				alarm.setMessage("클럽 가입이 거절되었습니다.");

				try {
					// [🔍 로그] RestTemplate 전송 JSON 출력
					com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
					String json = mapper.writeValueAsString(alarm);
					System.out.println("🔼 RestTemplate 전송 JSON: " + json);

					String url = alarmApiUrl + "/alarm/send";
					org.springframework.http.ResponseEntity<Void> response = restTemplate.postForEntity(url, alarm,
							Void.class);
					if (response.getStatusCode().is2xxSuccessful()) {
						log.info("✅ 거절 알림 메시지 전송 성공! (user_no: {})", user_no);
					} else {
						log.error("🔴 거절 알림 메시지 전송 실패 (status: {})", response.getStatusCode());
					}
				} catch (Exception e) {
					log.error("🔴 거절 알림 전송 실패", e);
				}
				// [1] 추가 끝
			}
			return true;
		}
		return false;
	}

	// ◆ 클럽 멤버 테이블에 실제 등록 (승인 시 사용)
	public int insertClubMember(int club_id, int user_no) {
		return clubApplyDAO.insertClubMember(club_id, user_no);
	}

	// ◆ 멤버 탈퇴 처리 (apply status: approved → withdrawn)
	public boolean withdrawClubMember(int club_id, int appli_user_no) {
		clubMemberService.deleteByClubIdAndUserNo(club_id, appli_user_no);
		int updated = clubApplyDAO.updateWithdrawnStatus(club_id, appli_user_no);

		if (updated > 0) {
			// ⬇️ 클럽 리더 user_no 조회
			Integer leaderUserNo = clubApplyDAO.findLeaderUserNoByClubId(club_id); // 바로 사용!

			AlarmMessageDTO alarm = new AlarmMessageDTO();
			alarm.setType("CLUB_WITHDRAW");
			alarm.setSenderId(String.valueOf(appli_user_no)); // 탈퇴한 멤버
			alarm.setReceiverId(String.valueOf(leaderUserNo)); // 팀장(user_no)
			alarm.setClubId(club_id);
			alarm.setMessage("[" + appli_user_no + "]님이 클럽을 탈퇴했습니다."); // 닉네임 활용도 OK

			try {
				String url = alarmApiUrl + "/alarm/send";
				restTemplate.postForEntity(url, alarm, Void.class);
			} catch (Exception e) {
				log.error("🔴 탈퇴 알림 전송 실패", e);
			}
		}
		return updated > 0;
	}

	// ◆ 멤버 강퇴 처리 (apply status: approved → kicked)
	public boolean kickClubMember(int club_id, int appli_user_no) {
		// (1) club_member 논리삭제(별도 서비스에서 left_at 갱신)
		clubMemberService.deleteByClubIdAndUserNo(club_id, appli_user_no);
		// (2) apply status 변경
		int updated = clubApplyDAO.updateKickedStatus(club_id, appli_user_no);

		if (updated > 0) {
			// [1] 강퇴 알림 전송
			AlarmMessageDTO alarm = new AlarmMessageDTO();
			alarm.setType("CLUB_KICKED");
			alarm.setSenderId("SYSTEM"); // 또는 실제 처리자 user_no
			alarm.setReceiverId(String.valueOf(appli_user_no)); // 강퇴된 멤버 본인
			alarm.setClubId(club_id);
			alarm.setMessage("클럽에서 강퇴 처리되었습니다.");

			try {
				com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
				String json = mapper.writeValueAsString(alarm);
				System.out.println("🔼 RestTemplate 전송 JSON(강퇴): " + json);

				String url = alarmApiUrl + "/alarm/send";
				org.springframework.http.ResponseEntity<Void> response = restTemplate.postForEntity(url, alarm,
						Void.class);

				if (response.getStatusCode().is2xxSuccessful()) {
					log.info("✅ 강퇴 알림 메시지 전송 성공! (user_no: {})", appli_user_no);
				} else {
					log.error("🔴 강퇴 알림 메시지 전송 실패 (status: {})", response.getStatusCode());
				}
			} catch (Exception e) {
				log.error("🔴 강퇴 알림 전송 실패", e);
			}
			// [1] 끝
		}

		return updated > 0;
	}

	// ◆ 해당 유저가 이미 approved(클럽 멤버)인지 체크
	public boolean isAlreadyClubMember(int club_id, int appli_user_no) {
		return clubApplyDAO.countApprovedMember(club_id, appli_user_no) > 0;
	}

	// ◆ 해당 유저가 현재 신청중(pending)인지 체크
	public boolean isAlreadyPendingApply(int club_id, int appli_user_no) {
		return clubApplyDAO.countPendingApply(club_id, appli_user_no) > 0;
	}

	// ////////////////////////////////////////////////////////////////////////
	
	// ◆ club_id, appli_user_no 기준으로 가장 최근 신청 이력 조회
	public ClubApply findLastApplyByClubIdAndApplicant(int club_id, int appli_user_no) {
		return clubApplyDAO.findLastApplyByClubIdAndApplicant(club_id, appli_user_no);
	}

	// ◆ club_id, appli_user_no 기준 신규 신청 (중복, 강퇴, 24시간 재신청 등 로직 포함)
	public AlarmMessageDTO applyToClub(ClubApply clubApply, int appli_user_no) {
	    int club_id = clubApply.getClub_id();

	    // 팀장 user_no 조회
	    Integer leader_user_no = clubApplyDAO.findLeaderUserNoByClubId(club_id);
	    if (leader_user_no == null)
	        throw new IllegalStateException("팀장 정보를 찾을 수 없습니다.");

	    // 팀장 본인 가입 금지
	    if (appli_user_no == leader_user_no)
	        throw new IllegalStateException("팀장은 본인 클럽에 가입 신청할 수 없습니다.");

	    // 기존 신청 이력 확인
	    ClubApply exist = clubApplyDAO.findLastApplyByClubIdAndApplicant(club_id, appli_user_no);
	    if (exist != null) {
	        String status = exist.getStatus();
	        if ("pending".equals(status) || "approved".equals(status)) {
	            throw new IllegalStateException("❌ 이미 가입 신청 중이거나 멤버입니다.");
	        }
	        if ("kicked".equals(status)) {
	            throw new IllegalStateException("❌ 강퇴된 유저는 재가입이 불가합니다.");
	        }
	        if ("canceled".equals(status)) {
	            // 24시간 이내 최근 취소 확인
	            ClubApply canceled = clubApplyDAO.findRecentCanceledApply(club_id, appli_user_no);
	            if (canceled != null)
	                throw new IllegalStateException("❌ 재가입 신청은 24시간 뒤에 해주세요.");

	            int updated = clubApplyDAO.updateStatusToPending(club_id, appli_user_no);
	            if (updated != 1)
	                throw new IllegalStateException("재신청 갱신에 실패했습니다.");

	            // 재신청 갱신 완료 → insert 불필요
	            return null;
	        }
	        // rejected, withdrawn 등은 신규 신청 허용 (insert 진행)
	    }

	    // 신규 신청 등록
	    clubApply.setStatus("pending");

	    int result;
	    if (clubApply.getBno() > 0) {
	        result = clubApplyDAO.insert(clubApply);
	    } else {
	        System.out.println(">>> [insert] clubApply: " + clubApply);
	        result = clubApplyDAO.insertByClubDetail(clubApply);
	    }

	    if (result != 1)
	        throw new IllegalStateException("❌ 클럽 신청 정보 저장 실패!");

	    // (알림 메시지 전송 등 필요 시 여기에 추가)
	    return null;
	}


	// ◆ club_id, appli_user_no 기준 신청 취소 (pending만)
	public boolean cancelApplyByClubId(int club_id, int appli_user_no) {
		ClubApply lastApply = clubApplyDAO.findLastApplyByClubIdAndApplicant(club_id, appli_user_no);
		if (lastApply == null)
			return false;
		String status = lastApply.getStatus();
		if ("approved".equals(status)) {
			throw new IllegalStateException("이미 승인된 신청은 취소할 수 없습니다.");
		}
		if (!"pending".equals(status)) {
			return false;
		}
		int updated = clubApplyDAO.cancelByClubIdAndApplicant(club_id, appli_user_no);
		return updated > 0;
	}

}
