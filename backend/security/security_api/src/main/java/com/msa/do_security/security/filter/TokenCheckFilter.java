package com.msa.do_security.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.msa.do_security.security.exception.AccessTokenException;
import com.msa.do_security.security.service.OAuth2UserVOService;
import com.msa.do_security.security.service.UserVODetailsService;
import com.msa.do_security.security.util.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {
	private final JWTUtil jwtUtil;
	private final UserVODetailsService userVODetailsService;
	private final OAuth2UserVOService oAtuAuth2UserVOService;
	private final List<String> excludedPaths;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 요청 URL을 얻는다
		final String path = request.getRequestURI();

		for (String pattern : excludedPaths) {
			log.info("패턴 검사 중: pattern = {}, path = {}", pattern, path);
			if (pathMatcher.match(pattern, path)) {
				log.info("요청 경로 {} 는 제외 대상 패턴 {} 과 매칭됨. 필터 통과.", path, pattern);
				filterChain.doFilter(request, response);
				return;
			}
		}

		log.info("이부분에서 JWT 토큰이 존재하고 유효한지 확인한다");
		log.info("jwtUtil = {}", jwtUtil);

		try {
			// JWT 검증 및 인증 처리를 한다
			setAuthentication(request);

			// 요청한 부분으로 이동한다
			filterChain.doFilter(request, response);
		} catch (AccessTokenException accessTokenException) {
			// 응답으로 토큰 예외 발생시 오류를 전달한다
			accessTokenException.sendResponseError(response);
		}
	}

	private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {

		// Header에 전달된 Authorization의 값을 얻는다
		String headerStr = request.getHeader("Authorization");
		log.info("요청 Authorization 헤더: {}", headerStr);
		log.info("요청 Method = {}, URI = {}, Authorization 헤더 = {}", request.getMethod(), request.getRequestURI(), request.getHeader("Authorization"));
		// Authorization의 값이 존재하지 않으면 오류 발생한다
		if (headerStr == null || headerStr.length() < 8) {
			throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
		}

		// Bearer 생략
		String tokenType = headerStr.substring(0, 6);
		String tokenStr = headerStr.substring(7);

		if (tokenType.equalsIgnoreCase("Bearer") == false) {
			throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
		}

		try {
			Map<String, Object> values = jwtUtil.validateToken(tokenStr);
			log.info("토큰 payload 값: {}", values);
			return values;
		} catch (MalformedJwtException malformedJwtException) {
			log.error("MalformedJwtException----------------------");
			throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
		} catch (SignatureException signatureException) {
			log.error("SignatureException----------------------");
			throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
		} catch (ExpiredJwtException expiredJwtException) {
			log.error("ExpiredJwtException----------------------");
			log.error("JWT 만료됨: {}", expiredJwtException.getMessage());
			throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
		}
	}

	private void setAuthentication(HttpServletRequest request) throws AccessTokenException {
		Map<String, Object> payload = validateAccessToken(request);
		// uid
		String userId = (String) payload.get("userId");
		log.info("userId: " + userId);
		String loginType = (String) payload.get("loginType");
		log.info("loginType: " + loginType);
		// uid에 대한 시큐리티 로그인 객체를 얻는다
		UserDetails userDetails;
		if ("LOCAL".equalsIgnoreCase(loginType)) {
	        userDetails = userVODetailsService.loadUserByUsername(userId);
	    } else {
	    	userDetails = oAtuAuth2UserVOService.loadUserByUsername(userId, loginType);
	    }
		System.out.println(userDetails);
		// userDetails 객체를 사용하여 인증객체로 생성한다
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetails.getUsername(), null, userDetails.getAuthorities());
		System.out.println(authentication);

		// 스프링 시큐리티에 인증객체를 설정한다
		SecurityContextHolder.getContext().setAuthentication(authentication);

	}
}
