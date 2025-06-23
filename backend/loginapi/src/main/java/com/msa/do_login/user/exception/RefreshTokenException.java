package com.msa.do_login.user.exception;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

public class RefreshTokenException extends RuntimeException {
	private static final long serialVersionUID = -1962334455914094938L;
	private static ObjectMapper objectMapper = new ObjectMapper();

	private ErrorCase errorCase;

	public enum ErrorCase {
		NO_ACCESS, BAD_ACCESS, NO_REFRESH, OLD_REFRESH, BAD_REFRESH
	}

	public RefreshTokenException(ErrorCase errorCase) {
		super(errorCase.name());
		this.errorCase = errorCase;
	}

	public void sendResponseError(HttpServletResponse response) {

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		try {
			objectMapper.writeValue(response.getWriter(), Map.of("msg", errorCase.name(), "time", new Date()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
