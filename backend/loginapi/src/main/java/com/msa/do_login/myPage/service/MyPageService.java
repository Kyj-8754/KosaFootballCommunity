package com.msa.do_login.myPage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.msa.do_login.myPage.dao.MyPageDAO;
import com.msa.do_login.myPage.dto.FriendDTO;
import com.msa.do_login.user.vo.UserStat;
import com.msa.do_login.user.vo.UserStyle;
import com.msa.do_login.user.vo.UserVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {
	private final MyPageDAO myPageDAO;
	private final PasswordEncoder passwordEncoder;
	
	public UserVO getUserVO(int userNo) {
		return myPageDAO.getUserVO(userNo);
	}
	
	public int update(UserVO member) {
		int result = 0;
		UserVO memberDB = myPageDAO.getUserVO(member.getUserNo());
		if (memberDB == null) {
			return result;
		}
		memberDB.setUserName(member.getUserName());
	    memberDB.setUserBirth(member.getUserBirth());
	    memberDB.setUserPhone(member.getUserPhone());
	    memberDB.setUserPostCode(member.getUserPostCode());
	    memberDB.setUserAddr(member.getUserAddr());
	    memberDB.setUserDetailAddr(member.getUserDetailAddr());
	    memberDB.setUserGender(member.getUserGender());
	    log.info("mod_stat_code 값: {}", memberDB.getStatCode());
		result = myPageDAO.update(memberDB);
		myPageDAO.history(memberDB);
		if (result == 0) {
			return result;
		} else {
			result = 1;
			return result;
		}
	}
	
	public int updateProfile(UserVO member) {
		int result = 0;
		UserVO memberDB = myPageDAO.getUserVO(member.getUserNo());
		if (memberDB == null) {
			return result;
		}
		memberDB.setUserName(member.getUserName());
	    memberDB.setUserComment(member.getUserComment());
	    memberDB.setStyleCode(member.getStyleCode());
	    memberDB.setStatCode(member.getStatCode());
		result = myPageDAO.updateProfile(memberDB);
		if (result == 0) {
			return result;
		} else {
			result = 1;
			return result;
		}
	}
	
	public FriendDTO searchFriendByKeyword (String keyword, int loginUserNo) {
		UserVO result = myPageDAO.searchFriendByKeyword(keyword);
		String status = myPageDAO.getRelationStatus(result.getUserNo(), loginUserNo);
		FriendDTO dto = new FriendDTO();
		dto.setUserNo(result.getUserNo());
		dto.setUserName(result.getUserName());
		dto.setUserAddr(result.getUserAddr());
		dto.setRelationStatus(status);
		return dto;
	}
	
	public boolean requestFriend(int requesterNo, int requestedNo) {
	    try {
	        log.info("insert 요청: requesterNo={}, requestedNo={}", requesterNo, requestedNo);
	        myPageDAO.insertFriendRequest(requesterNo, requestedNo);
	        return true;
	    } catch (Exception e) {
	        log.error("친구 요청 중 예외 발생", e);
	        return false;
	    }
	}
	
	public List<UserVO> getPendingRequests(int userNo) {
	    return myPageDAO.getPendingRequests(userNo);
	}
	
	public boolean acceptFriendRequest(int requesterNo, int receiverNo) {
	    int updated = myPageDAO.updateRelationStatusToAccept(requesterNo, receiverNo);
	    return updated > 0;
	}

	public boolean rejectFriendRequest(int requesterNo, int receiverNo) {
	    int deleted = myPageDAO.deleteRelationRequest(requesterNo, receiverNo);
	    return deleted > 0;
	}
	
	public List<UserVO> getFriendList(int userNo) {
	    return myPageDAO.getFriendList(userNo);
	}
	
	public List<FriendDTO> getOtherFriendList(int targetUserNo, int loginUserNo) {
		List<UserVO> friendList = myPageDAO.getFriendList(targetUserNo);
		List<FriendDTO> dtoList = new ArrayList<>();
		for (UserVO friend : friendList) {
			String status = myPageDAO.getRelationStatus(friend.getUserNo(), loginUserNo);
			FriendDTO dto = new FriendDTO();
			dto.setUserNo(friend.getUserNo());
			dto.setUserName(friend.getUserName());
			dto.setUserAddr(friend.getUserAddr());
			dto.setRelationStatus(status);
			dtoList.add(dto);
		}
		return dtoList;
	}
	
	public UserStyle getStyleName(int styleCode) {
		return myPageDAO.getStyleName(styleCode);
	}
	
	public UserStat getStatName(int statCode) {
		return myPageDAO.getStatName(statCode);
	}
	
}
