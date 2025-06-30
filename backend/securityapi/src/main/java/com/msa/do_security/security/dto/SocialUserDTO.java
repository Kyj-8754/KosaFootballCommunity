package com.msa.do_security.security.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.msa.do_security.security.vo.SocialAccountVO;
import com.msa.do_security.security.vo.UserVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialUserDTO extends User{
	private int userNo;
	private String email;
	private String userName;
	private String authCode;
	
	public SocialUserDTO(String email, Collection<GrantedAuthority> authorities) {
		super(email, "", authorities);
		this.email = email;
	}

	public static SocialUserDTO of(UserVO userVO, SocialAccountVO account) {
		SocialUserDTO socialUserDTO = new SocialUserDTO(account.getEmail(), userVO.getAuthorities());
		
		socialUserDTO.setUserName(userVO.getUserName());
		socialUserDTO.setUserNo(userVO.getUserNo());
		socialUserDTO.setAuthCode(userVO.getAuthCode());
		
		return socialUserDTO; 
	}
}
