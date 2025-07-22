package com.msa.do_login.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.user.dto.UserRegisterDTO;
import com.msa.do_login.user.service.UserService;
import com.msa.do_login.user.util.InMemoryCode;
import com.msa.do_login.user.util.SmsUtil;
import com.msa.do_login.user.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "회원 관련 API", description = "회원 기능 관련 API (회원 가입, 비밀번호 설정 등)")
public class UserController {
	private final UserService userService;
	private final SmsUtil smsUtil;
	private final InMemoryCode codeStore;

	// 회원 가입
	@Operation(summary = "회원가입", description = "사용자 정보를 등록합니다.")
	@PostMapping("/na/register")
	public ResponseEntity<Map<String, Object>> register(@RequestBody UserRegisterDTO dto) {
		userService.register(dto);
		return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "회원가입 성공"));
	}

	// 아이디 중복 확인
	@Operation(summary = "아이디 중복 확인", description = "입력된 아이디가 이미 존재하는지 확인합니다.")
	@PostMapping("/na/isExistUserId")
	public ResponseEntity<Map<String, Object>> isExistUserId(@RequestBody Map<String, String> payload) {
		boolean exists = userService.getLocalAccount(payload.get("userId")) != null;

		Map<String, Object> res = Map.of("res_code", exists ? "409" : "200", "res_msg",
				exists ? "이미 존재하는 아이디입니다." : "사용 가능한 아이디입니다.", "existUserId", exists);

		return exists ? ResponseEntity.status(HttpStatus.CONFLICT).body(res) : ResponseEntity.ok(res);
	}
	
	// 전화번호 중복 확인
	@Operation(summary = "전화번호 중복 확인", description = "입력된 전화번호가 이미 존재하는지 확인합니다.")
	@PostMapping("/na/isExistPhone")
	public ResponseEntity<Map<String, Object>> isExistPhone(@RequestBody Map<String, String> payload) {
		String userPhone = payload.get("userPhone");
		Map<String, Object> res = new HashMap<>();
		
		if (userPhone == null || userPhone.isBlank()) {
	        res.put("res_code", "400");
	        res.put("res_msg", "전화번호가 누락되었습니다.");
	        return ResponseEntity.badRequest().body(res);
	    }

	    boolean exists = userService.existsByPhone(userPhone);

	    if (exists) {
	        res.put("res_code", "409");
	        res.put("res_msg", "이미 가입된 전화번호입니다.");
	    } else {
	        res.put("res_code", "200");
	        res.put("res_msg", "사용 가능한 전화번호입니다.");
	    }

	    return ResponseEntity.ok(res);
	}
	
	// 비밀번호 변경
	@Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인 후 새 비밀번호로 변경합니다.")
	@PostMapping("/updatePassword")
	public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody Map<String, Object> payload) {
	    Map<String, Object> res = new HashMap<>();

	    // 값 꺼내기
	    Integer userNo = (Integer) payload.get("userNo");
	    String currentPassword = (String) payload.get("currentPassword");
	    String newPassword = (String) payload.get("newPassword");

	    log.info("비밀번호 변경 요청: userNo = {}", userNo);

	    if (userNo == null || currentPassword == null || newPassword == null) {
	        res.put("res_code", "400");
	        res.put("res_msg", "입력 정보가 누락되었습니다.");
	        return ResponseEntity.badRequest().body(res);
	    }

	    // 비밀번호 변경 처리
	    boolean isUpdated = userService.updatePassword(userNo, currentPassword, newPassword);

	    if (isUpdated) {
	        res.put("res_code", "200");
	        res.put("res_msg", "비밀번호가 성공적으로 변경되었습니다.");
	        return ResponseEntity.ok(res);
	    } else {
	        res.put("res_code", "403");
	        res.put("res_msg", "현재 비밀번호가 일치하지 않거나 변경에 실패했습니다.");
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
	    }
	}
	
	// 회원 탈퇴
	@Operation(summary = "회원 탈퇴", description = "사용자 번호와 비밀번호를 입력하여 회원 탈퇴를 진행합니다.")
	@PostMapping("/deleteMember")
	public ResponseEntity<Map<String, Object>> deleteMember(@RequestBody Map<String, Object> payload) {
	    Map<String, Object> res = new HashMap<>();

	    try {
	        int userNo = Integer.parseInt(String.valueOf(payload.get("userNo")));
	        String password = String.valueOf(payload.get("password"));
	        String loginType = String.valueOf(payload.get("loginType"));

	        log.info("회원 탈퇴 요청: userNo = {}, loginType = {}", userNo, loginType);

	        if (password == null || password.isBlank()) {
	            res.put("res_code", "400");
	            res.put("res_msg", "비밀번호는 필수입니다.");
	            return ResponseEntity.badRequest().body(res);
	        }

	        boolean success = userService.deleteMember(userNo, password, loginType);

	        if (success) {
	            res.put("res_code", "200");
	            res.put("res_msg", "회원 탈퇴가 완료되었습니다.");
	            return ResponseEntity.ok(res);
	        } else {
	            res.put("res_code", "403");
	            res.put("res_msg", "비밀번호가 일치하지 않아 탈퇴할 수 없습니다.");
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
	        }

	    } catch (NumberFormatException e) {
	        res.put("res_code", "400");
	        res.put("res_msg", "userNo 값이 유효하지 않습니다.");
	        return ResponseEntity.badRequest().body(res);

	    } catch (Exception e) {
	        res.put("res_code", "500");
	        res.put("res_msg", "서버 오류: " + e.getMessage());
	        return ResponseEntity.internalServerError().body(res);
	    }
	}

	
	// 인증 코드 전송
	@Operation(summary = "SMS 인증코드 전송", description = "입력된 전화번호로 인증코드를 전송합니다.")
	@PostMapping("/na/smsCode")
	public ResponseEntity<Map<String, Object>> sendSmsToCertification(@RequestParam("userPhone") String userPhone) {
		Map<String, Object> res = new HashMap<>();
		
		log.info("[문자 인증] 인증 요청 수신 - 전화번호: {}", userPhone);
        String verificationCode = userService.createCode();
        
        log.info("[문자 인증] 생성된 인증 코드: {}", verificationCode);
        smsUtil.sendOne(userPhone, verificationCode);
        log.info("[문자 인증] 문자 전송 완료 - 수신자: {}", userPhone);
        
        //인증코드 유효기간 5분 설정
        codeStore.setCodeWithExpiry(verificationCode, userPhone, 60 * 5L);
        
        log.info("[문자 인증] 인증 코드 유효시간 5분 설정 완료");
        res.put("res_code", "200");
        res.put("res_msg", "문자가 성공적으로 전송되었습니다.");
        return ResponseEntity.ok(res);
    }
	
	// 인증 코드 검증
	@Operation(summary = "SMS 인증코드 검증", description = "전송된 인증코드를 검증합니다.")
	@PostMapping("/na/verify")
	public ResponseEntity<Map<String, Object>> verifyCode(
	        @RequestParam("userPhone") String userPhone,
	        @RequestParam("smsCode") String code) {

	    Map<String, Object> res = new HashMap<>();
	    log.info("인증 코드 검증 요청: userPhone={}, code={}", userPhone, code);
	    // 1. 코드 존재 여부 확인
	    if (!codeStore.exists(code)) {
	        res.put("res_code", "400");
	        res.put("res_msg", "코드가 만료되었거나 잘못되었습니다.");
	        return ResponseEntity.badRequest().body(res);
	    }

	    // 2. 저장된 전화번호와 비교
	    String storedPhone = codeStore.getPhoneByCode(code);
	    if (!storedPhone.equals(userPhone)) {
	        res.put("res_code", "401");
	        res.put("res_msg", "전화번호가 일치하지 않습니다.");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
	    }

	    // 3. 인증 성공 후 코드 제거
	    codeStore.removeCode(code);

	    res.put("res_code", "200");
	    res.put("res_msg", "인증에 성공했습니다.");
	    return ResponseEntity.ok(res);
	}
	
	// 아이디 찾기
	@Operation(summary = "아이디 찾기", description = "이름과 전화번호를 통해 아이디를 조회합니다.")
	@PostMapping("/na/findId")
	public ResponseEntity<Map<String, Object>> findUserId(
			@RequestBody Map<String, String> payload) {
		String userName = payload.get("userName");
	    String userPhone = payload.get("userPhone");
	    Map<String, Object> res = new HashMap<>();

	    log.info("아이디 찾기 요청 - 이름: {}, 전화번호: {}", userName, userPhone);

	    if (userName == null || userPhone == null) {
	        res.put("res_code", "400");
	        res.put("res_msg", "이름 또는 전화번호가 누락되었습니다.");
	        return ResponseEntity.badRequest().body(res);
	    }

	    // userService에서 사용자 조회
	    String foundUserId = userService.findUserId(userName, userPhone);

	    if (foundUserId != null) {
	        res.put("res_code", "200");
	        res.put("res_msg", "회원님의 아이디는 [" + foundUserId + "]입니다.");
	    } else {
	        res.put("res_code", "404");
	        res.put("res_msg", "일치하는 회원 정보를 찾을 수 없습니다.");
	    }

	    return ResponseEntity.ok(res);
	}

	// 비밀번호 찾기
	@Operation(summary = "비밀번호 찾기", description = "아이디, 이름, 전화번호로 회원 정보를 확인합니다.")
	@PostMapping("/na/findPwd")
	public ResponseEntity<Map<String, Object>> resetUserPwd(
			@RequestBody Map<String, String> payload) {
		String userId = payload.get("userId");
		String userName = payload.get("userName");
	    String userPhone = payload.get("userPhone");
	    Map<String, Object> res = new HashMap<>();

	    log.info("비밀번호 찾기 요청 -아이디: {}, 이름: {}, 전화번호: {}", userId, userName, userPhone);

	    if (userId == null || userName == null || userPhone == null) {
	        res.put("res_code", "400");
	        res.put("res_msg", "필수 입력값이 누락되었습니다.");
	        return ResponseEntity.badRequest().body(res);
	    }

	    UserVO user = userService.getUserByUserIdNamePhone(userId, userName, userPhone);

	    if (user != null) {
	        res.put("res_code", "200");
	        res.put("res_msg", "비밀번호를 재설정합니다.");
	        res.put("userNo", user.getUserNo());
	    } else {
	        res.put("res_code", "404");
	        res.put("res_msg", "일치하는 회원 정보를 찾을 수 없습니다.");
	    }

	    return ResponseEntity.ok(res);
	}
	
	// 비밀번호 재설정
	@Operation(summary = "비밀번호 재설정", description = "비밀번호를 재설정합니다.")
	@PostMapping("/na/resetPassword")
	public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, Object> payload) {
	    Map<String, Object> res = new HashMap<>();

	    // 값 꺼내기
	    String userNoStr = String.valueOf(payload.get("userNo"));
	    int userNo = Integer.parseInt(userNoStr);
	    String newPassword = (String) payload.get("newPassword");

	    log.info("비밀번호 변경 요청: userNo = {}", userNo);

	    if (userNoStr == null || newPassword == null) {
	        res.put("res_code", "400");
	        res.put("res_msg", "입력 정보가 누락되었습니다.");
	        return ResponseEntity.badRequest().body(res);
	    }

	    // 비밀번호 변경 처리
	    boolean isUpdated = userService.resetPassword(userNo, newPassword);

	    if (isUpdated) {
	        res.put("res_code", "200");
	        res.put("res_msg", "비밀번호가 성공적으로 변경되었습니다.");
	        return ResponseEntity.ok(res);
	    } else {
	        res.put("res_code", "403");
	        res.put("res_msg", "비밀번호 재설정에 실패했습니다.");
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
	    }
	}

}
