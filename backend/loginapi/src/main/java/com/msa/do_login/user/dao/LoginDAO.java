package com.msa.do_login.user.dao;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.SocialAccount;
import com.msa.do_login.user.vo.UserVO;
import com.msa.do_security.security.vo.SocialAccountVO;

@Mapper
public interface LoginDAO {
	
	// userNo로 사용자 정보 조회
    public UserVO findUserByUserNo(@Param("userNo") int userNo);
    
	public void insertUser(UserVO user);

	public void insertLocalAccount(LocalAccount account);
	
	public void insertSocialAccount(SocialAccount socialAccount);

	public LocalAccount getLocalAccount(String userId);

	boolean existsByUserCode(String userCode);

	public LocalAccount findAccountByUserNo(int userNo);

	public Optional<UserVO> findByUserId(String username);
	
	public SocialAccount findSocialAccount(@Param("provider") String provider, @Param("providerId") String providerId);
	
	public boolean updatePassword(@Param("userNo") int userNo, @Param("encodedNewPwd")String encodedNewPwd);
}