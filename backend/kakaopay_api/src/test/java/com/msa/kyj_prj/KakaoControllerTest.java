package com.msa.kyj_prj;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.kyj_prj.dto.KakaoPayRequestDTO;
import com.msa.kyj_prj.kakaopay.KakaoController;
import com.msa.kyj_prj.kakaopay.KakaoService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;


//KakaoController 테스트용
@WebMvcTest(KakaoController.class)
public class KakaoControllerTest {
	// 가짜 HTTP 요청을 날릴 수 있는 도구
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private KakaoService kakaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 결제_ready_실패_서버오류() throws Exception {
    	 // kakaoService.paymentReady() 호출 시 runtiem err 발생하도록
        given(kakaoService.paymentReady(any()))
                .willThrow(new RuntimeException("서버 다운"));
        
        // 테스트용 빈 DTO 생성
        KakaoPayRequestDTO dto = new KakaoPayRequestDTO();
        
        // ready에 post요청 보내고 결과 검증하기
        mockMvc.perform(post("/kakaopay/ready")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다: 서버 다운"));
    }
    
    @Test
    void 결제_취소_정상_리디렉션() throws Exception {
        // kakaoService.cancel() 호출은 void 메서드이므로 특별한 설정 필요 없음

        mockMvc.perform(get("/kakaopay/cancel")
                .param("id", "123"))
                .andExpect(status().is3xxRedirection()) // 리디렉션 상태코드 확인 (302)
                .andExpect(redirectedUrl("http://localhost:5173/payment/cancel.html")); // 실제 리디렉션 주소 확인
    }
    
    @Test
    void 결제_환불_실패_잘못된_예약() throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("user_no", "U1");
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("reservation_id", "R1");
        reservation.put("tid", "T123");
        param.put("reservation", reservation);

        willThrow(new IllegalArgumentException("해당 예약이 존재하지 않습니다."))
            .given(kakaoService).refund(any(), anyString());

        mockMvc.perform(post("/kakaopay/refund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("해당 예약이 존재하지 않습니다."));
    }
}
