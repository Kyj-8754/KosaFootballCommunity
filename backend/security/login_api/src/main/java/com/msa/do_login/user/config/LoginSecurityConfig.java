package com.msa.do_login.user.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizedUrl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.do_security.security.config.BaseSecurityConfig;
import com.msa.do_security.security.service.OAuth2UserVOService;
import com.msa.do_security.security.service.UserVODetailsService;
import com.msa.do_security.security.util.JWTUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class LoginSecurityConfig extends BaseSecurityConfig {
	
	public LoginSecurityConfig(ObjectMapper objectMapper, JWTUtil jwtUtil, UserVODetailsService userVODetailsService, OAuth2UserVOService oAuth2UserService) {
		super(objectMapper, jwtUtil, userVODetailsService, oAuth2UserService);
	}
	
	@Override
	protected void customizeAuthorization(AuthorizationManagerRequestMatcherRegistry authorize) throws Exception {
		((AuthorizedUrl) authorize.requestMatchers("/user/na/**")).permitAll();
		((AuthorizedUrl) authorize.requestMatchers("/oauth/**")).permitAll();
		((AuthorizedUrl) authorize.requestMatchers("/v3/api-docs/**")).permitAll();
		((AuthorizedUrl) authorize.requestMatchers("/swagger-ui/**")).permitAll();
		((AuthorizedUrl) authorize.requestMatchers("/swagger-ui.html")).permitAll();
		((AuthorizedUrl) authorize.requestMatchers("/webjars/**")).permitAll();
		((AuthorizedUrl) authorize.requestMatchers("/mypage/**")).authenticated();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(List.of("http://www.itsfootball.store")); // Vue 개발 서버
	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    config.setAllowedHeaders(List.of("*"));
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return source;
	}
	
	
}