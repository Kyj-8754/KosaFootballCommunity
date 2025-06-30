package com.msa.do_login.myPage.service;

import org.springframework.stereotype.Service;

import com.msa.do_login.myPage.dao.MyPageDAO;
import com.msa.do_login.user.vo.UserVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {
	private final MyPageDAO myPageDAO;
	
	public UserVO getUserVO(int userNo) {
		return myPageDAO.getUserVO(userNo);
	}
	
}
