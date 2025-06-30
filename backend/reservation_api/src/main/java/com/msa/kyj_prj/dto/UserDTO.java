package com.msa.kyj_prj.dto;

import lombok.Data;

@Data
public class UserDTO {
	private int user_no;				// 유저 pk
	private String user_name;			// 유저 이름
	private String user_phone;			// 유저 전화 번호
	private String user_exit_status;	// 유저 탈퇴 정보
	private String auth_code;			// 유저 권한
}
