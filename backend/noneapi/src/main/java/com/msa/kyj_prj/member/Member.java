package com.msa.kyj_prj.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class Member {
	private String userid;		//아이디
	private String passwd;		//패스워드
	private String name;		//이름
	private String loginTime;	//로그인 기록
	private char is_deleted;	//회원 탈퇴시 Y, N저장
	private String deleted_at;	//회원 탈퇴 기록
	private String phone_no;	//핸드폰
	private String postcode;	//우편번호
	private String roadaddress;	//도로주소
	private String jibunaddress;//지번
	private String detail_add;	//상세주소
	private int fail_login;		//로그인실패기록
	private String supervisor;	//관리자판단
	private String regi_date;	//회원등록일
	private String birthdate;	//생년월일
	
	
	public boolean isValid() {
		if (userid == null || userid.length() == 0) return false;
		if (passwd == null || passwd.length() == 0) return false;
		if (name == null || name.length() == 0) return false;
		return true;
	}
}
