package com.msa.do_login.user.dao;

import org.apache.ibatis.annotations.Mapper;

import com.msa.do_login.user.vo.User;

@Mapper
public interface UserDAO {
	  public int register(User user);
}
