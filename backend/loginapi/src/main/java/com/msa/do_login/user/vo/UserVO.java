package com.msa.do_login.user.vo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO {
	private int userNo;
	private String userName;
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
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
	private String authCode;
	private String userCode;
	private String userGender;
	private String userComment;
	private Integer styleCode;
	private Integer statCode;
	
	@JsonIgnore
	public Collection<GrantedAuthority> getAuthorities() {
		if (authCode != null && authCode.length() > 0) {
			return Arrays.stream(authCode.split(",")).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toUnmodifiableList());
		}
		return Collections.EMPTY_LIST;
	}
	
}