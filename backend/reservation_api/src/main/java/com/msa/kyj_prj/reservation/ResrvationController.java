package com.msa.kyj_prj.reservation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msa.kyj_prj.dto.ReservationDTO;
import com.msa.kyj_prj.dto.SlotDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservation", description = "예약/결제 정보 관련 API")
public class ResrvationController {

	private final ReservationService reservationService;


	// 예약 슬롯 가져오기
	@Operation(summary = "예약 슬롯 정보", description = "SVCID와 날짜를 기준으로 예약 가능한 슬롯 정보를 조회합니다.",
			responses = {
					@ApiResponse(responseCode = "200", description = "예약슬롯 조회 성공",
							content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = SlotDTO.class))),
					@ApiResponse(responseCode = "400", description = "잘못된 요청"),
					@ApiResponse(responseCode = "500", description = "서버 오류 발생")
			})
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
	@Operation(summary = "예약 등록", description = "예약 정보를 DB에 저장합니다.",
			responses = {
					@ApiResponse(responseCode = "200", description = "예약 성공",
							content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Reservation.class))),
					@ApiResponse(responseCode = "400", description = "잘못된 요청"),
					@ApiResponse(responseCode = "500", description = "서버 오류 발생")
			})
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
	@Operation(summary = "예약 목록 조회", description = "해당 유저의 전체 예약 목록을 반환합니다.",
			responses = {
					@ApiResponse(responseCode = "200", description = "예약 목록 반환 성공",
							content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ReservationDTO.class))),
					@ApiResponse(responseCode = "400", description = "잘못된 요청"),
					@ApiResponse(responseCode = "500", description = "서버 오류 발생")
			})
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
	@Operation(summary = "예약 정보", description = "해당 예약의 정보를 반환합니다.",
			responses = {
					@ApiResponse(responseCode = "200", description = "예약 정보 조회 성공",
							content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ReservationDTO.class))),
					@ApiResponse(responseCode = "400", description = "잘못된 요청"),
					@ApiResponse(responseCode = "500", description = "서버 오류 발생")
			})
	@PostMapping("reservation_confirm")
	public ResponseEntity<Map<String, Object>> getreservation(@RequestBody Map<String, Object> param) {
		 long reservation_id = Long.parseLong(param.get("reservation_id").toString());
		
		System.out.println(reservation_id);
		Map<String, Object> result = new HashMap<>();
		
		 try {
			 ReservationDTO reservationDB = reservationService.getreservation(reservation_id);
	            result.put("res_code", "200");
	            result.put("res_msg", "예약 정보 조회 성공");
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
	
	// 결제 리스트 노출
	@Operation(summary = "결제 리스트", description = "해당 유저의 전체 결제 목록을 반환합니다.",
			responses = {
					@ApiResponse(responseCode = "200", description = "결제 목록 조회 성공",
							content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ReservationDTO.class))),
					@ApiResponse(responseCode = "400", description = "잘못된 요청"),
					@ApiResponse(responseCode = "500", description = "서버 오류 발생")
			})
	@PostMapping("paymet_list")
	public ResponseEntity<Map<String, Object>> getpaymentList(@RequestBody Map<String, Object> param){
		
		Map<String, Object> result = new HashMap<>();
		
		 try {
			 	result.put("reservationList",reservationService.getPaymentList(param.get("user_no").toString()));
	            result.put("res_code", "200");
	            result.put("res_msg", "결제 목록 조회 성공");
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
	
	// 예약 취소
	@Operation(summary = "예약 취소", description = "예약을 취소 처리합니다.",
			responses = {
				    @ApiResponse(responseCode = "200", description = "예약이 정상적으로 취소됨"),
				    @ApiResponse(responseCode = "400", description = "잘못된 요청 (이미 취소된 예약 등)"),
				    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
				  })
	@PostMapping("cancel")
	public ResponseEntity<Map<String, Object>> cancle(@RequestBody Map<String, Object> param) {
		 log.info("예약 취소 로직입니다."); 
		 try {
		        reservationService.cancelReservation(param);
		        return ResponseEntity.ok(Map.of(
		            "error", false,
		            "message", "예약이 정상적으로 취소되었습니다."
		        ));
		    } catch (IllegalStateException e) {
		        return ResponseEntity.badRequest().body(Map.of(
		            "error", true,
		            "message", e.getMessage()
		        ));
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
		            "error", true,
		            "message", "서버 오류가 발생했습니다."
		        ));
		    }
	}
	
	// board_id 추가
	@PostMapping("updateBoard")
	public ResponseEntity<Map<String, Object>> updateBoardId(@RequestBody Map<String, Object> param) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        Long reservationId = Long.parseLong(param.get("reservation_id").toString());
	        Long boardId = Long.parseLong(param.get("board_id").toString());

	        reservationService.updateBoardId(reservationId, boardId);

	        result.put("res_code", "200");
	        result.put("res_msg", "예약과 게시글 연결 완료");
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
