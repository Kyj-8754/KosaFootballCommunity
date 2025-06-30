package com.msa.do_login.user.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SocialUserRegisterDTO {
	private String provider;
	private String providerId;

	private String userName;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date userBirth;

	private String userPhone;
	private String userPostcode;
	private String userAddr;
	private String userDetailAddr;
	private String userGender;
}
