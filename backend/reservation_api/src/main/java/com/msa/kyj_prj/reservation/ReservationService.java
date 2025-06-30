package com.msa.kyj_prj.reservation;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.msa.kyj_prj.dto.UserDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService{
	private final ReservationDAO reservationDAO;
	private final WebClient.Builder webClientBuilder; 
	
//	public UserDTO getUser(int userNo) {
//        return webClientBuilder.build()
//                .get()
//                .uri("http://localhost:8111/users/" + userNo)
//                .retrieve()
//                .bodyToMono(UserDTO.class)
//                .block();
//    }
	// 예약 등록 폼
	public Reservation getReservationForm(String SVCID, String date, String userNo) {
		// → user-api에 사용자 정보 요청
		UserDTO user = webClientBuilder.build()
			.get()
		    .uri("http://localhost:8111/users/" + userNo)
		    .retrieve()
		    .bodyToMono(UserDTO.class)
		    .block();
				
				
		return reservationDAO.getReservationForm(SVCID,date,userNo);
	}
	
}


