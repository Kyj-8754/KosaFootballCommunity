package com.msa.do_login.user.dto;

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
	private String userId;
	private String userPwd;
	private String userName;
	private String authCode;
	
	private SocialUserDTO(String userId, String userPwd, Collection<GrantedAuthority> authorities) {
		super(userId, userPwd, authorities);
		this.userId = userId;
		this.userPwd = userPwd;
	}

	public static SocialUserDTO of(UserVO userVO, SocialAccountVO account) {
		SocialUserDTO dto = new SocialUserDTO(
			account.getEmail(),
			"SOCIAL_LOGIN",
			userVO.getAuthorities()
		);

		dto.setUserNo(userVO.getUserNo());
		dto.setUserName(userVO.getUserName());
		dto.setAuthCode(userVO.getAuthCode());

		return dto;
	}
}
