package com.msa.do_login.myPage.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msa.do_login.myPage.dao.MyPageDAO;
import com.msa.do_login.myPage.dto.FriendDTO;
import com.msa.do_login.myPage.dto.MyClubInfoDTO;
import com.msa.do_login.myPage.dto.UserProfileDTO;
import com.msa.do_login.myPage.service.MyPageService;
import com.msa.do_login.user.vo.UserStat;
import com.msa.do_login.user.vo.UserStyle;
import com.msa.do_login.user.vo.UserVO;
import com.msa.do_login.webSocket.WebSocket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Tag(name = "마이페이지 API", description = "마이페이지 기능 관련 API (정보 조회, 정보 수정 등)")
public class MyPageController {
	private final MyPageService myPageService;
	private final MyPageDAO myPageDAO;
	private final WebSocket websocket;
	
	// 회원 상세정보 조회
	@Operation(summary = "회원 상세정보 조회", description = "회원 번호를 통해 상세 정보를 조회합니다.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음")
    })
	@GetMapping("/detailView")
	public ResponseEntity<Map<String, Object>> getMemberDetail(@RequestParam int userNo) {
	    UserVO userInfo = myPageService.getUserVO(userNo);
	    log.info("회원 정보 = {}", userInfo);

	    if (userInfo == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Map.of("res_code", "404", "res_msg", "해당 사용자를 찾을 수 없습니다."));
	    }

	    Map<String, Object> response = new HashMap<>();
	    response.put("res_code", "200");
	    response.put("res_msg", "회원 정보 조회 성공");
	    response.put("member", userInfo);
	    
	    List<MyClubInfoDTO> myClubList = myPageService.getClubList(userNo);
	    response.put("myClubList", myClubList);
	    UserProfileDTO profileInfo = myPageService.getProfileInfo(userNo);
	    response.put("profileInfo", profileInfo);
	    
	    // 스타일 이름과 능력 이름은 null이 아닐 경우에만 추가
	    if (userInfo.getStyleCode() != null) {
	        UserStyle userStyle = myPageService.getStyleName(userInfo.getStyleCode());
	        response.put("userStyle", userStyle);
	    }

	    if (userInfo.getStatCode() != null) {
	        UserStat userStat = myPageService.getStatName(userInfo.getStatCode());
	        response.put("userStat", userStat);
	    }

	    return ResponseEntity.ok(response);
	}
	
	// 회원 정보 수정
	@Operation(summary = "회원 정보 수정", description = "회원 번호를 통해 회원 정보를 수정합니다.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
        @ApiResponse(responseCode = "400", description = "회원 정보 수정 실패")
    })
	@PostMapping("/update")
	public ResponseEntity<Map<String, Object>> update(
			@RequestParam("userNo") int userNo,
	        @RequestBody UserVO member
	) {
		Map<String, Object> map = new HashMap<>();
		
		log.info("수정 요청된 회원 번호: {}", userNo);
		log.info("수정 요청된 정보: {}", member);

		member.setUserNo(userNo);

		int result = myPageService.update(member);

		if (result == 0) {
			map.put("res_code", "400");
			map.put("res_msg", "회원 정보 수정 중 오류가 발생하였습니다.");
			return ResponseEntity.badRequest().body(map);
		} else {
			map.put("res_code", "200");
			map.put("res_msg", "회원 정보 수정에 성공하였습니다.");
			return ResponseEntity.ok(map);
		}
	}
	
	// 친구 목록 조회
	@Operation(summary = "친구 목록 조회", description = "회원 번호를 통해 친구 목록을 조회합니다.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공"),
        @ApiResponse(responseCode = "204", description = "조회할 친구가 존재하지 않음")
    })
	@GetMapping("/friends")
	public ResponseEntity<Map<String, Object>> getFriendList(@RequestParam("userNo") int userNo) {
	    log.info("친구 목록 조회 요청 도착: userNo = {}", userNo);

	    List<UserVO> friendList = myPageService.getFriendList(userNo);
	    Map<String, Object> res = new HashMap<>();

	    if (friendList == null || friendList.isEmpty()) {
	        res.put("res_code", "204");
	        res.put("res_msg", "친구가 없습니다.");
	        res.put("data", Collections.emptyList());
	    } else {
	        res.put("res_code", "200");
	        res.put("res_msg", "친구 목록 조회 성공");
	        res.put("data", friendList);
	    }

	    return ResponseEntity.ok(res);
	}
	
	// 다른 사용자의 친구 목록 조회
	@Operation(summary = "다른 사용자의 친구 목록 조회", description = "다른 사용자의 친구 목록을 로그인한 사용자의 번호를 통해 조회합니다.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공"),
        @ApiResponse(responseCode = "204", description = "조회할 친구가 존재하지 않음")
    })
	@GetMapping("/otherFriends")
	public ResponseEntity<Map<String, Object>> getOtherFriendList(
	        @RequestParam("targetUserNo") int targetUserNo,
	        @RequestParam("loginUserNo") int loginUserNo) {

	    log.info("다른 사용자의 친구 목록 조회 요청: targetUserNo = {}, loginUserNo = {}", targetUserNo, loginUserNo);

	    List<FriendDTO> friendList = myPageService.getOtherFriendList(targetUserNo, loginUserNo);
	    Map<String, Object> res = new HashMap<>();
	    if (friendList == null || friendList.isEmpty()) {
	        res.put("res_code", "204");
	        res.put("res_msg", "친구가 없습니다.");
	        res.put("data", Collections.emptyList());
	    }else {	    	
	    	res.put("res_code", "200");
	    	res.put("res_msg", "다른 사용자의 친구 목록 조회 성공");
	    	res.put("data", friendList);
	    }
	    return ResponseEntity.ok(res);
	}
	
	// 친구 검색
	@Operation(summary = "친구 검색", description = "키워드를 기반으로 친구를 검색합니다.")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "친구 검색 성공"),
        @ApiResponse(responseCode = "404", description = "친구 검색 실패")
    })
	@GetMapping("/search")
	public ResponseEntity<Map<String, Object>> searchFriends(@RequestParam("keyword") String keyword,
			@RequestParam("loginUserNo") int loginUserNo) {
	    log.info("친구 검색 요청 도착. keyword = {}", keyword);

	    FriendDTO result = myPageService.searchFriendByKeyword(keyword, loginUserNo);

	    Map<String, Object> response = new HashMap<>();
	    
	    
	    if (result != null) {
	        response.put("res_code", "200");
	        response.put("res_msg", "친구 검색 성공");
	        response.put("data", result);
	        return ResponseEntity.ok(response);
	    } else {
	        response.put("res_code", "404");
	        response.put("res_msg", "해당하는 사용자를 찾을 수 없습니다.");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }
	}
	// 친구 신청 요청 
	@Operation(summary = "친구 신청", description = "다른 사용자에게 친구 신청을 보냅니다.")
	@PostMapping("/request")
	public ResponseEntity<Map<String, Object>> requestFriend(@RequestBody Map<String, Object> requestBody) {
	    Map<String, Object> response = new HashMap<>();
	    
	    String senderId = String.valueOf(requestBody.get("requesterNo"));
	    String receiverId = String.valueOf(requestBody.get("requestedNo"));

	    // userNo 추출
	    // 친구 요청 보내는 사람 userno
	    Integer requesterNo = (Integer) requestBody.get("requesterNo");
	    // 친구 요청 받는 사람 userno
	    Integer requestedNo = (Integer) requestBody.get("requestedNo");
	    log.info("친구 요청자 정보 도착: requesterNo = {}", requesterNo);
	    log.info("친구 요청받는 사람 정보 도착: requestedNo = {}", requestedNo);
	 
	    if (requesterNo == null || requestedNo == null) {
	        response.put("res_code", "400");
	        response.put("res_msg", "요청 정보가 부족합니다.");
	        return ResponseEntity.badRequest().body(response);
	    }
	    String senderUserName = myPageDAO.findUserNameByUserNo(requesterNo);
	    boolean success = myPageService.requestFriend(requesterNo, requestedNo);
	    if (success) {
	    	websocket.sendFriendRequestAlarm(
	    		    "FRIEND_REQUEST",
	    		    senderId,
	    		    senderUserName,
	    		    receiverId
	    		);

	        response.put("res_code", "200");
	        response.put("res_msg", "친구 요청이 완료되었습니다.");
	        return ResponseEntity.ok(response);
	    } else {
	        response.put("res_code", "500");
	        response.put("res_msg", "친구 요청 처리 중 오류가 발생했습니다.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	// 받은 신청 목록
	@Operation(summary = "받은 친구 신청 목록", description = "로그인 사용자가 받은 친구 요청 목록을 조회합니다.")
	@GetMapping("/pending")
	public ResponseEntity<Map<String, Object>> getPendingRequests(@RequestParam("userNo") int userNo) {
	    List<UserVO> pendingList = myPageService.getPendingRequests(userNo);

	    Map<String, Object> response = new HashMap<>();
	    
	    if (pendingList == null || pendingList.isEmpty()) {
	        response.put("res_code", "204"); // 204 No Content 의미로 사용 가능
	        response.put("res_msg", "받은 친구 요청이 없습니다.");
	        response.put("data", Collections.emptyList());
	        return ResponseEntity.ok(response); // 또는 .status(HttpStatus.NO_CONTENT).body(response) 도 가능
	    }
	    
	    response.put("res_code", "200");
	    response.put("res_msg", "받은 친구 요청 목록");
	    response.put("data", pendingList);

	    return ResponseEntity.ok(response);
	}
	
	// 친구 요청 수락 
	@PostMapping("/accept")
	@Operation(summary = "친구 요청 수락", description = "수신한 친구 요청을 수락합니다.")
	public ResponseEntity<Map<String, Object>> acceptFriend(@RequestBody Map<String, Integer> body) {
	    int requesterNo = body.get("requesterNo");
	    int receiverNo = body.get("requestedNo");

	    boolean success = myPageService.acceptFriendRequest(requesterNo, receiverNo);

	    Map<String, Object> res = new HashMap<>();
	    if (success) {
	        res.put("res_code", "200");
	        res.put("res_msg", "친구 요청을 수락했습니다.");
	    } else {
	        res.put("res_code", "500");
	        res.put("res_msg", "요청 수락에 실패했습니다.");
	    }
	    return ResponseEntity.ok(res);
	}

	// 친구 요청 거절
	@Operation(summary = "친구 요청 거절", description = "수신한 친구 요청을 거절합니다.")
	@PostMapping("/reject")
	public ResponseEntity<Map<String, Object>> rejectFriend(@RequestBody Map<String, Integer> body) {
	    int requesterNo = body.get("requesterNo");
	    int receiverNo = body.get("requestedNo");

	    boolean success = myPageService.rejectFriendRequest(requesterNo, receiverNo);

	    Map<String, Object> res = new HashMap<>();
	    if (success) {
	        res.put("res_code", "200");
	        res.put("res_msg", "친구 요청을 거절했습니다.");
	    } else {
	        res.put("res_code", "500");
	        res.put("res_msg", "요청 거절에 실패했습니다.");
	    }
	    return ResponseEntity.ok(res);
	}
	
	// 프로필 정보 수정
	@Operation(summary = "프로필 정보 수정", description = "회원 번호를 통해 프로필 정보를 수정합니다.")
	@PostMapping("/profileUpdate")
	public ResponseEntity<Map<String, Object>> profileUpdate(
			@RequestParam("userNo") int userNo,
	        @RequestBody UserVO member
	) {
		Map<String, Object> map = new HashMap<>();
		
		log.info("수정 요청된 회원 번호: {}", userNo);
		log.info("수정 요청된 정보: {}", member);
		if (member.getStyleCode() != null && member.getStyleCode() == 0) {
		    member.setStyleCode(null);
		}
		if (member.getStatCode() != null && member.getStatCode() == 0) {
		    member.setStatCode(null);
		}
		if (member.getUserComment() != null && member.getUserComment().trim().isEmpty()) {
		    member.setUserComment(null);
		}
		member.setUserNo(userNo);

		int result = myPageService.updateProfile(member);

		if (result == 0) {
			map.put("res_code", "400");
			map.put("res_msg", "회원 정보 수정 중 오류가 발생하였습니다.");
			return ResponseEntity.badRequest().body(map);
		} else {
			map.put("res_code", "200");
			map.put("res_msg", "회원 정보 수정에 성공하였습니다.");
			return ResponseEntity.ok(map);
		}
	}

}
