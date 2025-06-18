package com.msa.kyj_prj;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChild(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorizeHttpRequests ->
		                                               // /** -> 루트 경로 이하 모두 허가함 
			      authorizeHttpRequests.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
		.csrf().disable();
		return http.build();
	}
	
	//UserService.passwordEncoder = PasswordEncoder passwordEncoder()이 설정됨   
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
//	@Bean
//	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
//		return authenticationConfiguration.getAuthenticationManager();
//	}
}
