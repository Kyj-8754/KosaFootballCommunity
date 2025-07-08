package com.msa.kyj_prj;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.msa.kyj_prj.reservation.ReservationDAO;
import com.msa.kyj_prj.reservation.ReservationService;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
	@InjectMocks // 테스트 대상 클래스 (실제 객체)
	private ReservationService reservationService;

	@Mock // 가짜 DAO
	private ReservationDAO reservationDAO;
	
	@Test
	void getReservationForm_파라미터누락_예외발생() {
		// 파라미터 필요없는거
	    assertThrows(IllegalArgumentException.class, () -> {
	        reservationService.getReservationForm(null, "2025-07-08");
	    });
	
	    assertThrows(IllegalArgumentException.class, () -> {
	        reservationService.getReservationForm("S123", "");
	    });
	}
	
	@Test
	void cancelReservation_상태예약아님_예외발생() {
		// 예약상태 관련 서비스
	    Map<String, Object> reservationMap = Map.of(
	        "reservation_id", 1001,
	        "status", "cancelled"
	    );
	    Map<String, Object> param = Map.of("reservation", reservationMap);
	
	    assertThrows(IllegalStateException.class, () -> {
	        reservationService.cancelReservation(param);
	    });
	}
	
	@Test
	void expiredReservation_정상호출() {
	    reservationService.expiredReservation();
	    verify(reservationDAO).expiredReservation();
	}
}
