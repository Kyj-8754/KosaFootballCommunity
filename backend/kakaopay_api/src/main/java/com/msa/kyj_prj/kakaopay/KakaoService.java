package com.msa.kyj_prj.kakaopay;

import java.util.Map;

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

import com.msa.kyj_prj.dto.KakaoPayRequestDTO;
import com.msa.kyj_prj.dto.PaymentDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

	private final RestTemplate restTemplate;
	private final KakaoDAO kakaoDAO;
	
	@Value("${kakao.admin.key}")
	private String ADMIN_KEY;
	
 
	
	// 카카오 서버에 결제 준비 보내기
	public Map<String, Object> paymentReady(KakaoPayRequestDTO param) {
		// 권한 '매니저' 확인용
		if(!"ROLE_A2".equals(param.getAuthCode())) {
			throw new IllegalStateException("결제 불가능한 유저입니다.");
		}
		
		// 객체 준비
		PaymentDTO payment = new PaymentDTO();
		
		// 결제 전 DB에 결제 정보 있는지 확인용
	    payment.setReservation_id(param.getPartnerOrderId());
	    // 결제 상태 가져옴 (해당 정보가 결제완료, 혹은 실패인지 확인하기위한 로직)
		String status = kakaoDAO.findReservationId(payment);
		
		
		// 결제 검증 후 시작 로직 결제준비, 실패 혹은 null일 경우 가능
		if ("pending".equals(status) || status == null || "failed".equals(status)) {
		    // 결제 이력이 없음 → 정상 진행 가능
			log.info("결제 검증 완료, 시작");
			
			// 결제 정보를 올리고 PK값 반환
		    Long id = savePaymentInfo(param);
		    log.info("id 가져옴" + id);
		   
		    // 성공, 실패, 취소 url
		    String approvalUrl = "http://www.itsfootball.store/kakaopay_api/kakaopay/success?id=" + id;
		    String cancelUrl = "http://www.itsfootball.store/kakaopay_api/kakaopay/cancel?id=" + id;
		    String failUrl = "http://www.itsfootball.store/kakaopay_api/kakaopay/fail?id=" + id;
		    
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
		    
		    log.info("카카오 응답 바디: {}", result);
		    
		    // 카카오 API 에서 받아온 TID를 DB에 담아주기 위해서 사용
		    String tid = (String) response.getBody().get("tid");
		    // 결제 DB에 TID 저장
		    kakaoDAO.updateTid(id, tid);
		 
		    
		    return result;
		} else if ("paid".equals(status)) {
		    throw new IllegalStateException("이미 결제 완료된 예약입니다.");
		} else if ("refunded".equals(status)) {
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
	    
	    // 승인처리 확인용
	    if (result == null || result.get("approved_at") == null) {
	        return false;
	    }

	    // DB 업데이트
	    paymentList.setStatus("paid");
	    paymentList.setPaid_at((String) result.get("approved_at"));
	    
	    int update_result = kakaoDAO.updatePayment(paymentList);
	    
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
		
		
		// 결제 정보 가져옴
	    PaymentDTO updated = kakaoDAO.findById(id);
	    
	    // 실패 저장
	    updated.setStatus("failed");
	    
	    // DB 업데이트 명령문
    	int update_result = kakaoDAO.updatePayment(updated);
	    
	    // DB 실패시 오류 보냄 
	    if (update_result <= 0) {
	        throw new IllegalStateException("결제 승인 실패");
	    }	    
	    
	    return true;
	}

	// 결제 취소 처리
	public boolean cancel(Long id) {
		// 결제 정보 가져옴
	    PaymentDTO updated = kakaoDAO.findById(id);
	    updated.setStatus("cancelled");
	    
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
	public boolean refund(PaymentDTO payment, String user_no) {
		
		// 필수 필드 검증
	    if (payment == null) {
	        throw new IllegalArgumentException("잘못된 예약 정보입니다.");
	    }
	    
	    if (!user_no.equals(payment.getUser_no())) {
	    	throw new IllegalArgumentException("잘못된 회원입니다.");
	    }

	    log.info("환불 처리 로직 시작");
	    
		// DB 에서 현재 결제 정보 상태 받아옴
		String status = kakaoDAO.findReservationId(payment);
		
		log.info("결제상태"+status);
		
	    // 유효성 검증
	    if (status == null) throw new IllegalArgumentException("결제 정보가 없습니다.");
	    if ("refunded".equals(status)) throw new IllegalArgumentException("이미 취소된 결제입니다.");

	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "KakaoAK " + ADMIN_KEY);
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

	    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
	    body.add("cid", "TC0ONETIME");
	    body.add("tid", payment.getTid());
	    body.add("cancel_amount",  String.valueOf(payment.getAmount()));
	    body.add("cancel_tax_free_amount", "0");

	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
	    RestTemplate restTemplate = new RestTemplate();
	    log.info("reseponse 로 넘기기");
	    
	    // 승인 응답
	    ResponseEntity<String> response = restTemplate.postForEntity(
		        "https://kapi.kakao.com/v1/payment/cancel",
		        request,
		        String.class
		    );
	    log.info("완료 코드" + response.getStatusCode());
	    
	    if (response.getStatusCode() == HttpStatus.OK) {
	        // 상태 DB 업데이트
	    	// DB 업데이트
		   
	    	payment.setStatus("refunded");
		    int update_result = kakaoDAO.updatePayment(payment);
		    
		    // 결제 DB 실패시 오류 보냄
		    if (update_result <= 0) {
		        throw new IllegalStateException("결제환불 완료, 그러나 문제발생");
		    }
		    
		    // 결제 성공시 true보냄
		    return true;
		    
		    
	    } else {
	        throw new IllegalStateException("카카오 환불 실패");
	    }
	}
}
