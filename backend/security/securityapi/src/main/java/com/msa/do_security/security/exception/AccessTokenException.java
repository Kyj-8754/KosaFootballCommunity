package com.msa.do_security.security.exception;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

public class AccessTokenException extends RuntimeException {
	private static final long serialVersionUID = -1962334455914094938L;
	private static ObjectMapper objectMapper = new ObjectMapper();

	TOKEN_ERROR token_error;

	public enum TOKEN_ERROR {
		UNACCEPT(401, "Token is null or too short"), 
		BADTYPE(401, "Token type Bearer"), 
		MALFORM(403, "Malformed Token"),
		BADSIGN(403, "BadSignatured Token"), 
		EXPIRED(403, "Expired Token");

		private int status;
		private String msg;

		TOKEN_ERROR(int status, String msg) {
			this.status = status;
			this.msg = msg;
		}

		public int getStatus() {
			return this.status;
		}

		public String getMsg() {
			return this.msg;
		}
	}

	public AccessTokenException(TOKEN_ERROR token_error) {
		super();
		this.token_error = token_error;
	}

	// 예외정보를 응답 결과로 전달 할 수 있는 메소드
	public void sendResponseError(HttpServletResponse response) {

		response.setStatus(token_error.getStatus());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		try {
			objectMapper.writeValue(response.getWriter(), Map.of("msg", token_error.getMsg(), "time", new Date()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
