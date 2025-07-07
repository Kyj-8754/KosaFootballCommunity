package com.msa.kyj_prj.kakaopay;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
		// 객체 준비
		PaymentDTO payment = new PaymentDTO();
		
		// 결제 전 DB에 결제정보 올림
	    payment.setReservation_id(param.getPartnerOrderId());
	    
	    // 결제 상태 가져옴 (해당 정보가 결제완료, 혹은 실패인지 확인하기위한 로직)
		String status = kakaoDAO.findReservationId(payment);
		
		// 결제 검증 후 시작 로직
		if (status == null) {
		    // 결제 이력이 없음 → 정상 진행 가능

			// 결제 정보의 고유 ID 가져옴
		    Long id = savePaymentInfo(param);
		    
		    // 성공, 실패, 취소 url
		    String approvalUrl = "http://localhost:8102/kakaopay/success?id=" + id;
		    String cancelUrl = "http://localhost:8102/kakaopay/cancel?id=" + id;
		    String failUrl = "http://localhost:8102/kakaopay/fail?id=" + id;
		    
		    // api 요청 보내기 위한 로직
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
		    body.add("cancel_url", cancelUrl);
		    body.add("fail_url", failUrl);

		    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
		    
		    // 결제 응답
		    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
		            "https://kapi.kakao.com/v1/payment/ready",
		            HttpMethod.POST,
		            request,
		            new ParameterizedTypeReference<Map<String, Object>>() {}
		        );

		    // 응답온 body 담아주기
		    Map<String, Object> result = response.getBody();
		    
		    // 카카오 API 에서 받아온 TID를 DB에 담아주기 위해서 사용
		    String tid = (String) response.getBody().get("tid");
		    // 결제 DB에 TID 저장
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
		
		// 결제 정보 받아옴
		PaymentDTO paymentList = kakaoDAO.findById(id);
		
		// 결제 승인 요청 보내는 API
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
	    
	    
	    // 승인 응답
	    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
	            "https://kapi.kakao.com/v1/payment/approve",
	            HttpMethod.POST,
	            request,
	            
	            new ParameterizedTypeReference<Map<String, Object>>() {}
	    );
	    
	    
	    // 승인 응답에서 body만 불러옴
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
	    
	    int update_result = kakaoDAO.updatePayment(updated);
	    
	    // 결제 DB 실패시 오류 보냄
	    if (update_result <= 0) {
	        throw new IllegalStateException("결제 승인 실패");
	    }
	    
	    // 결제 성공시 true보냄
	    return true;
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
		


	//결제 실패 처리
	public boolean fail(Long id) {
		// DB 업데이트
	    PaymentDTO updated = new PaymentDTO();
	    updated.setStatus("failed");
	    
	    // DB 업데이트
    	int update_result = kakaoDAO.updatePayment(updated);
	    
	    // 결제 DB 실패시 오류 보냄
	    if (update_result <= 0) {
	        throw new IllegalStateException("결제 승인 실패");
	    }
	    
	    // 결제 성공시 true보냄
	    return true;
	}

	// 결제 취소 처리
	public boolean cancel(Long id) {
		// DB 업데이트
    PaymentDTO updated = new PaymentDTO();
    updated.setStatus("canceled");
    
    // DB 업데이트
	int update_result = kakaoDAO.updatePayment(updated);
    
    // 결제 DB 실패시 오류 보냄
    if (update_result <= 0) {
        throw new IllegalStateException("결제 승인 실패");
    }
    
    // 결제 성공시 true보냄
    return true;
		
	}

	// 결제 환불 처리
	public boolean refund(Map<String, Object> param) {
		
		// 필수 필드 검증
	    if (!param.containsKey("reservation_id") || !param.containsKey("tid")) {
	        throw new IllegalArgumentException("잘못된 예약 정보입니다.");
	    }

	    // 매개변수 담아주기
	    PaymentDTO dto = new PaymentDTO();
	    
	    dto.setReservation_id(param.get("reservation_id").toString());
	    dto.setTid(param.get("tid").toString());
	    
		// DB 에서 현재 결제 정보 상태 받아옴
		String status = kakaoDAO.findReservationId(dto);

		
	    // 유효성 검증
	    if (status == null) throw new IllegalArgumentException("결제 정보가 없습니다.");
	    if (!"paid".equals(status)) throw new IllegalArgumentException("이미 취소된 결제입니다.");

	    // 카카오 API에 환불요청 보내기
	    String tid = param.get("tid").toString();
	    int cancelAmount = Integer.parseInt(param.get("amount").toString());

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "KakaoAK " + ADMIN_KEY);
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

	    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
	    body.add("cid", "TC0ONETIME");
	    body.add("tid", tid);
	    body.add("cancel_amount", String.valueOf(cancelAmount));
	    body.add("cancel_tax_free_amount", "0");

	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
	    RestTemplate restTemplate = new RestTemplate();

	    ResponseEntity<String> response = restTemplate.postForEntity(
	        "https://kapi.kakao.com/v1/payment/cancel",
	        request,
	        String.class
	    );

	    if (response.getStatusCode() == HttpStatus.OK) {
	        // 상태 DB 업데이트
	    	// DB 업데이트
		    PaymentDTO updated = new PaymentDTO();
		    updated.setTid(tid);
		    updated.setStatus("canceled");
		    int update_result = kakaoDAO.updatePayment(updated);
		    
		    // 결제 DB 실패시 오류 보냄
		    if (update_result <= 0) {
		        throw new IllegalStateException("결제 승인 실패");
		    }
		    
		    // 결제 성공시 true보냄
		    return true;
		    
		    
	    } else {
	        throw new IllegalStateException("카카오 환불 실패");
	    }
	}
}
