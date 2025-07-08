package com.msa.do_login.user.service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.msa.do_login.user.dao.LoginDAO;
import com.msa.do_login.user.dto.UserRegisterDTO;
import com.msa.do_login.user.util.InMemoryCode;
import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.UserVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final LoginDAO loginDAO;
	private final PasswordEncoder passwordEncoder;
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final SecureRandom RANDOM = new SecureRandom();
	private final InMemoryCode inMemoryCode;

	// 회원에게 부여할 랜덤 코드 검증 로직
	private String generateUniqueUserCode(int length) {
		String code;
		// 회원 코드 중복 확인
		do {
			code = generateRandomCode(length);
		} while (loginDAO.existsByUserCode(code));
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
		loginDAO.insertUser(user);
		String encodedPwd = passwordEncoder.encode(dto.getUserPwd());
		// 2. LocalAccount 생성 및 저장
		LocalAccount account = LocalAccount.builder().userNo(user.getUserNo()).userId(dto.getUserId())
				.userPwd(encodedPwd).build();
		loginDAO.insertLocalAccount(account);
	}

	// 존재하는 아이디 여부 확인
	public LocalAccount getLocalAccount(String userId) {
		return loginDAO.getLocalAccount(userId);
	}

	// 회원 정보 가져오기
	public Optional<UserVO> getMemberByUserId(String userId) {
		return loginDAO.findByUserId(userId);
	}

	// 비밀번호 변경
	public boolean updatePassword(int userNo, String currentPassword, String newPassword) {
		UserVO user = loginDAO.findUserByUserNo(userNo);
		LocalAccount account = loginDAO.findAccountByUserNo(userNo);
		if (user == null || account == null)
			return false;

		// 비밀번호 비교
		if (!passwordEncoder.matches(currentPassword, account.getUserPwd())) {
			return false;
		}

		// 새 비밀번호 암호화 후 저장
		String encodedNewPwd = passwordEncoder.encode(newPassword);
		return loginDAO.updatePassword(userNo, encodedNewPwd);
	}
	
	// 문자 인증용 번호 생성 
	public String createCode() {
	    Random random = new Random();
	    String code;
	    do {
	        int number = random.nextInt(1_000_000); // 0 ~ 999999
	        code = String.format("%06d", number); // 6자리 문자열, 앞에 0 채움
	    } while (inMemoryCode.exists(code));

	    return code;
	}
	
	public String findUserId(String userName, String userPhone) {
	    UserVO user = loginDAO.findUserByUserNameAndUserPhone(userName, userPhone);
	    if (user == null) return null;
	    LocalAccount account = loginDAO.findAccountByUserNo(user.getUserNo());
	    if (account == null) return null;
	    return account.getUserId();
	}
	
	public boolean existsByPhone(String userPhone) {
		return loginDAO.findUserByPhone(userPhone) != null;
	}
	
	public UserVO getUserByUserIdNamePhone(String userId, String userName, String userPhone) {
	    UserVO user = loginDAO.findUserByUserNameAndUserPhone(userName, userPhone);
	    if (user == null) return null;

	    LocalAccount account = loginDAO.findAccountByUserNo(user.getUserNo());
	    if (account == null) return null;

	    if (account.getUserId().equals(userId)) {
	        return user;
	    }
	    return null;
	}
	
	// 비밀번호 재설정
	public boolean resetPassword(int userNo, String newPassword) {
		UserVO user = loginDAO.findUserByUserNo(userNo);
		LocalAccount account = loginDAO.findAccountByUserNo(userNo);
		if (user == null || account == null)
			return false;

		// 새 비밀번호 암호화 후 저장
		String encodedNewPwd = passwordEncoder.encode(newPassword);
		return loginDAO.updatePassword(userNo, encodedNewPwd);
	}
}
