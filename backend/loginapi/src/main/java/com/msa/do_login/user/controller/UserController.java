package com.msa.do_login.user.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.user.dto.UserRegisterDTO;
import com.msa.do_login.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	private final UserService userService;
	
	// 회원 가입
	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> register(@RequestBody UserRegisterDTO dto) {
	    userService.register(dto);
	    return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "회원가입 성공"));
	}
	
	// 이메일 중복 확인
	@PostMapping("/isExistUserId")
	public ResponseEntity<Map<String, Object>> isExistUserId(@RequestBody Map<String, String> payload) {
	    boolean exists = userService.getLocalAccount(payload.get("userId")) != null;

	    Map<String, Object> res = Map.of(
	        "res_code", exists ? "409" : "200",
	        "res_msg", exists ? "이미 존재하는 아이디입니다." : "사용 가능한 아이디입니다.",
	        "existUserId", exists
	    );

	    return exists
	        ? ResponseEntity.status(HttpStatus.CONFLICT).body(res)
	        : ResponseEntity.ok(res);
	}
	
}
