package com.msa.kyj_prj.member;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.msa.kyj_prj.page.PageResponseVO;

@Service
public class LoginService{
	// 인터페이스 자동 상속
	@Autowired
	private LoginDAO loginDAO;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// 로그인 최대값
	private int max_fail = 5;

	// 로그인
	public LoginResult login(String userid, String passwd) {
		// 회원 정보 받아오기
		Member member = loginDAO.getMember(userid);
		// 회원 정보 판단 여부, 탈퇴한사람은 로그인 불가능
		if (member == null || member.getIs_deleted() == 'Y') {
			return new LoginResult(LoginStatus.NO_USER, null, 0);
		}
		// 회원 차단여부 판단로직
		 if (member.getFail_login() >= max_fail) {
		        return new LoginResult(LoginStatus.FAIL_LOCKED, null, member.getFail_login());
		    }
		 
		// 회원 아이디/비밀번호 검증
		if (!passwordEncoder.matches(passwd, member.getPasswd())) {
			failLogin(userid);
	        int fails = member.getFail_login()+1;
	        return new LoginResult(LoginStatus.FAIL_CREDENTIALS, null, fails);
	    }
		
		successLogin(userid);
	    
	    return new LoginResult(LoginStatus.SUCCESS, member, 0);
	}
	

	// 멤버정보 가져오기
	public Member getMember(String userid) {
		return loginDAO.getMember(userid);
	}
	
	// 멤버 업데이트
	@Transactional
	public boolean update(Member member) {
			return loginDAO.update(member) > 0;
	}
	
	
	// 회원 등록
	@Transactional
	public int registForm(Member member) {
		return loginDAO.regist(member);
	}
	// 회원 리스트
	public PageResponseVO list(String searchValue, int pageNumber, int size) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start", (pageNumber-1)*size+1);
		map.put("end", pageNumber * size);
		map.put("searchValue", searchValue);
		
		return new PageResponseVO(
				loginDAO.list(map),
				loginDAO.getTotalCount(map),
				pageNumber,
				size,
				searchValue);
		
	}
	// 회원 삭제
	@Transactional
	public boolean delete(String userid) {
		return loginDAO.delete(userid);
	}
	
	//회원 밴처리
	@Transactional
	public void ban(String userid) {
		loginDAO.ban(userid);
	}

	//회원 밴해제처리
	@Transactional
	public void unban(String userid) {
		loginDAO.unban(userid);
	}
	
	//로그인 한시간 기록
	// 로그인 횟수 초기화
	@Transactional
	public void successLogin(String userid) {
			loginDAO.setLoginTime(userid);
		    loginDAO.unban(userid);
	};
	
	//로그인실패
	@Transactional
	public void failLogin(String userid) {
		loginDAO.fail_login(userid);
	}
	
	
	//유저 정보 찾기
	public Member findMember(String username, String phone_no, String userid) {
		return loginDAO.findMember(username, phone_no, userid);
	}


	public boolean rePasswd(String userid, String hash) {
		return loginDAO.rePasswd(userid, hash);
	}
	
}
