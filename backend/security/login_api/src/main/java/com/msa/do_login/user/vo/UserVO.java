package com.msa.do_login.user.vo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "회원 정보 VO")
public class UserVO {
	
	@Schema(description = "회원 번호", example = "1")
	private int userNo;
	
	@Schema(description = "회원 이름", example = "홍길동")
	private String userName;
	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	@Schema(description = "생년월일", example = "1990-01-01")
	private Date userBirth;
	
	@Schema(description = "전화번호", example = "01012345678")
	private String userPhone;
	
	@Schema(description = "우편번호", example = "12345")
	private String userPostCode;
	@Schema(description = "기본 주소", example = "서울특별시 강남구")
	private String userAddr;

	@Schema(description = "상세 주소", example = "아파트 101동 202호")
	private String userDetailAddr;

	@Schema(description = "원본 프로필 이미지 파일명", example = "profile.jpg")
	private String oriPicName;

	@Schema(description = "저장된 프로필 이미지 파일명", example = "uuid_profile.jpg")
	private String newPicName;

	@Schema(description = "최근 로그인 시각", example = "2025-07-15T09:00:00")
	private Date recentLogin;

	@Schema(description = "회원 가입일", example = "2025-01-01T10:00:00")
	private Date userRegDate;

	@Schema(description = "회원 탈퇴 여부 (Y: 탈퇴, N: 정상)", example = "N")
	private String userExitStatus;

	@Schema(description = "탈퇴일", example = "2025-06-30T12:00:00")
	private Date userExitDate;

	@Schema(description = "로그인 실패 상태 (Y: 잠금, N: 정상)", example = "N")
	private String userFailStatus;

	@Schema(description = "로그인 실패 횟수", example = "0")
	private int userFailCnt;

	@Schema(description = "권한 코드 (콤마로 구분, 예: A1,A2,A3)", example = "A1")
	private String authCode;

	@Schema(description = "사용자 코드 (랜덤 생성된 유저 고유코드)", example = "6HrkO")
	private String userCode;

	@Schema(description = "성별 (M: 남성, F: 여성)", example = "F")
	private String userGender;

	@Schema(description = "사용자 소개글", example = "안녕하세요. 반갑습니다.")
	private String userComment;

	@Schema(description = "스타일 코드", example = "1")
	private Integer styleCode;

	@Schema(description = "성향 코드", example = "1")
	private Integer statCode;
	
	@JsonIgnore
	public Collection<GrantedAuthority> getAuthorities() {
		if (authCode != null && authCode.length() > 0) {
			return Arrays.stream(authCode.split(",")).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toUnmodifiableList());
		}
		return Collections.EMPTY_LIST;
	}
	
}