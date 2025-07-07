package com.msa.do_login.admin.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.msa.do_login.user.vo.UserVO;

@Mapper
public interface AdminDAO {
	
	public List<UserVO> getUserList(@Param("limitPageNo") int limitPageNo,
			  @Param("numPerPage")int numPerPage,
			  @Param("searchType") String searchType,
			  @Param("searchValue")String searchValue);

	public int getTotalCount(@Param("searchType") String searchType, @Param("searchValue") String searchValue);
	
	public UserVO getUserByUserNo(int userNo);
	
	public int grantManager(int userNo);
	
	public int revokeManager(int userNo);
}
