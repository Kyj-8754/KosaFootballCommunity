package com.msa.do_login.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.do_security.security.config.BaseSecurityConfig;
import com.msa.do_security.security.service.UserVODetailsService;
import com.msa.do_security.security.util.JWTUtil;

@Configuration
public class LoginSecurityConfig extends BaseSecurityConfig{
	public LoginSecurityConfig(ObjectMapper objectMapper, JWTUtil jwtUtil, UserVODetailsService userVODetailsService) {
        super(objectMapper, jwtUtil, userVODetailsService);
    }
	@Override
	protected void customizeAuthorization(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
			.requestMatchers("/user/na/**", "/kakao/callback").permitAll()
		);
	}
}
