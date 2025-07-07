package com.msa.kyj_prj.match;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.msa.kyj_prj.match.alarm.MatchAlarmMessageDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {

	private final MatchDAO matchDAO;
	private final RestTemplate restTemplate;

	@Value("${alarm.api.url}")
	private String alarmApiUrl;

	// 소셜매치 목록 호출
	public List<Match> getSocialMatches(Map<String, Object> params) {
		params.put("match_code", "social");
		return matchDAO.selectFilteredMatches(params);
	}

	// 리그매치 목록 호출
	public List<Match> getLeagueMatches(Map<String, Object> params) {
		params.put("match_code", "league");
		return matchDAO.selectFilteredMatches(params);
	}

	// 매치 상세보기
	public Match getMatchDetail(Long matchId) {
		return matchDAO.selectMatchDetailById(matchId);
	}

	// 매치 참가 신청
	public void applyToMatch(MatchParticipant participant) {
		Long matchId = participant.getMatch_id();

		// 매치 정보 조회
		Match match = matchDAO.selectMatchDetailById(matchId);
		String matchCode = match.getMatch_code();

		// 현재 참가 인원(소셜) 또는 클럽 수(리그)
		int currentCount = matchDAO.countMatchParticipants(matchId);

		// 조건 체크
		if ("social".equalsIgnoreCase(matchCode) && currentCount >= 18) {
			throw new IllegalStateException("소셜 매치는 최대 18명까지만 참가할 수 있습니다.");
		}

		if ("league".equalsIgnoreCase(matchCode) && currentCount >= 3) {
			throw new IllegalStateException("리그 매치는 최대 3팀까지만 참가할 수 있습니다.");
		}

		// 조건 통과 시 참가 신청
		matchDAO.insertMatchParticipant(participant);

		// [1] 매치 주최자 정보
		Integer managerUserNo = match.getUser_no(); // 주최자

		// [2] 신청자 닉네임 조회 (선택)
		String applicantName = "";
		List<Map<String, Object>> participants = matchDAO.selectParticipantsByMatchId(participant.getMatch_id());
		for (Map<String, Object> p : participants) {
			if (p.get("user_no").equals(participant.getUser_no())) {
				applicantName = (String) p.get("user_name");
				break;
			}
		}

		// [3] 알람 DTO 생성 및 전송
		try {
			MatchAlarmMessageDTO alarm = new MatchAlarmMessageDTO();
			alarm.setType("MATCH_APPLY");
			alarm.setSenderId(String.valueOf(participant.getUser_no()));
			alarm.setReceiverId(String.valueOf(managerUserNo));
			alarm.setMatchId(participant.getMatch_id());
			alarm.setMatchTitle(match.getMatch_title());
			alarm.setMessage(applicantName + "님이 [" + match.getMatch_title() + "] 경기에 참가 신청했습니다.");
			alarm.setUrl("/match/" + match.getMatch_id());

			restTemplate.postForEntity(alarmApiUrl + "/alarm/send", alarm, Void.class);
		} catch (Exception e) {
			// 알람 실패해도 신청 로직엔 영향 없음 (필요시 로깅)
		}
	}

	// 매치 참가 여부 확인
	public boolean hasUserApplied(Long matchId, Integer userNo) {
		Map<String, Object> param = new HashMap<>();
		param.put("match_id", matchId);
		param.put("user_no", userNo);
		return matchDAO.checkUserApplied(param) > 0;
	}

	// 매치 신청 취소
	public void cancelMatchParticipant(Long matchId, Integer userNo) {
		Map<String, Object> param = new HashMap<>();
		param.put("match_id", matchId);
		param.put("user_no", userNo);
		matchDAO.cancelMatchParticipant(param);
	}

	// 매치 인원 수 조회
	public int getMatchParticipantCount(Long matchId) {
		return matchDAO.countMatchParticipants(matchId);
	}

	// 유저 번호로 클럽 정보 조회
	public Map<String, Object> getClubByUserNo(Long userNo) {
		return matchDAO.selectClubByUserNo(userNo);
	}

	// 지역명 리스트 조회
	public List<String> getAllAreanms() {
		return matchDAO.selectDistinctAreanms();
	}

	// 특정 매치의 참가자 + 사용자 이름 조회
	public List<Map<String, Object>> getMatchParticipantsWithNames(Long matchId) {
		return matchDAO.selectParticipantsByMatchId(matchId);
	}

	// 특정 매치의 참가자 + 사용자 이름 + 클럽 명 조회
	public List<Map<String, Object>> selectParticipantsWithClubByMatchId(Long matchId) {
		return matchDAO.selectParticipantsWithClubByMatchId(matchId);
	}

	// 매치 참가자 상태 업데이트
	public int updateMatchParticipantStatus(Map<String, Object> param) {
		return matchDAO.updateMatchParticipantStatus(param);
	}

	// 마감 처리
	public void closeMatch(Long matchId) {
		Map<String, Object> param = new HashMap<>();
		param.put("match_id", matchId);
		param.put("match_closed", "closed");
		matchDAO.updateMatchClosedStatus(param);
	}

	@Scheduled(cron = "0 0 */2 * * *") // 매 2시간마다 실행
	public void activatePastMatches() {
		System.out.println("예약 실행");
		List<Match> allMatches = matchDAO.selectFilteredMatches(new HashMap<>());
		LocalDateTime now = LocalDateTime.now();
		LocalDate today = now.toLocalDate();

		for (Match match : allMatches) {
			LocalDateTime matchDateTime = match.getMatch_date(); // OK
			String status = match.getMatch_status();

			// 1단계: 'waiting' + 오늘이거나 이전 날짜 → 'active'
			if ("waiting".equalsIgnoreCase(status) && !matchDateTime.toLocalDate().isAfter(today)) {
				Map<String, Object> param = new HashMap<>();
				param.put("match_id", match.getMatch_id());
				param.put("match_status", "active");
				matchDAO.updateMatchStatus(param);
			}

			// 2단계: 'active' + 2시간 경과 → 'completed'
			else if ("active".equalsIgnoreCase(status) && matchDateTime.plusHours(2).isBefore(now)) {
				Map<String, Object> param = new HashMap<>();
				param.put("match_id", match.getMatch_id());
				param.put("match_status", "completed");
				matchDAO.updateMatchStatus(param);
			}
		}
	}
}
