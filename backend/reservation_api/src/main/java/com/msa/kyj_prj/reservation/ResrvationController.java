package com.msa.kyj_prj.reservation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ResrvationController {

	private final ReservationService reservationService;


	// 예약 등록 폼
	@PostMapping("reservationForm")
	public Map<String, Object> list(String SVCID, String date, String userNo) {
		
		
		
		Map<String, Object> result = new HashMap<>();
		reservationService.getReservationForm(SVCID, date, userNo);
		
			return result;
	}
}
