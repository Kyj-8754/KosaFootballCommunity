package com.msa.do_login.myPage.dao;

import org.apache.ibatis.annotations.Mapper;

import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.UserStat;
import com.msa.do_login.user.vo.UserStyle;
import com.msa.do_login.user.vo.UserVO;

@Mapper
public interface MyPageDAO {
	public UserVO getUserVO(int userNo);
	public UserStyle getUserStyle(int styleCode);
	public UserStat getUserStat(int statCode);
	public LocalAccount getLocalAccount(int userNo);
}
