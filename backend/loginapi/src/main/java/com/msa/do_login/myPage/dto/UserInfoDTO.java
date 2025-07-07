package com.msa.do_login.myPage.dto;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.UserVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDTO extends User {
	private int userNo;
	private String userId;
	private String userPwd;
	private String userName;
	private String authCode;
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
	private String userCode;
	private String userGender;
	private String userComment;
	private String styleName;
	private String statName;

	private UserInfoDTO(String userId, String userPwd, Collection<GrantedAuthority> authorities) {
		super(userId, userPwd, authorities);
		this.userId = userId;
		this.userPwd = userPwd;
	}

	public static UserInfoDTO of(UserVO userVO, LocalAccount account) {
		UserInfoDTO userDTO = new UserInfoDTO(account.getUserId(), account.getUserPwd(), userVO.getAuthorities());
		
		userDTO.setUserName(userVO.getUserName());
		userDTO.setUserNo(userVO.getUserNo());
		userDTO.setAuthCode(userVO.getAuthCode());
		
		return userDTO; 
	}
}
