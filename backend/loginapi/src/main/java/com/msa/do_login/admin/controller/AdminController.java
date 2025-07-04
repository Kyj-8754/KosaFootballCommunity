package com.msa.do_login.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.admin.service.AdminService;
import com.msa.do_login.page.Paging;
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
	public ResponseEntity<Map<String, Object>> getUserList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "searchType", required = false, defaultValue = "userName") String searchType,
			@RequestParam(value = "searchValue", required = false) String searchValue) {
		log.info("회원 목록 조회 요청 - page: {}, size: {}, searchType: {}, searchValue: {}", page, size, searchType, searchValue);
		Paging paging = new Paging();
		paging.setNowPage(page);
		paging.setNumPerPage(size);
		paging.setTotalData(adminService.getTotalMemberCount(searchType, searchValue));
		paging.calcPaging();

		List<UserVO> userList = adminService.getUserList(paging, searchType, searchValue);
		return ResponseEntity
				.ok(Map.of("res_code", "200", "res_msg", "회원 목록 조회 성공", "userList", userList, "paging", paging));
	}
	
}
