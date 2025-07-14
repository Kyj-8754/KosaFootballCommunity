package com.msa.do_login.user.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.UserVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO extends User {
	private int userNo;
	private String userId;
	private String userPwd;
	private String userName;
	private String authCode;

	private UserDTO(String userId, String userPwd, Collection<GrantedAuthority> authorities) {
		super(userId, userPwd, authorities);
		this.userId = userId;
		this.userPwd = userPwd;
	}

	public static UserDTO of(UserVO userVO, LocalAccount account) {
		UserDTO userDTO = new UserDTO(account.getUserId(), account.getUserPwd(), userVO.getAuthorities());
		
		userDTO.setUserName(userVO.getUserName());
		userDTO.setUserNo(userVO.getUserNo());
		userDTO.setAuthCode(userVO.getAuthCode());
		
		return userDTO; 
	}
}
