package com.msa.kyj_prj.member;

public enum LoginStatus {
	SUCCESS,        // 로그인 성공
    NO_USER,        // 회원 없음
    FAIL_CREDENTIALS, // 비밀번호 불일치
    FAIL_LOCKED     // 계정 잠금
}
