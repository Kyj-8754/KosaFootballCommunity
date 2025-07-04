package com.msa.kyj_prj.reservation;
import java.util.List;

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
	
	// 예약된거 가져오기
	public List<Reservation> getreservation(Long reservation_id) {
		return reservationDAO.getReservations(reservation_id);
	}
	
	// 예약 리스트
	public List<ReservationDTO> getReservationList(String user_no){
		return reservationDAO.getReservationList(user_no);
	}
	
}


