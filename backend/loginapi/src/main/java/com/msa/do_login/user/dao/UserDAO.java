package com.msa.do_login.user.dao;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.UserVO;

@Mapper
public interface UserDAO {

	public void insertUser(UserVO user);

	public void insertLocalAccount(LocalAccount account);

	public LocalAccount getLocalAccount(String userId);

	boolean existsByUserCode(String userCode);

	public String getAuthNameByCode(String authCode);

	LocalAccount findAccountByUserNo(int userNo);

	Optional<UserVO> findByUserId(String userName);
}