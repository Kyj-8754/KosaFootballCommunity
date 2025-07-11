package com.msa.do_security.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.do_security.security.exception.RefreshTokenException;
import com.msa.do_security.security.util.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {
	private final String refreshPath;
	private final ObjectMapper objectMapper;
	private final JWTUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();

		if (!path.equals(refreshPath)) {
			log.info("skip refresh token filter.....");
			filterChain.doFilter(request, response);
			return;
		}

		log.info("리플레쉬 필터 기능 실행 ..................");
		// filterChain.doFilter(request, response);

		// 전송된 JSON에서 accessToken과 refreshToken을 얻어온다.
		Map<String, String> tokens = parseRequestJSON(request);

		String accessToken = tokens.get("accessToken");
		String refreshToken = tokens.get("refreshToken");

		log.info("accessToken: " + accessToken);
		log.info("refreshToken: " + refreshToken);

		Map<String, Object> refreshClaims = null;

		try {
			// 전달받은 accessToken을 확인 한다
			checkAccessToken(accessToken);

			// 전달받은 refreshToken을 확인 한다
			refreshClaims = checkRefreshToken(refreshToken);
			log.info("refreshClaims = {}", refreshClaims);
		} catch (RefreshTokenException refreshTokenException) {
			refreshTokenException.sendResponseError(response);
			return; // 더이상 실행 필요 없이 응답에 오류 전달하고 종료함
		}

		// Refresh Token의 유효시간이 얼마 남지 않은 경우
		Long exp = (Long) refreshClaims.get("exp");

		Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
		Date current = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		// 만료 시간과 현재 시간의 간격 계산
		// 만일 3일 미만인 경우에는 Refresh Token도 다시 생성
		long gapTime = (expTime.getTime() - current.getTime());

		log.info("-----------------------------------------");
		log.info("current = {}", sdf.format(current));
		log.info("expTime = {}", sdf.format(expTime));
		log.info("gap = {}", gapTime);

		String uid = (String) refreshClaims.get("uid");

		// 이상태까지 오면 무조건 AccessToken은 새로 생성
		String accessTokenValue = jwtUtil.generateToken(Map.of("uid", uid), 60 * 24 * 7);

		String refreshTokenValue = tokens.get("refreshToken");

		// RefreshToken이 3분도 안남았다면..
		if (gapTime < (1000 * 60 * 3)) {
			log.info("new Refresh Token required...  ");
			refreshTokenValue = jwtUtil.generateToken(Map.of("uid", uid), 60 * 24 * 30);
			 
		}

		log.info("Refresh Token result....................");
		log.info("accessToken: " + accessTokenValue);
		log.info("refreshToken: " + refreshTokenValue);

		sendTokens(accessTokenValue, refreshTokenValue, response);

	}

	private Map<String, String> parseRequestJSON(HttpServletRequest request) {
		try {
			// json으로 요청한 문자열을 얻는다
			String jsonText = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

			// JSON 문자열을 Map 객체로 변환 한다
			Map<String, String> jsonData = objectMapper.readValue(jsonText, Map.class);
			log.info("jsonData = {}", jsonData);

			return jsonData;

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	private void checkAccessToken(String accessToken) throws RefreshTokenException {

		try {
			jwtUtil.validateToken(accessToken);
		} catch (ExpiredJwtException expiredJwtException) {
			log.info("Access Token has expired");
			log.info("정상으로 로그만 출력하고 다음 단계 처리를 진행 할 수 있게 기능을 구현한다");
		} catch (Exception exception) {
			throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
		}
	}

	private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {

		try {
			return jwtUtil.validateToken(refreshToken);
		} catch (ExpiredJwtException expiredJwtException) {
			throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
		}
	}

	private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		try {
			objectMapper.writeValue(response.getWriter(),
					Map.of("accessToken", accessTokenValue, "refreshToken", refreshTokenValue));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
