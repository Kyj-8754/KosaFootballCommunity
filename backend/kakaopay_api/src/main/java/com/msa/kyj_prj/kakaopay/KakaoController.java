package com.msa.kyj_prj.kakaopay;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.kyj_prj.dto.KakaoPayRequestDTO;
import com.msa.kyj_prj.dto.PaymentDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/kakaopay")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "kakaopay", description = "카카오페이 관련 API" )
public class KakaoController {
	
	@Value("${base.url}")
	private String baseUrl;
	private final KakaoService kakaoService;

	// 카카오 api 결제
	@PostMapping("/ready")
	@Operation(summary = "결제 준비", description = "예약 데이터를 토대로 카카오 결제 준비단계 API")
	public ResponseEntity<Map<String, Object>> payment(@RequestBody KakaoPayRequestDTO param) {
		log.info("결제 시작");
		try {
			Map<String, Object> result = kakaoService.paymentReady(param);
			return ResponseEntity.ok(result);
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(Map.of("error", true, "message", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
					.body(Map.of("error", true, "message", "서버 오류가 발생했습니다: " + e.getMessage()));
		}
	}

	// 결제 승인 처리
	@GetMapping("/success")
	@Operation(summary = "결제 승인처리", description = "카카오 결제 승인 처리 API")
	public void kakaoPaySuccess(HttpServletResponse response, @RequestParam String pg_token, @RequestParam Long id)
			throws IOException {

		try {
			log.info("카카오페이 결제 승인 요청 시작");
			kakaoService.approve(pg_token, id);
			response.sendRedirect("http://"+ baseUrl +"/payment/success.html");
		} catch (Exception e) {
			log.error("결제 승인 실패", e);
			response.sendRedirect("http://"+ baseUrl +"/payment/fail.html");
		}
	}

	// 결제 실패 처리
	@GetMapping("/fail")
	@Operation(summary = "결제 실패", description = "카카오 결제 실패 처리 API")
	public void kakaoPayFail(HttpServletResponse response, @RequestParam Long id) throws IOException {

		// 2. 실패 처리
		kakaoService.fail(id);

		// 클라이언트로 redirect
		response.sendRedirect("http://"+ baseUrl +"/payment/fail.html");
	}

	// 결제 취소 처리
	@GetMapping("/cancel")
	@Operation(summary = "결제 취소", description = "카카오 결제 취소 처리 API")
	public void kakaoPayCancel(HttpServletResponse response, @RequestParam Long id)
			throws IOException {
		// 2. 실패 처리
		kakaoService.cancel(id);
		// 클라이언트로 redirect
		response.sendRedirect("http://"+baseUrl+"/payment/cancel.html");
	}

	// 결제 환불
	@PostMapping("/refund")
	@Operation(summary = "결제 환불", description = "카카오 결제 데이터 기준으로 환불하는 API")
	public ResponseEntity<Map<String, Object>> kakaoRefund(@RequestBody Map<String, Object> param) {
		Object userNoObj = param.get("user_no");
		String user_no = String.valueOf(userNoObj);
		ObjectMapper mapper = new ObjectMapper();
		PaymentDTO dto = mapper.convertValue(param.get("reservation"), PaymentDTO.class);
		
		log.info("제대로 받음"+user_no);
	    try {
	        kakaoService.refund(dto, user_no);
	        return ResponseEntity.ok(Map.of("success", true));
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message",  "시스템 오류가 발생했습니다."));
	    }
	}
}
