package com.msa.do_login.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.msa.do_login.admin.dao.AdminDAO;
import com.msa.do_login.page.Paging;
import com.msa.do_login.user.vo.UserVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminDAO adminDAO;
	
	// 유저 리스트 조회
	public List<UserVO> getUserList(Paging paging, String searchType, String searchValue){
		return adminDAO.getUserList(paging.getLimitPageNo(), paging.getNumPerPage(), searchType, searchValue);
	}
	
	// 회원 수 검색 로직
	public int getTotalMemberCount(String searchType, String searchValue) {
		return (int) adminDAO.getTotalCount(searchType, searchValue);
	}
	
	// 회원 매니저 권한 부여 
	public int grantManager(int userNo) {
		int result = 0;
		UserVO userDB = adminDAO.getUserByUserNo(userNo);
		if (userDB == null) {
			return result;
		}
		return adminDAO.grantManager(userNo);
	}
	
	// 회원 매니저 권한 해제 
	public int revokeManager(int userNo) {
		int result = 0;
		UserVO userDB = adminDAO.getUserByUserNo(userNo);
		if (userDB == null) {
			return result;
		}
		return adminDAO.revokeManager(userNo);
	}
}
