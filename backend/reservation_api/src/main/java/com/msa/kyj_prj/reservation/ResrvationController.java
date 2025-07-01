package com.msa.kyj_prj.reservation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msa.kyj_prj.dto.SlotDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
@Slf4j
public class ResrvationController {

	private final ReservationService reservationService;


	// 예약 등록폼
	@PostMapping("reservationForm")
	public ResponseEntity<Map<String, Object>> reservationForm(@RequestBody Map<String, String> params) {
		String svcid = params.get("SVCID");
	    String date = params.get("date");
		
	    // 1차 검증
	    if (svcid == null || date == null) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Missing parameters"));
	    }
	    
		Map<String, Object> result = new HashMap<>();
		try {
			
            List<SlotDTO> slots = reservationService.getReservationForm(svcid, date);
            

            result.put("res_code", "200");
            result.put("res_msg", "예약 슬롯 조회 성공");
            result.put("slots", slots);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            result.put("res_code", "400");
            result.put("res_msg", "잘못된 요청: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            result.put("res_code", "500");
            result.put("res_msg", "서버 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
	}
	
	
	// 예약DB 저장
	@PostMapping("reservation_std")
	public ResponseEntity<String> reservation_std(@RequestBody Reservation reservation) {
		if (reservation == null) {
	        return ResponseEntity
	            .badRequest()
	            .body("잘못된 요청입니다. 예약 정보가 없습니다.");
	    }
		
		try {
	            int result = reservationService.reservation(reservation);
	            
	            if(result ==1 ) {
	            	return ResponseEntity.ok("예약이 완료되었습니다.");
	            } else {
	                return ResponseEntity.ok("예약에 실패하였습니다.");
	            }
            } catch (Exception e) {
 
            return ResponseEntity
            		.status(HttpStatus.INTERNAL_SERVER_ERROR)
            		.body("서버 오류:" + e.getMessage() + "관리자에게 문의해주세요");
        }
	}
}
