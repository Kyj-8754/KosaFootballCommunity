package com.msa.kyj_prj.kakaopay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/kakaopay")
@RequiredArgsConstructor
@Slf4j
public class KakaoController {

	private final KakaoService kakaoService;

	// 카카오 api 결제
	@PostMapping("/ready")
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
	public void kakaoPaySuccess(HttpServletResponse response, @RequestParam String pg_token, @RequestParam Long id)
			throws IOException {

		try {
			log.info("카카오페이 결제 승인 요청 시작");
			kakaoService.approve(pg_token, id);
			response.sendRedirect("http://localhost:5173/payment/success");
		} catch (Exception e) {
			log.error("결제 승인 실패", e);
			response.sendRedirect("http://localhost:5173/payment/fail");
		}
	}

	// 결제 실패 처리
	@GetMapping("/fail")
	public void kakaoPayFail(HttpServletResponse response, @RequestParam Long id) throws IOException {

		// 2. 실패 처리
		kakaoService.fail(id);

		// 클라이언트로 redirect
		response.sendRedirect("http://localhost:5173/payment/fail");
	}

	// 결제 취소 처리
	@GetMapping("/cancel")
	public void kakaoPayCancel(HttpServletResponse response, @RequestParam Long id)
			throws IOException {
		// 2. 실패 처리
		kakaoService.cancel(id);
		// 클라이언트로 redirect
		response.sendRedirect("http://localhost:5173/payment/cancel");
	}

	// 결제 환불
	@SuppressWarnings("unchecked") // 서비스에서 검증할것
	@PostMapping("/refund")
	public ResponseEntity<Map<String, Object>> kakaoRefund(@RequestBody Map<String, Object> param) {
		Map<String, Object> result = new HashMap<>();
	    
	    try {
	        kakaoService.refund((Map<String, Object>) param.get("reservation"));
	        return ResponseEntity.ok(Map.of("success", true));
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
	    }
	}
}
