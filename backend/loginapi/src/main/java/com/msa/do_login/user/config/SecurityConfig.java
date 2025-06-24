package com.msa.do_login.user.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.do_login.user.filter.LoginFilter;
import com.msa.do_login.user.filter.RefreshTokenFilter;
import com.msa.do_login.user.filter.TokenCheckFilter;
import com.msa.do_login.user.service.UserVODetailsService;
import com.msa.do_login.user.util.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final ObjectMapper objectMapper;
	private final JWTUtil jwtUtil;
	private final UserVODetailsService userVODetailsService;

	// 비밀번호 암호화를 위한 BCrypt 해시 함수 객체 생성
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 로그인 없이 정적 리소스에 대한 접근을 가능하게 하는 메소드
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {

		log.info("------------web configure-------------------");

		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Bean
	public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {

		log.info("------------configure-------------------");

		// 인증관리자 빌더 객체 얻기
		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);

		// 인증관리자에 userDetailsService와 비밀번호 암호화 객체를 설정한다
		authenticationManagerBuilder.userDetailsService(userVODetailsService).passwordEncoder(passwordEncoder());

		// 인증관리자를 생성한다
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

		// http 보안 객체에 인증관리자를 설정한다
		http.authenticationManager(authenticationManager);

		// 해당 소스 작성후 : 브라우저에서 /generateToken URL을 실행한다
		final LoginFilter loginFilter = new LoginFilter("/generateToken", objectMapper, jwtUtil);
		loginFilter.setAuthenticationManager(authenticationManager);

		// UsernamePasswordAuthenticationFilter 필더 객체 실행 전에 동작할 loginFilter를 설정한다
		http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);

		// UsernamePasswordAuthenticationFilter 필더 객체 실행 전에 동작할 TokenCheckFilter 객체를
		// 생성하여 설정한다
		// 해당 소스 작성후 : 브라우저에서 /api/sample/test URL을 실행한다
		http.addFilterBefore(new TokenCheckFilter(jwtUtil, userVODetailsService),
				UsernamePasswordAuthenticationFilter.class);

		// TokenCheckFilter 필더 객체 실행 전에 동작할 RefreshTokenFilter 객체를 생성하여 설정한다
		// 해당 소스 작성후 : 브라우저에서 /refreshToken URL을 실행한다
		http.addFilterBefore(new RefreshTokenFilter("/refreshToken", objectMapper, jwtUtil), TokenCheckFilter.class);

		// csrf 비활성화
		http.csrf(csrf -> csrf.disable());
		// 세션을 사용하지 않음
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(authroize -> authroize
				.requestMatchers("/user/na/**", "/generateToken", "/refreshToken","/kakao/callback").permitAll()
				.requestMatchers("/user/filter/myPage").authenticated()
				.requestMatchers("/user/**").hasAnyAuthority("ROLE_A3", "ROLE_A2", "ROLE_A1") // 여러개의 권한 중 하나라도 있으면 성공
				.requestMatchers("/manager/**").hasAnyAuthority("ROLE_A2", "ROLE_A1")
				.requestMatchers("/admin/**").hasAnyAuthority("ROLE_A1") // 반드시 해당 권한만 허가
				.anyRequest().permitAll() // /home url은 비회원이 사용할 수 있음
		);

		return http.build();

	}
}
