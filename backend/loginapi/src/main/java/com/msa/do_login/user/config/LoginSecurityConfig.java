package com.msa.do_login.user.config;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizedUrl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.do_security.security.config.BaseSecurityConfig;
import com.msa.do_security.security.service.UserVODetailsService;
import com.msa.do_security.security.util.JWTUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class LoginSecurityConfig extends BaseSecurityConfig {
	
	public LoginSecurityConfig(ObjectMapper objectMapper, JWTUtil jwtUtil, UserVODetailsService userVODetailsService) {
		super(objectMapper, jwtUtil, userVODetailsService);
	}

	@Override
	protected void customizeAuthorization(AuthorizationManagerRequestMatcherRegistry authorize) throws Exception {
		((AuthorizedUrl) authorize.requestMatchers("/user/na/**")).permitAll();
		((AuthorizedUrl) authorize.requestMatchers("/kakao/callback")).permitAll();
	}

	@Override
	protected List<String> getExcludedPaths() {
		return Stream.concat(
			super.getExcludedPaths().stream(),
			Stream.of("/user/na/**", "/kakao/callback")
		).toList();
	}
}
