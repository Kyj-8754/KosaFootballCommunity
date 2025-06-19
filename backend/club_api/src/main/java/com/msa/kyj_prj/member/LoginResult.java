package com.msa.kyj_prj.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {
	private LoginStatus status;  // 처리 결과 코드
    private Member      member;  // 로그인 성공 시 채워지는 Member
    private int         failCount; // (선택) 실패 누적 횟수
}
