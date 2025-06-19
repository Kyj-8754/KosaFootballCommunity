package com.msa.do_login.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.user.service.UserService;
import com.msa.do_login.user.vo.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/register")
	public Map<String, Object> register(@RequestBody User user) throws Exception {
		Map<String, Object> map = new HashMap<>();
		if (user == null) {
			map.put("res_code", "400");
			map.put("res_msg", "입력간에 오류가 발생하였습니다.");
			return map;
		}
		int result = userService.register(user);
		if (result == 0) {
			map.put("res_code", "400");
			map.put("res_msg", "회원등록에 실패하였습니다.");
		} else {
			map.put("res_code", "200");
			map.put("res_msg", "성공적으로 회원등록을 완료하였습니다.");
		}
		return map;
	}
}
