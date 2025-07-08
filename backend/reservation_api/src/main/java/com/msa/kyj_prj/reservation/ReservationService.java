package com.msa.kyj_prj.reservation;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.msa.kyj_prj.dto.ReservationDTO;
import com.msa.kyj_prj.dto.SlotDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService{
	private final ReservationDAO reservationDAO;
	
	// 예약 등록 폼
	public List<SlotDTO> getReservationForm(String SVCID, String date) {
		// 예외처리
		if (SVCID == null || SVCID.isEmpty() || date == null || date.isEmpty()) {
            throw new IllegalArgumentException("SVCID 또는 날짜 정보가 누락되었습니다.");
        }
		try {
	        List<SlotDTO> list = reservationDAO.getReservationForm(SVCID, date);
	        
	        return list;
	    } catch (Exception e) {
	        log.error("슬롯 조회 중 오류 발생", e);
	        throw e;  // 혹은 ResponseStatusException 등으로 변환
	    }
	}
	
	// 예약 하기
	public int reservation(Reservation reservation) {
		return reservationDAO.reservation(reservation);
	}
	
	
	// 예약 취소
	public void cancelReservation(Map<String, Object> param) {
		// 받은 파라미터를 이용해서 검증확인
		Map<String, Object> reservationMap = (Map<String, Object>) param.get("reservation");
//		String user_no = param.get("user_no").toString();
//		String re_user_no = reservationMap.get("user_no").toString();
	    String status = (String) reservationMap.get("status");
	    
	    log.info("검증로직 시작");
	    // 예약된 상태만 취소 가능
	    if (!"reserved".equalsIgnoreCase(status)) {
	        throw new IllegalStateException("예약된 상태에서만 취소할 수 있습니다.");
	    }

	    int reservation_id = (Integer)reservationMap.get("reservation_id");
	    
	    System.out.println("DAO 가기전임" + reservation_id);
	    reservationDAO.updateStatusToCancelled(reservation_id);
	}
	
	// 예약 리스트
	public List<ReservationDTO> getReservationList(String user_no){
		return reservationDAO.getReservationList(user_no);
	}
	
	// 예약된거 가져오기
	public ReservationDTO getreservation(Long reservation_id) {
		return reservationDAO.getReservations(reservation_id);
	}
	
	// 결제 리스트
	public List<ReservationDTO> getPaymentList(String user_no){
		return reservationDAO.getPaymentList(user_no);
	}
	
	// 예약에 board_id 연결
	public void updateBoardId(Long reservationId, Long boardId) {
	    if (reservationId == null || boardId == null) {
	        throw new IllegalArgumentException("reservationId 또는 boardId가 null입니다.");
	    }

	    log.info("예약 [{}]에 게시글 [{}] 연결 중", reservationId, boardId);
	    reservationDAO.updateBoardId(reservationId, boardId);
	}
	
	// 10분 기점으로 예약 만료
	@Scheduled(cron = "0 0/10 * * * ?", zone = "Asia/Seoul")
	public void expiredReservation() {
		log.info("예약 만료 스케쥴러 시작");
		try {
	        reservationDAO.expiredReservation();
	        log.info("예약 만료 작업 완료");
	    } catch (Exception e) {
	        log.error("예약 만료 스케쥴러 오류 발생", e);
	    }
	}
	
}


