package com.msa.do_security.security.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.msa.do_security.security.vo.LocalAccount;
import com.msa.do_security.security.vo.UserVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialUserDTO extends User{
	private int userNo;
	private String userId;
	private String userPwd;
	private String userName;
	private String authCode;
	
	public SocialUserDTO(String userId, String userPwd, Collection<GrantedAuthority> authorities) {
		super(userId, userPwd, authorities);
		this.userId = userId;
		this.userPwd = userPwd;
	}

	public static SocialUserDTO of(UserVO userVO, LocalAccount account) {
		SocialUserDTO socialUserDTO = new SocialUserDTO(account.getUserId(), account.getUserPwd(), userVO.getAuthorities());
		
		socialUserDTO.setUserName(userVO.getUserName());
		socialUserDTO.setUserNo(userVO.getUserNo());
		socialUserDTO.setAuthCode(userVO.getAuthCode());
		
		return socialUserDTO; 
	}
}
