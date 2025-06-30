package com.msa.do_login.myPage.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.myPage.dto.UserInfoDTO;
import com.msa.do_login.myPage.service.MyPageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
	private final MyPageService myPageService;

	// 회원 상세정보 조회
	@GetMapping("/detailView/{userNo}")
	public ResponseEntity<Map<String, Object>> getMemberDetail(@PathVariable int userNo) {
		log.info("회원 상세정보 조회 요청 도착");
		UserInfoDTO memberInfo = myPageService.getMemberInfo(userNo);
		if (memberInfo == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("res_code", "404", "res_msg", "해당 사용자를 찾을 수 없습니다."));
		}
		log.info("회원 상세정보 조회 결과 반환 = {}",memberInfo);
		return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "회원 정보 조회 성공", "member", memberInfo));
	}
}
