package com.msa.kyj_prj.member;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.msa.kyj_prj.util.Util;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("member")
public class MemberController {

	@Autowired
	LoginService loginService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
		// 유저 가입
		// 비동기 처리
		// json으로 받은 값 rest api로 처리
		@PostMapping("regist")
		public boolean postRegist(@RequestBody Member member) {
			String raw = member.getPasswd();
		    String hash = passwordEncoder.encode(raw);
		    member.setPasswd(hash);
			
			int result = loginService.registForm(member);
			System.out.println("insert 결과: " + result);
			return result == 1;
		}

		// 로그인 처리
		@PostMapping("login")
		public Map<String, Object> login(@RequestBody Member member) {
			LoginResult member_result = loginService.login(member.getUserid(), member.getPasswd());
			Map<String, Object> result = new HashMap<String, Object>();
			int MAX_FAILS = 5;
			
			switch (member_result.getStatus()) {
		      case SUCCESS:
		        result.put("status", "SUCCESS");
		        result.put("supervisor", member_result.getMember().getSupervisor());
		        result.put("message", member_result.getMember().getName() +  "님 환영합니다.");
		        break;
		      case NO_USER:
		        result.put("status", "NO_USER");
		        result.put("message", "등록된 회원이 아닙니다.");
		        break;
		      case FAIL_CREDENTIALS:
		        result.put("status", "FAIL_CREDENTIALS");
		        result.put("message", String.format(
		          "비밀번호 %d회 실패, %d회 남았습니다.",
		          member_result.getFailCount(), MAX_FAILS - member_result.getFailCount()
		        ));
		        break;
		      case FAIL_LOCKED:
		        result.put("status", "FAIL_LOCKED");
		        result.put("message", "계정이 잠금되었습니다.");
		        break;
		    }
			
			return result;
		}
		
		
		// 유저 상세창
		@RequestMapping("detailView")
		public Map<String, Object> detailView(String userid) {
			Map<String, Object> result = new HashMap<>();
			Member memberDB = loginService.getMember(userid);
			if (memberDB == null) {
				return result;
			}
			result.put("memberDB", memberDB);
			return result; 
			}
		
		
		//유저 업데이트
		@PostMapping("update")
		public Map<String, Object> update(@RequestBody Map<String, Object> param){
			Map<String, Object> result = new HashMap<String, Object>();
			//전달받은 json 반환
			String userid = (String)param.get("userid");
			String passwd = (String)param.get("passwd");
			String newPasswd = (String)param.get("newPasswd");
			
			
			//패스워드 기존값과 같은지 비교, 로그인 서비스 재활용
			LoginResult member_result = loginService.login(userid, passwd);
			
			// 실패하면 다시 리턴
			if(member_result.getStatus() != LoginStatus.SUCCESS) {
				result.put("error", true);
				result.put("message", "기존 비밀번호가 틀림니다.");
				return result;
			}
			
			// 비밀번호 수정하기 시작
			// 비밀번호 해시처리
		    String hash = passwordEncoder.encode(newPasswd);
		    
		    
		    Member reMember = member_result.getMember();
		    reMember.setPasswd(hash);
			// 업데이트 시작
			
			// 업데이트 완료 확인
			if(!loginService.update(reMember)) {
				result.put("error", true);
				result.put("message", "수정 실패하였습니다..");
				return result;
			}
			result.put("error", false);
			result.put("message", "수정 완료되었습니다 다시 로그인 해 주세요.");
			
			return result;
			}
	
		

		// userid 중복확인
		@PostMapping("isExistUserId")
	    public Map<String, Object> isExistUserId(@RequestBody Member member) {
			Map<String, Object> map = new HashMap<String, Object>();
			Member memberInfo = loginService.getMember(member.getUserid());
			
			map.put("existUserId", memberInfo != null);
			return map;
	    }
		
		
		// 유저 삭제
		@PostMapping("unregister")
		public Map<String, Object> delete(@RequestBody Map<String, Object> params) {
			Map<String, Object> result = new HashMap<String, Object>();
			String userid = (String) params.get("userid");
			String sotreUserid = (String) params.get("sotreUserid");
			
		    if (!userid.equals(sotreUserid)) {
		        result.put("error", true);
		        result.put("message", "로그인이 필요합니다.");
		        return result;
		    }

		    boolean success = loginService.delete(userid);

		    if (success) {
		        result.put("error", false);
		        result.put("message", "메인 화면으로 이동합니다.");
		    } else {
		        result.put("error", true);
		        result.put("message", "회원 탈퇴 처리 실패");
		    }
			return result;
		}
		
		
		// 유저 리스트
		@GetMapping("list")
		public Map<String, Object> list(String pageNo, String size, String searchValue) {
			Map<String, Object>result = new HashMap<>();
			
			result.put("pageResponse",loginService.list(searchValue,
					Util.parseInt(pageNo, 1),
					Util.parseInt(size, 10)
					));
			return result;
		}
		
		//유저 밴처리
		@PostMapping("ban")
		public Map<String, Object> ban(@RequestBody Map<String, String> memberban){
			Map<String, Object> result = new HashMap<String, Object>();
			
			System.out.println(memberban.get("userid"));
			 // 세션에 관리자 판단 여부 로직
			if (memberban.get("supervisor") == null || !memberban.get("supervisor").equals("Y")) {
				result.put("success",false);
				result.put("message", "관리자 권한이 필요합니다.");
				return result;
		    }
			
			// 회원 DB
			// 1. 유저 상태 조회, 탈퇴된 회원이면 존재하지않는다고 노출
		    Member targetUser = loginService.getMember(memberban.get("userid"));
		    if (targetUser == null || targetUser.getIs_deleted() == 'Y') {
		        result.put("success", false);
		        result.put("message", "해당 유저가 존재하지 않습니다.");
		        return result;
		    }
			// 회원 밴 처리
		    if(memberban.get("banned").equals("Y")) {
		    	loginService.ban(memberban.get("userid"));
		    	result.put("success", true);
		    	result.put("message", "해당 유저 밴 처리에 성공하였습니다.");
		    }else if(memberban.get("banned").equals("N")) {
		    	loginService.unban(memberban.get("userid"));
		    	result.put("success", true);
		    	result.put("message", "해당 유저 해제 처리에 하였습니다.");
		    }
		    else{
		    	result.put("success", false);
		    	result.put("message", "알 수 없는 오류로 처리 실패했습니다.");
		    }
			
			return result;
			}
		
		
		
		// 유저 아이디 찾기
		@PostMapping("findMemberId")
		public Map<String, Object> findMemberId(@RequestBody Map<String, String> param) {
			Map<String, Object> result = new HashMap<>();
			
			String username = param.get("username");
			String phone_no = param.get("phone_no");
			String userid = param.get("userid");
		   
			Member memberDB = loginService.findMember(username, phone_no, userid);
			
			if (memberDB == null) {
				result.put("existUserId", false);
			return result;
			}
		
			result.put("existUserId", true);
			result.put("userid", memberDB.getUserid());
			return result; 
		}
		
		
		// 유저 아이디 찾기
		@PostMapping("reMemberPw")
		public Map<String, Object> reMemberPw(@RequestBody Map<String, String> param) {
			Map<String, Object> status = new HashMap<>();
			
			String userid = param.get("userid");
			String repasswd = param.get("repasswd");
		   
			String hash = passwordEncoder.encode(repasswd);
			
			boolean result = loginService.rePasswd(userid, hash);
			
			if (result) {
				status.put("existUserId", true);
				return status;
			}
		
				status.put("existUserId", false);
			return status; 
			}
		
}
