package com.msa.do_login.myPage.service;

import org.springframework.stereotype.Service;

import com.msa.do_login.myPage.dao.MyPageDAO;
import com.msa.do_login.myPage.dto.UserInfoDTO;
import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.UserStat;
import com.msa.do_login.user.vo.UserStyle;
import com.msa.do_login.user.vo.UserVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {
	private final MyPageDAO myPageDAO;
	
	// 회원 정보 불러오기 
	public UserInfoDTO getMemberInfo(int userNo) {
		UserVO userVO= myPageDAO.getUserVO(userNo);
		UserStyle userStyle = myPageDAO.getUserStyle(userVO.getStyleCode());
		UserStat userStat = myPageDAO.getUserStat(userVO.getStatCode());
		LocalAccount localAccount = myPageDAO.getLocalAccount(userNo);

	    UserInfoDTO userInfo = UserInfoDTO.of(userVO, localAccount);
	    if (userStyle != null) {
	        userInfo.setStyleName(userStyle.getStyleName());
	    }

	    if (userStat != null) {
	        userInfo.setStatName(userStat.getStatName());
	    }
	    return userInfo;
	}
	
}
