package com.msa.kyj_prj.kakaopay;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
	private final KakaoDAO kakaoDAO;
	private static final String ADMIN_KEY = "91e1d1afa824dec2d285f98c15707b02";
	
	// 카카오 서버에 결제 준비 보내기
	public Map<String, Object> paymentReady(KakaoPayRequestDTO param) {
		PaymentDTO payment = new PaymentDTO();

	    payment.setReservation_id(param.getPartnerOrderId());
		String status = kakaoDAO.findReservationId(payment);
		
		if (status == null) {
		    // 결제 이력이 없음 → 정상 진행 가능

		    Long id = savePaymentInfo(param);
		    String approvalUrl = "http://localhost:8102/kakaopay/success?id=" + id;
		    
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
		    body.add("approval_url", approvalUrl);
		    body.add("cancel_url", "http://localhost:5173/payment/cancel");
		    body.add("fail_url", "http://localhost:5173/payment/fail");

		    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
		    
		    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
		            "https://kapi.kakao.com/v1/payment/ready",
		            HttpMethod.POST,
		            request,
		            new ParameterizedTypeReference<Map<String, Object>>() {}
		        );

		    Map<String, Object> result = response.getBody();
		    
		    String tid = (String) response.getBody().get("tid");

		    kakaoDAO.updateTid(id, tid);
		 
		    
		    return result;
		} else if ("paid".equals(status)) {
		    throw new IllegalStateException("이미 결제 완료된 예약입니다.");
		} else if ("canceled".equals(status)) {
		    throw new IllegalStateException("이미 결제가 취소된 예약입니다.");
		}else {
	        // 예상 못한 상태
	        throw new IllegalStateException("처리할 수 없는 결제 상태입니다: " + status);
	    }
	}

	
	// 카카오 서버에 완료 보내기
	public boolean approve(String pg_token, Long id) {
		
		PaymentDTO paymentList = kakaoDAO.findById(id);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "KakaoAK " + ADMIN_KEY);
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

	    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	    body.add("cid", "TC0ONETIME");
	    body.add("partner_order_id", paymentList.getReservation_id());
	    body.add("partner_user_id", paymentList.getUser_no());
	    body.add("pg_token", pg_token);
	    body.add("tid", paymentList.getTid());

	    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
	    
	    ResponseEntity<Map> response = restTemplate.postForEntity(
	            "https://kapi.kakao.com/v1/payment/approve",
	            request,
	            Map.class
	    );
	    
	    Map<String, Object> result = response.getBody();
	    
	    // 승인처리 확인
	    if (result == null || result.get("approved_at") == null) {
	        return false;
	    }

	    // DB 업데이트
	    PaymentDTO updated = new PaymentDTO();
	    updated.setTid(paymentList.getTid());
	    updated.setStatus("paid");
	    updated.setPaid_at((String) result.get("approved_at"));
	    
	    return kakaoDAO.updatePayment(updated) > 0;
	}
	
	
	// 결제 DB에 올리기
	public Long savePaymentInfo(KakaoPayRequestDTO param) {
		PaymentDTO payment = new PaymentDTO();

	    payment.setReservation_id(param.getPartnerOrderId());
	    payment.setUser_no(param.getPartnerUserId());
	    payment.setAmount(param.getTotalAmount());
	    payment.setMethod("KAKAO");  // 또는 dto에서 받을 수 있음

	    kakaoDAO.paymentReady(payment);
	    
	    return payment.getId();
	}
		
	// 결제 승인 처리
	public int saveApproval(PaymentDTO payment) {
		return kakaoDAO.paymentApprove(payment);
	}


	
	

}
