package com.msa.do_security.security.util;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTUtil {
	@Value("${com.msa.do_security.jwt.secret}")
	private String key; // 서버만 알고 있는 비밀키값

	public byte[] getSecretKey() {
		try {
			log.info("key = {}", key);
			return Base64.getEncoder().encode(key.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String generateToken(Map<String, Object> valueMap, int minutes) {

		log.info("generateKey...  : " + key);

		// 헤더 부분
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");

		// payload 부분 설정
		Map<String, Object> payloads = new HashMap<>();
		payloads.putAll(valueMap);


		int time = (1) * minutes;


		try {
			String jwtStr = Jwts.builder().header().add("typ", "JWT").add("alg", "HS256") // 헤더 설정
					.and().claims(payloads) // 클레임 정보
					.issuedAt(Date.from(ZonedDateTime.now().toInstant())) // 발급 시간
					.expiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant())) // 만료 시간
					.signWith(Keys.hmacShaKeyFor(getSecretKey()), Jwts.SIG.HS256) // 서명
					.compact();

			log.info("jwtStr...  : " + jwtStr);
			return jwtStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> validateToken(String token) throws JwtException {

		Map<String, Object> claim = null;

		// 인증 토큰 문자열을 이용하여 클래임 객체를 얻는다
		claim = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(getSecretKey())).build().parseSignedClaims(token)
				.getPayload();

		return claim;
	}
}
