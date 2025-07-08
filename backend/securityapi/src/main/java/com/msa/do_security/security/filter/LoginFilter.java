package com.msa.do_security.security.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.do_security.security.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFilter extends AbstractAuthenticationProcessingFilter {
	private static final String CONTENT_TYPE = "application/json";// json 타입의 데이터로만 로그인을 진행한다.
	private final ObjectMapper objectMapper;
	private final JWTUtil jwtUtil;

	public LoginFilter(String defaultFilterPrrocessingUrl, ObjectMapper objectMapper, JWTUtil jwtUtil) {
		super(defaultFilterPrrocessingUrl);
		this.objectMapper = objectMapper;
		this.jwtUtil = jwtUtil;

		// 로그인 성공시 처리 핸들러 등록
		this.setAuthenticationSuccessHandler((request, response, authentication) -> {
			// authentication : 인증된 로그인 정보
			log.info("로그인 성공시 처리 핸들러 .............");
			log.info("인증된 로그인 정보 : {}", authentication);
			log.info("인증된 로그인 아이디 : {}", authentication.getName());
			com.msa.do_security.security.dto.UserDTO userDTO = (com.msa.do_security.security.dto.UserDTO) authentication
					.getPrincipal();
			// JWT에 추가할 정보로 아이디가 있는 Map 객체를 생성한다
						final Map<String, Object> claim = Map.of(
								"userId", userDTO.getUserId(), 
								"userNo", userDTO.getUserNo(),
								"userName", URLEncoder.encode(userDTO.getUserName(), StandardCharsets.UTF_8), 
								"authCode", "ROLE_"+userDTO.getAuthCode(),
								"loginType", "local");

						Map<String, String> keyMap = Map.of(
								"accessToken", jwtUtil.generateToken(claim, 30), // Access Token 유효기간 30분으로 생성 
								"refreshToken", jwtUtil.generateToken(claim, 60) // Refresh Token 유효기간 60분으로 생성
								);
			// json 객체로 응답 스트림에 keyMap 객체를 출력 한다
			objectMapper.writeValue(response.getWriter(), keyMap);
		});
		
		// 로그인 실패시 처리 핸들러 등록
		this.setAuthenticationFailureHandler((request, response, exception) -> {
			log.info("로그인 실패시 처리 핸들러 등록 .............");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
			response.setContentType("application/json; charset=UTF-8");
			objectMapper.writeValue(response.getWriter(),
					Map.of("res_code", "401", "res_msg", "아이디 또는 비밀번호가 잘못되었습니다."));
		});
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		// TODO Auto-generated method stub
		log.info("LoginFilter ----------------------------- ");

		log.info("ContentType = {}", request.getContentType());
		if (request.getContentType() == null || !request.getContentType().startsWith(CONTENT_TYPE)) {
			log.info("Authentication Content-Type not supported: " + request.getContentType());
			return null;
		}

		// json으로 요청한 문자열을 얻는다
		String jsonText = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

		// JSON 문자열을 Map 객체로 변환 한다
		Map<String, String> jsonData = objectMapper.readValue(jsonText, Map.class);
		log.info("jsonData = {}", jsonData);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				jsonData.get("userId"), jsonData.get("userPwd"));
		return getAuthenticationManager().authenticate(authenticationToken);
	}
}
