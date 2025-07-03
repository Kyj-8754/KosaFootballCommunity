package com.msa.do_login.admin.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.admin.service.AdminService;
import com.msa.do_login.user.vo.UserVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	private final AdminService adminService;

	// 유저 목록 조회
	@GetMapping("/userList")
	public ResponseEntity<Map<String, Object>> getFriendList() {
		log.info("유저 목록 조회 요청 도착");

		List<UserVO> userList = adminService.getUserList();
		Map<String, Object> res = new HashMap<>();

		if (userList == null || userList.isEmpty()) {
			res.put("res_code", "204");
			res.put("res_msg", "유저가 없습니다.");
			res.put("data", Collections.emptyList());
		} else {
			res.put("res_code", "200");
			res.put("res_msg", "유저 목록 조회 성공");
			res.put("data", userList);
		}

		return ResponseEntity.ok(res);
	}
}
