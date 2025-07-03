package com.msa.kyj_prj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizedUrl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.do_security.security.config.BaseSecurityConfig;
import com.msa.do_security.security.service.OAuth2UserVOService;
import com.msa.do_security.security.service.UserVODetailsService;
import com.msa.do_security.security.util.JWTUtil;

@Configuration
public class BoardSecurityConfig extends BaseSecurityConfig{
	
	public BoardSecurityConfig(ObjectMapper objectMapper, JWTUtil jwtUtil, UserVODetailsService userVODetailsService, OAuth2UserVOService oAuth2UserService) {
		super(objectMapper, jwtUtil, userVODetailsService, oAuth2UserService);
	}
	
	@Override
	protected void customizeAuthorization(AuthorizationManagerRequestMatcherRegistry authorize) throws Exception {
		((AuthorizedUrl) authorize.requestMatchers("/board/**")).permitAll();
	}
	
}
