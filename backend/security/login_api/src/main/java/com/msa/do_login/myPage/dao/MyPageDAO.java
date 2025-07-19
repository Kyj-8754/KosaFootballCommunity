package com.msa.do_login.myPage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.msa.do_login.myPage.dto.MyClubInfoDTO;
import com.msa.do_login.user.vo.LocalAccount;
import com.msa.do_login.user.vo.UserStat;
import com.msa.do_login.user.vo.UserStyle;
import com.msa.do_login.user.vo.UserVO;
import com.msa.do_security.security.vo.SocialAccountVO;

@Mapper
public interface MyPageDAO {
	public UserVO getUserVO(int userNo);

	public UserStyle getUserStyle(int styleCode);

	public UserStat getUserStat(int statCode);

	public LocalAccount getLocalAccount(int userNo);

	public SocialAccountVO getSocialAccount(int userNo);

	public int update(UserVO user);
	
	public int updateProfile(UserVO user);

	public void history(UserVO user);

	public UserVO searchFriendByKeyword(String keyword);

	public void insertFriendRequest(@Param("requesterNo") int requesterNo, @Param("requestedNo") int requestedNo);

	public List<UserVO> getPendingRequests(int userNo);

	public int updateRelationStatusToAccept(@Param("requesterNo") int requesterNo,
			@Param("requestedNo") int requestedNo);

	public int deleteRelationRequest(@Param("requesterNo") int requesterNo, @Param("requestedNo") int requestedNo);

	public List<UserVO> getFriendList(int userNo);
	
	public String getRelationStatus(@Param("targetUserNo") int targetUserNo,
			@Param("loginUserNo") int loginUserNo);
	
	public UserStyle getStyleName(int styleCode);
	
	public UserStat getStatName(int statCode);
	
	// user no로 user name을 찾는 메소드
	public String findUserNameByUserNo(@Param("user_no") int user_no);
	
	public List<MyClubInfoDTO> getClubList(int userNo);
	
	public Double getMannerScore(int userNo);

	public Double getlevel(int userNo);
	
	public int getMatchCount(int userNo);
	
	public int getPOMCount(int userNo);
	
	public int getSmileCardCount(int userNo);
	
	public int getYellowCardCount(int userNo);
	
	public int getRedCardCount(int userNo);
}
