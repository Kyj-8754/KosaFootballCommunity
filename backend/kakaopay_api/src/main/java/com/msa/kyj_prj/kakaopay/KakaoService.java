package com.msa.kyj_prj.kakaopay;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoService {

	private final RestTemplate restTemplate;
	private static final String ADMIN_KEY = "91e1d1afa824dec2d285f98c15707b02";
	
	public Map<String, Object> paymentReady(KakaoPayRequestDTO param) {
		HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "KakaoAK " + ADMIN_KEY);
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

	    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	    body.add("cid", "TC0ONETIME");
	    body.add("partner_order_id", param.getPartnerOrderId());
	    body.add("partner_user_id", param.getPartnerUserId());
	    body.add("item_name", param.getItemName());
	    body.add("quantity", 1);
	    body.add("total_amount", String.valueOf(param.getTotalAmount()));
	    body.add("vat_amount", 0);
	    body.add("tax_free_amount", 0);
	    body.add("approval_url", "http://localhost:5173/payment/success");
	    body.add("cancel_url", "http://localhost:5173/payment/cancel");
	    body.add("fail_url", "http://localhost:5173/payment/fail");

	    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
	    
	    ResponseEntity<Map> response = restTemplate.postForEntity(
	            "https://kapi.kakao.com/v1/payment/ready",
	            request,
	            Map.class
	    );

	    return response.getBody();
	}

}
