package com.msa.do_login.user.dao;

import org.apache.ibatis.annotations.Mapper;

import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.User;

@Mapper
public interface UserDAO {
	  public void insertUser(User user);
	  public void insertLocalAccount(LocalAccount account);
	  public LocalAccount getLocalAccount(String userId);
	  boolean existsByUserCode(String userCode);
}