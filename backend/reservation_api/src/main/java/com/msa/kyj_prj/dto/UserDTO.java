package com.msa.kyj_prj.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserDTO {
	@Schema(description = "유저 PK", example = "SVC123")
	private int userNo;				// 유저 pk
	@Schema(description = "유저 이름", example = "SVC123")
	private String userName;		// 유저 이름
	@Schema(description = "유저 전화 번호", example = "SVC123")
	private String userPhone;		// 유저 전화 번호
	@Schema(description = "유저 탈퇴 정보", example = "SVC123")
	private String userExitStatus;	// 유저 탈퇴 정보
	@Schema(description = "유저 권한", example = "SVC123")
	private String authCode;		// 유저 권한
}
