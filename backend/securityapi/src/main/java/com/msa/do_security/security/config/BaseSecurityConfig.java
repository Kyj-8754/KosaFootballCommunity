package com.msa.do_security.security.config;

import java.util.List;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.do_security.security.filter.LoginFilter;
import com.msa.do_security.security.filter.RefreshTokenFilter;
import com.msa.do_security.security.filter.TokenCheckFilter;
import com.msa.do_security.security.service.OAuth2UserVOService;
import com.msa.do_security.security.service.UserVODetailsService;
import com.msa.do_security.security.util.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class BaseSecurityConfig {

	private final ObjectMapper objectMapper;
	private final JWTUtil jwtUtil;
	private final UserVODetailsService userVODetailsService;
	private final OAuth2UserVOService oAuth2UserService;

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
		http.addFilterBefore(new TokenCheckFilter(jwtUtil, userVODetailsService, getExcludedPaths()),
				UsernamePasswordAuthenticationFilter.class);

		// TokenCheckFilter 필더 객체 실행 전에 동작할 RefreshTokenFilter 객체를 생성하여 설정한다
		// 해당 소스 작성후 : 브라우저에서 /refreshToken URL을 실행한다
		http.addFilterBefore(new RefreshTokenFilter("/refreshToken", objectMapper, jwtUtil), TokenCheckFilter.class);
		
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		// csrf 비활성화
		http.csrf(csrf -> csrf.disable());
		http.formLogin(form -> form.disable());
		// 세션을 사용하지 않음
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		defaultAuthorizationRules(http);
		
		return http.build();

	}

	private void defaultAuthorizationRules(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authroize -> {
			authroize.requestMatchers("/generateToken").permitAll();
			authroize.requestMatchers("/refreshToken").permitAll();
			authroize.requestMatchers("/login").permitAll();
			authroize.requestMatchers("/login?error").permitAll();
			authroize.requestMatchers("/.well-known/**").permitAll();
			try {
				customizeAuthorization(authroize);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			authroize.requestMatchers("/manager/**").hasAnyAuthority("ROLE_A2", "ROLE_A1");
			authroize.requestMatchers("/admin/**").hasAnyAuthority("ROLE_A1");

			authroize.anyRequest().permitAll();
		}); // 반드시 해당 권한만 허가)
	}

	protected void customizeAuthorization(
			AuthorizationManagerRequestMatcherRegistry authorizeHttpRequestsCustomizer) throws Exception {
	}

	protected List<String> getExcludedPaths() {
		return List.of("/generateToken",
				"/refreshToken",
				"/oauth/**",
				"/login",
				"/login?error",
				"/oauth/callback/**",
				"/.well-known/**",
				"/user/na/**");
	}
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(List.of("http://localhost:5173")); // Vue 개발 서버
	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    config.setAllowedHeaders(List.of("*"));
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return source;
	}
}
