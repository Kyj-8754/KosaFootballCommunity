package com.msa.do_login.admin.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.msa.do_login.user.vo.UserVO;

@Mapper
public interface AdminDAO {
	public List<UserVO> getUserList();
}
