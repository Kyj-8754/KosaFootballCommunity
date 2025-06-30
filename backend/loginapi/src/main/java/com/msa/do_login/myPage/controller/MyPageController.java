package com.msa.do_login.myPage.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.myPage.service.MyPageService;
import com.msa.do_login.user.vo.UserVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
	private final MyPageService myPageService;

	// 회원 상세정보 조회
	@GetMapping("/detailView")
	public ResponseEntity<Map<String, Object>> getMemberDetail(@RequestParam int userNo) {
		UserVO userInfo = myPageService.getUserVO(userNo);
		log.info("회원 정보 ="+userInfo);
		if (userInfo == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("res_code", "404", "res_msg", "해당 사용자를 찾을 수 없습니다."));
		}
		return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "회원 정보 조회 성공", "member", userInfo));
	}
}
