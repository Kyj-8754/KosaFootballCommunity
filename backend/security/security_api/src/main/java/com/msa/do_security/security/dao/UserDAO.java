package com.msa.do_security.security.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.msa.do_security.security.vo.LocalAccount;
import com.msa.do_security.security.vo.SocialAccountVO;
import com.msa.do_security.security.vo.UserVO;

@Mapper
public interface UserDAO {

	// 소셜 계정 존재 여부 확인
    SocialAccountVO findSocialAccountSecurity(@Param("provider") String provider, @Param("providerId") String providerId);

    // userNo로 사용자 정보 조회
    UserVO findUserByUserNoSecurity(@Param("userNo") int userNo);

    // 신규 사용자 등록
    void insertUserSecurity(UserVO userVO);

    // 신규 소셜 계정 등록
    void insertSocialAccountSecurity(SocialAccountVO socialAccountVO);
    
    public UserVO findByUserIdSecurity(@Param("username")String username);
    
    LocalAccount findAccountByUserNoSecurity(int userNo);
    
    
    
}