package com.msa.kyj_prj.kakaopay;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		return ResponseEntity.ok(result);
	}
}
