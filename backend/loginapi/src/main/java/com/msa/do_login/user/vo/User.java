package com.msa.do_login.user.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
	private int userNo;
	private String userName;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date userBirth;
	private String userPhone;
	private String userPostCode;
	private String userAddr;
	private String userDetailAddr;
	private String oriPicName;
	private String newPicName;
	private Date recentLogin;
	private Date userRegDate;
	private String userExitStatus;
	private Date userExitDate;
	private String userFailStatus;
	private int userFailCnt;
	private int authCode;
	private String userCode;
	private String userGender;
	private String userComment;
	private int styleCode;
	private int statCode;
}
