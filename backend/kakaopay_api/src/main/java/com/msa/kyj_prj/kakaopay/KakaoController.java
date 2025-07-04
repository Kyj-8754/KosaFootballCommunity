package com.msa.kyj_prj.kakaopay;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<Map<String, Object>> payment(@RequestBody KakaoPayRequestDTO param){
		log.info("결제 시작");
		Map<String, Object> result = kakaoService.paymentReady(param);
	    return ResponseEntity.ok(result); // 성공 응답 (tid, redirect_url 포함)
	}
	
	// 결제 승인 처리
	@GetMapping("/success")
	public void kakaoPaySuccess(HttpServletResponse response, @RequestParam String pg_token, @RequestParam Long id)
	throws IOException{
	    
		log.info("카카오페이 결제 승인 요청 시작");
		
	    // 1. 승인 처리
	    kakaoService.approve(pg_token, id);

	    // 클라이언트로 redirect
	    response.sendRedirect("http://localhost:5173/#/payment/success"); 
	}
	
	// 결제 실패
	@GetMapping("/fail")
	public String kakaoPayFail() {
	    return "redirect:http://localhost:5173/payment/fail"; 
	}

	// 결제 취소
	@GetMapping("/cancel")
	public String kakaoPayCancel() {
	    return "redirect:http://localhost:5173/payment/cancel";
	}
}
