package com.msa.do_login.user.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "소셜 회원 가입 요청 DTO")
public class SocialUserRegisterDTO {
	@Schema(description = "소셜 로그인 제공자 (kakao, google, naver)", example = "kakao")
	private String provider;
	
	@Schema(description = "소셜 제공자에서의 사용자 ID", example = "1234567890")
	private String providerId;

	@Schema(description = "사용자 이름", example = "홍길동")
	private String userName;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "생년월일", example = "1990-01-01")
	private Date userBirth;

	@Schema(description = "전화번호", example = "01012345678")
	private String userPhone;
	
	@Schema(description = "우편번호", example = "12345")
	private String userPostcode;
	
	@Schema(description = "기본 주소", example = "서울특별시 강남구")
	private String userAddr;
	
	@Schema(description = "상세 주소", example = "아파트 101동 202호")
	private String userDetailAddr;
	
	@Schema(description = "성별 (M: 남성, F: 여성)", example = "M")
	private String userGender;
}
