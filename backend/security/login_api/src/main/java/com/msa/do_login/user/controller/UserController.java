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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	private final UserService userService;
	private final SmsUtil smsUtil;
	private final InMemoryCode codeStore;

	// 회원 가입
	@PostMapping("/na/register")
	public ResponseEntity<Map<String, Object>> register(@RequestBody UserRegisterDTO dto) {
		userService.register(dto);
		return ResponseEntity.ok(Map.of("res_code", "200", "res_msg", "회원가입 성공"));
	}

	// 이메일 중복 확인
	@PostMapping("/na/isExistUserId")
	public ResponseEntity<Map<String, Object>> isExistUserId(@RequestBody Map<String, String> payload) {
		boolean exists = userService.getLocalAccount(payload.get("userId")) != null;

		Map<String, Object> res = Map.of("res_code", exists ? "409" : "200", "res_msg",
				exists ? "이미 존재하는 아이디입니다." : "사용 가능한 아이디입니다.", "existUserId", exists);

		return exists ? ResponseEntity.status(HttpStatus.CONFLICT).body(res) : ResponseEntity.ok(res);
	}
	
	// 전화번호 중복 확인
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
	
	// 인증 코드 전송
	@PostMapping("/na/smsCode")
	public ResponseEntity<Map<String, Object>> sendSmsToCertification(@RequestParam("userPhone") String userPhone) {
		Map<String, Object> res = new HashMap<>();
		
		log.info("[문자 인증] 인증 요청 수신 - 전화번호: {}", userPhone);
        String verificationCode = userService.createCode();
        
        log.info("[문자 인증] 생성된 인증 코드: {}", verificationCode);
        // smsUtil.sendOne(userPhone, verificationCode);
        System.out.println("[테스트용 인증번호]"+verificationCode);
        log.info("[문자 인증] 문자 전송 완료 - 수신자: {}", userPhone);
        //인증코드 유효기간 5분 설정
        codeStore.setCodeWithExpiry(verificationCode, userPhone, 60 * 5L);
        
        log.info("[문자 인증] 인증 코드 유효시간 5분 설정 완료");
        res.put("res_code", "200");
        res.put("res_msg", "문자가 성공적으로 전송되었습니다.");
        return ResponseEntity.ok(res);
    }
	
	// 인증 코드 검증
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
