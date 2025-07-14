package com.msa.kyj_prj.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserDTO {
	private int userNo;				// 유저 pk
	private String userName;		// 유저 이름
	private String userPhone;		// 유저 전화 번호
	private String userExitStatus;	// 유저 탈퇴 정보
	private String authCode;		// 유저 권한
}
