package com.msa.do_login.user.service;

import java.security.SecureRandom;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.msa.do_login.user.dao.UserDAO;
import com.msa.do_login.user.dto.UserRegisterDTO;
import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.UserVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserDAO userDAO;
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final SecureRandom RANDOM = new SecureRandom();
	
	// 회원에게 부여할 랜덤 코드 검증 로직
	private String generateUniqueUserCode(int length) {
		String code;
		// 회원 코드 중복 확인
		do {
			code = generateRandomCode(length);
		} while (userDAO.existsByUserCode(code));
		return code;
	}

	// 랜덤한 영문/숫자 조합 코드 생성
	private String generateRandomCode(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
		}
		return sb.toString();
	}

	// 로컬 회원 가입 시 회원 정보와 아이디 비밀번호 분리 저장
	@Transactional
	public void register(UserRegisterDTO dto) {
		String userCode = generateUniqueUserCode(5);
		// 1. User 생성 및 저장
		UserVO user = UserVO.builder().userName(dto.getUserName()).userBirth(dto.getUserBirth())
				.userPhone(dto.getUserPhone()).userPostCode(dto.getUserPostcode()).userAddr(dto.getUserAddr())
				.userDetailAddr(dto.getUserDetailAddr()).userGender(dto.getUserGender()).authCode("A3")
				.userCode(userCode).userRegDate(new Date()).build();
		userDAO.insertUser(user);

		// 2. LocalAccount 생성 및 저장
		LocalAccount account = LocalAccount.builder().userNo(user.getUserNo()).userId(dto.getUserId())
				.userPwd(dto.getUserPwd()).build();
		userDAO.insertLocalAccount(account);
	}

	// 존재하는 아이디 여부 확인
	public LocalAccount getLocalAccount(String userId) {
		return userDAO.getLocalAccount(userId);
	}

}
