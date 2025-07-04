package com.msa.kyj_prj.reservation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	public ResponseEntity<Map<String, Object>> reservation_std(@RequestBody Reservation reservation) {
		
		if (reservation == null) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Missing parameters"));
	    }
		Map<String, Object> resultJson = new HashMap<>();
		
		try {
	            int result = reservationService.reservation(reservation);
	            
	            if(result ==1 ) {
	            	resultJson.put("res_code", "200");
	            	resultJson.put("res_msg", "예약이 완료 되었습니다.");
	            	resultJson.put("reservation_id", reservation.getReservation_id());
	            	return ResponseEntity.ok(resultJson);
	            } else {
	            	resultJson.put("res_code", "400");
	            	resultJson.put("res_msg", "잘못된 요청: ");
	                 return ResponseEntity.badRequest().body(resultJson);
	            }
            } catch (Exception e) {
            	resultJson.put("res_code", "500");
            	resultJson.put("res_msg", "서버 오류 발생 관리자 문의 필요: " + e.getMessage());
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultJson);
        }
	}
	
	// 예약 리스트 노출
	@PostMapping("list")
	public ResponseEntity<Map<String, Object>> getresrvationList(@RequestBody Map<String, Object> param){
		
		Map<String, Object> result = new HashMap<>();
		
		 try {
			 	result.put("reservationList",reservationService.getReservationList(param.get("user_no").toString()));
	            result.put("res_code", "200");
	            result.put("res_msg", "예약 리스트 조회 성공");
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
	
	// 예약창으로
	@PostMapping("reservation_confirm")
	public ResponseEntity<Map<String, Object>> getreservation(@RequestBody Map<String, Object> param) {
		 long reservation_id = Long.parseLong(param.get("reservation_id").toString());
//		if (reservatio long reservation_id = Long.parseLong(param.get("reservation_id").toString());n_id == null) {
//	        return ResponseEntity.badRequest().body(Map.of("error", "Missing parameters"));
//	    }
		
		System.out.println(reservation_id);
		Map<String, Object> result = new HashMap<>();
		
		 try {
			 List<Reservation> reservationDB = reservationService.getreservation(reservation_id);
	            result.put("res_code", "200");
	            result.put("res_msg", "예약 슬롯 조회 성공");
	            result.put("reservationDB", reservationDB);
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
}
