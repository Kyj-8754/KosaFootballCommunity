package com.msa.do_login.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.admin.service.AdminService;
import com.msa.do_login.page.Paging;
import com.msa.do_login.user.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "관리자 API", description = "관리자 기능 관련 API (회원 목록, 권한 설정 등)")
public class AdminController {
	private final AdminService adminService;

	// 유저 목록 조회
	@Operation(summary = "회원 목록 조회", description = "전체 회원 목록을 페이징 및 검색 조건에 따라 조회합니다.")
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
	
	// 사용자 관리자 권한 부여
	@Operation(summary = "관리자 권한 부여", description = "지정된 회원번호에 대해 관리자 권한을 부여합니다.")
    @PostMapping("/grantManager")
    public ResponseEntity<Map<String, Object>> grantManager(@RequestBody Map<String, String> param) {
        Map<String, Object> map = new HashMap<>();
        int result = adminService.grantManager(Integer.parseInt(param.get("userNo")));

        if (result == 0) {
            map.put("res_code", "400");
            map.put("res_msg", "관리자 권한 부여 중 오류가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        } else {
            map.put("res_code", "200");
            map.put("res_msg", "관리자 권한 부여에 성공하였습니다.");
            return ResponseEntity.ok(map);
        }
    }

    // 사용자 관리자 권한 해제
	@Operation(summary = "관리자 권한 해제", description = "지정된 회원번호에 대해 관리자 권한을 해제합니다.")
    @PostMapping("/revokeManager")
    public ResponseEntity<Map<String, Object>> revokeManager(@RequestBody Map<String, String> param) {
        Map<String, Object> map = new HashMap<>();
        int result = adminService.revokeManager(Integer.parseInt(param.get("userNo")));

        if (result == 0) {
            map.put("res_code", "400");
            map.put("res_msg", "관리자 권한 해제 중 오류가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        } else {
            map.put("res_code", "200");
            map.put("res_msg", "관리자 권한 해제에 성공하였습니다.");
            return ResponseEntity.ok(map);
        }
    }
	
}
