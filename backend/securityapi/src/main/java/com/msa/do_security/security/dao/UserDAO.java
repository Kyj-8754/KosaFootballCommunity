package com.msa.do_security.security.dao;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.msa.do_security.security.vo.LocalAccount;
import com.msa.do_security.security.vo.UserVO;

@Mapper
public interface UserDAO {

	public LocalAccount findAccountByUserNo(int userNo);

	public Optional<UserVO> findByUserId(String username);
}