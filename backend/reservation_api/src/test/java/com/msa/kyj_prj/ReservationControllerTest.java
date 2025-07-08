package com.msa.kyj_prj;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.kyj_prj.reservation.ReservationService;
import com.msa.kyj_prj.reservation.ResrvationController;


@WebMvcTest(ResrvationController.class)
public class ReservationControllerTest {
	 	@Autowired
	    private MockMvc mockMvc;
	
	    @MockBean
	    private ReservationService reservationService;
	
	    @Autowired
	    private ObjectMapper objectMapper;
	
	    @Test
	    void 예약취소_실패_상태불가() throws Exception {
	        // given
	        Map<String, Object> param = Map.of("reservation_id", "R123");
	        doThrow(new IllegalStateException("이미 취소된 예약입니다."))
	            .when(reservationService).cancelReservation(any());

	        // when & then
	        mockMvc.perform(post("/reservation/cancel")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(param)))
	            .andExpect(status().isBadRequest())
	            .andExpect(jsonPath("$.error").value(Boolean.TRUE))
	            .andExpect(jsonPath("$.message").value("이미 취소된 예약입니다."));
	    }
}
